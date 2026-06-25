package sirttas.dpanvil.data.network.payload;

import io.netty.buffer.Unpooled;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.data.DataHandler;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.data.serializer.IJsonDataSerializer;
import sirttas.dpanvil.registry.RegistryListener;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public record ReloadDataPayload(
		List<SubPayload<?, ?>> messages) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<ReloadDataPayload> TYPE = PayloadHelper.createType("reload_data");
	public static final StreamCodec<FriendlyByteBuf, ReloadDataPayload> STREAM_CODEC = StreamCodec
			.of((b, p) -> p.write(b), ReloadDataPayload::new);

	public ReloadDataPayload(Collection<ResourceKey<IDataManager<?>>> managers) {
		this(managers.stream()
				.<SubPayload<?, ?>>map(m -> SubPayload.create(m, (k, s) -> Collections.emptyMap()))
				.toList());
	}

	public ReloadDataPayload(FriendlyByteBuf buf) {
		this(Util.make(() -> {
			List<SubPayload<?, ?>> messages = new java.util.ArrayList<>();
			int size = buf.readInt();

			for (int i = 0; i < size; i++) {
				messages.add(SubPayload.load(buf));
			}
			return List.copyOf(messages);
		}));
	}

	public void write(FriendlyByteBuf buf) {
		buf.writeInt(messages.size());
		for (SubPayload<?, ?> message : messages) {
			message.write(buf);
		}
		DataPackAnvilApi.LOGGER.debug("Sending DataPack Anvil packet with size: {} bytes", buf::writerIndex);
	}

	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(() -> RegistryListener.getInstance().listen(r -> {
			messages.forEach(SubPayload::handle);
			DataHandler.onDPAnvilUpdate();
		}));
	}

	@Override
	public @NotNull Type<ReloadDataPayload> type() {
		return TYPE;
	}

	private record SubPayload<T, I>(
			ResourceKey<IDataManager<T>> key,
			IDataManager<T> manager,
			IJsonDataSerializer<T, I> serializer,
			Map<ResourceLocation, T> data,
			Map<ResourceLocation, I> intermediateData) {

		public static <T, I> SubPayload<T, I> load(FriendlyByteBuf buf) {
			return create(IDataManager.createManagerKey(buf.readResourceLocation()), (k, s) -> {
				try {
					var mapSize = buf.readInt();
					var data = HashMap.<ResourceLocation, I>newHashMap(mapSize);

					for (int i = 0; i < mapSize; i++) {
						data.put(buf.readResourceLocation(), s.read(buf));
					}
					return Map.copyOf(data);
				} catch (Exception e) {
					throw new IllegalStateException("Error while decoding network packet for DataManger " + k, e);
				}
			});
		}

		@SuppressWarnings("unchecked")
		public static <T, I> SubPayload<T, I> create(ResourceKey<? super IDataManager<T>> key,
				BiFunction<ResourceKey<IDataManager<T>>, IJsonDataSerializer<T, I>, Map<ResourceLocation, I>> dataBuilder) {
			ResourceKey<IDataManager<T>> k = (ResourceKey<IDataManager<T>>) key;
			IDataManager<T> manager = DataPackAnvil.WRAPPER.getManager(key);
			IJsonDataSerializer<T, I> serializer = DataPackAnvil.WRAPPER.getSerializer(key);
			Map<ResourceLocation, T> data = Map.copyOf(manager.getData());
			Map<ResourceLocation, I> intermediateData = dataBuilder.apply(k, serializer);

			return new SubPayload<>(k, manager, serializer, data, intermediateData);
		}

		public void write(FriendlyByteBuf buf) {
			buf.writeResourceLocation(key.location());
			var survivors = new java.util.ArrayList<FriendlyByteBuf>(data.size());

			data.forEach((loc, prop) -> {
				FriendlyByteBuf encoded = encodeSingleData(buf, loc, prop);
				if (encoded != null) {
					survivors.add(encoded);
				}
			});

			buf.writeInt(survivors.size());
			for (FriendlyByteBuf survivor : survivors) {
				buf.writeBytes(survivor);
				survivor.release();
			}
		}

		private FriendlyByteBuf encodeSingleData(FriendlyByteBuf buf, ResourceLocation loc, T prop) {
			FriendlyByteBuf scratch = buf instanceof RegistryFriendlyByteBuf rbuf
					? new RegistryFriendlyByteBuf(Unpooled.buffer(), rbuf.registryAccess())
					: new FriendlyByteBuf(Unpooled.buffer());

			try {
				scratch.writeResourceLocation(loc);
				serializer.write(prop, scratch);
				return scratch;
			} catch (Exception e) {
				scratch.release();
				DataPackAnvilApi.LOGGER.warn("Skipping {} in DataManager {} during network sync, failed to encode: {}",
						loc, key, e.getMessage());
				return null;
			}
		}

		public void handle() {
			try {
				var newData = new HashMap<>(manager.getData());

				for (Map.Entry<ResourceLocation, I> entry : intermediateData.entrySet()) {
					newData.put(entry.getKey(), serializer.read(entry.getValue()));
				}
				manager.setData(newData);
			} catch (Exception e) {
				DataManagerWrapper.logManagerException(key, e);
			}
		}
	}
}
