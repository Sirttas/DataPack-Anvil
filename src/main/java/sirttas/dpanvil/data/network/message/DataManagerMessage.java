package sirttas.dpanvil.data.network.message;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.event.DataManagerReloadEvent;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.data.serializer.IJsonDataSerializer;

public class DataManagerMessage<T> {

	private final ResourceLocation id;
	private final IDataManager<T> manager;
	private final IJsonDataSerializer<T> serializer;
	private Map<ResourceLocation, T> data;

	public DataManagerMessage(ResourceLocation id) {
		this.id = id;
		this.manager = DataPackAnvil.WRAPPER.getManager(id);
		this.serializer = DataPackAnvil.WRAPPER.getSerializer(id);
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
			DataPackAnvilApi.LOGGER.error(() -> "Error while decoding network packet for datamanger " + id, e);
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
			MinecraftForge.EVENT_BUS.post(new DataManagerReloadEvent<>(manager));
		} catch (Exception e) {
			DataManagerWrapper.logManagerException(id, e);
		}
	}

	public ResourceLocation getId() {
		return id;
	}
}