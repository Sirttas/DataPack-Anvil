package sirttas.dpanvil.data.network.payload;

import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.ConfigurationPayloadContext;
import net.neoforged.neoforge.network.handling.ISynchronizedWorkHandler;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
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
		List<SubPayload<?, ?>> messages
) implements CustomPacketPayload {

	public static final ResourceLocation ID = DataPackAnvilApi.createRL("reload_data");

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

	@Override
	public void write(FriendlyByteBuf buf) {
		buf.writeInt(messages.size());
		for (SubPayload<?, ?> message : messages) {
			message.write(buf);
		}
		DataPackAnvilApi.LOGGER.debug("Sending DataPack Anvil packet with size: {} bytes", buf::writerIndex);
	}

	@Override
	public @NotNull ResourceLocation id() {
		return ID;
	}

	public void handle(PlayPayloadContext ctx) {
		handle(ctx.workHandler());
	}

	public void handle(ConfigurationPayloadContext ctx) {
		handle(ctx.workHandler());
	}

	private void handle(ISynchronizedWorkHandler workHandler) {
		workHandler.execute(() -> RegistryListener.getInstance().listen(r -> {
			messages.forEach(SubPayload::handle);
			DataHandler.onDPAnvilUpdate();
		}));
	}

	private record SubPayload<T, I>(
			ResourceKey<IDataManager<T>> key,
			IDataManager<T> manager,
			IJsonDataSerializer<T, I> serializer,
			Map<ResourceLocation, T> data,
			Map<ResourceLocation, I> intermediateData
	) {

		public static <T, I> SubPayload<T, I> load(FriendlyByteBuf buf) {
			return create(IDataManager.createManagerKey(buf.readResourceLocation()), (k, s) -> {
				try {
					var mapSize = buf.readInt();
					var data = new HashMap<ResourceLocation, I>(mapSize);

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
		public static <T, I> SubPayload<T, I> create(ResourceKey<? super IDataManager<T>> key, BiFunction<ResourceKey<IDataManager<T>>, IJsonDataSerializer<T, I>, Map<ResourceLocation, I>> dataBuilder) {
			ResourceKey<IDataManager<T>> k = (ResourceKey<IDataManager<T>>) key;
			IDataManager<T> manager = DataPackAnvil.WRAPPER.getManager(key);
			IJsonDataSerializer<T, I> serializer = DataPackAnvil.WRAPPER.getSerializer(key);
			Map<ResourceLocation, T> data = Map.copyOf(manager.getData());
			Map<ResourceLocation, I> intermediateData = dataBuilder.apply(k, serializer);

			return new SubPayload<>(k, manager, serializer, data, intermediateData);
		}

		public void write(FriendlyByteBuf buf) {
			buf.writeResourceLocation(key.location());
			buf.writeInt(data.size());
			data.forEach((loc, prop) -> encodeSingleData(buf, loc, prop));
		}

		private void encodeSingleData(FriendlyByteBuf buf, ResourceLocation loc, T prop) {
			try {
				buf.writeResourceLocation(loc);
				serializer.write(prop, buf);
			} catch (Exception e) {
				throw new IllegalStateException("Error while encoding network packet for DataManger " + key + ", " + loc + " has invalid data", e);
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
