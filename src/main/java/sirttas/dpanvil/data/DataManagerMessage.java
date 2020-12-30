package sirttas.dpanvil.data;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.api.data.DataManager;

public class DataManagerMessage<T> {

	private ResourceLocation id;
	private DataManager<T> manager;
	private Map<ResourceLocation, T> data;

	public DataManagerMessage(ResourceLocation id) {
		this.id = id;
		this.manager = DataManagers.getManager(id);
		this.data = manager.getData();
	}

	public void decode(PacketBuffer buf) {
		int mapSize = buf.readInt();

		data = new HashMap<>(mapSize);
		for (int i = 0; i < mapSize; i++) {
			data.put(buf.readResourceLocation(), manager.getSerializer().read(buf));
		}
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(data.size());
		data.forEach((loc, prop) -> {
			buf.writeResourceLocation(loc);
			manager.getSerializer().write(prop, buf);
		});
	}

	public void process() {
		manager.setData(data);
	}

	public ResourceLocation getId() {
		return id;
	}
}