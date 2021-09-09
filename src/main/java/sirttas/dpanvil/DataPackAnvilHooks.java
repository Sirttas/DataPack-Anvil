package sirttas.dpanvil;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import sirttas.dpanvil.api.event.DataPackReloadCompletEvent;

public class DataPackAnvilHooks {

	private DataPackAnvilHooks() {}
	
	public static void onReloadComplet(MinecraftServer server) {
		MinecraftForge.EVENT_BUS.post(new DataPackReloadCompletEvent(server.getRecipeManager(), server.getTags(), DataPackAnvil.WRAPPER.getDataManagers()));
	}
	
}
