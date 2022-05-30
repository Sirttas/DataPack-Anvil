package sirttas.dpanvil.data.network.message;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.data.serializer.IJsonDataSerializer;

import java.util.HashMap;
import java.util.Map;

public class DataManagerMessage<T> {

	private final ResourceKey<IDataManager<T>> key;
	private final IDataManager<T> manager;
	private final IJsonDataSerializer<T> serializer;
	private Map<ResourceLocation, T> data;

	public DataManagerMessage(ResourceLocation id) {
		this(IDataManager.createManagerKey(id));
	}

	@SuppressWarnings("unchecked")
	public DataManagerMessage(ResourceKey<? super IDataManager<T>> key) {
		this.key = (ResourceKey<IDataManager<T>>) key;
		this.manager = DataPackAnvil.WRAPPER.getManager(key);
		this.serializer = DataPackAnvil.WRAPPER.getSerializer(key);
		this.data = manager.getData();
	}

	public void decode(FriendlyByteBuf buf) {
		try {
			int mapSize = buf.readInt();

			data = new HashMap<>(mapSize);
			for (int i = 0; i < mapSize; i++) {
				data.put(buf.readResourceLocation(), serializer.read(buf));
			}
		} catch (Exception e) {
			DataPackAnvilApi.LOGGER.error(() -> "Error while decoding network packet for DataManger " + key, e);
		}
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(data.size());
		data.forEach((loc, prop) -> {
			buf.writeResourceLocation(loc);
			serializer.write(prop, buf);
		});
	}

	public void process() {
		try {
			manager.setData(data);
		} catch (Exception e) {
			DataManagerWrapper.logManagerException(key, e);
		}
	}

	public ResourceLocation getId() {
		return key.location();
	}
}
