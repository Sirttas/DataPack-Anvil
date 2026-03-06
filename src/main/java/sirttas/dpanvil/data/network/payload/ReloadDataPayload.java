package sirttas.dpanvil.data.network.payload;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
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
		List<SubPayload<?, ?>> messages
) implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<@NotNull ReloadDataPayload> TYPE = PayloadHelper.createType("reload_data");
	public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull ReloadDataPayload> STREAM_CODEC = StreamCodec.of((b, p) -> p.write(b), ReloadDataPayload::new);

	public ReloadDataPayload(Collection<ResourceKey<@NotNull IDataManager<?>>> managers) {
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
		}));
	}

	@Override
	public @NotNull Type<@NotNull ReloadDataPayload> type() {
		return TYPE;
	}

	private record SubPayload<T, I>(
			ResourceKey<@NotNull IDataManager<T>> key,
			IDataManager<T> manager,
			IJsonDataSerializer<T, I> serializer,
			Map<Identifier, T> data,
			Map<Identifier, I> intermediateData
	) {

		public static <T, I> SubPayload<T, I> load(FriendlyByteBuf buf) {
			return create(IDataManager.createManagerKey(buf.readIdentifier()), (k, s) -> {
				try {
					var mapSize = buf.readInt();
					var data = HashMap.<Identifier, I>newHashMap(mapSize);

					for (int i = 0; i < mapSize; i++) {
						data.put(buf.readIdentifier(), s.read(buf));
					}
					return Map.copyOf(data);
				} catch (Exception e) {
					throw new IllegalStateException("Error while decoding network packet for DataManger " + k, e);
				}
			});
		}

		@SuppressWarnings("unchecked")
		public static <T, I> SubPayload<T, I> create(ResourceKey<? super IDataManager<T>> key, BiFunction<ResourceKey<@NotNull IDataManager<T>>, IJsonDataSerializer<T, I>, Map<Identifier, I>> dataBuilder) {
			ResourceKey<@NotNull IDataManager<T>> k = (ResourceKey<@NotNull IDataManager<T>>) key;
			IDataManager<T> manager = DataPackAnvil.WRAPPER.getManager(key);
			IJsonDataSerializer<T, I> serializer = DataPackAnvil.WRAPPER.getSerializer(key);
			Map<Identifier, T> data = Map.copyOf(manager.getData());
			Map<Identifier, I> intermediateData = dataBuilder.apply(k, serializer);

			return new SubPayload<>(k, manager, serializer, data, intermediateData);
		}

		public void write(FriendlyByteBuf buf) {
			buf.writeIdentifier(key.identifier());
			buf.writeInt(data.size());
			data.forEach((loc, prop) -> encodeSingleData(buf, loc, prop));
		}

		private void encodeSingleData(FriendlyByteBuf buf, Identifier loc, T prop) {
			try {
				buf.writeIdentifier(loc);
				serializer.write(prop, buf);
			} catch (Exception e) {
				throw new IllegalStateException("Error while encoding network packet for DataManger " + key + ", " + loc + " has invalid data", e);
			}
		}

		public void handle() {
			try {
				var newData = new HashMap<>(manager.getData());

				for (Map.Entry<Identifier, I> entry : intermediateData.entrySet()) {
					newData.put(entry.getKey(), serializer.read(entry.getValue()));
				}
				manager.setData(newData);
			} catch (Exception e) {
				DataManagerWrapper.logManagerException(key, e);
			}
		}
	}
}
