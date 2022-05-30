package sirttas.dpanvil;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.event.DataPackReloadCompleteEvent;
import sirttas.dpanvil.api.imc.DataManagerIMC;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.data.network.message.MessageHandler;
import sirttas.dpanvil.data.network.message.MessageHelper;
import sirttas.dpanvil.data.network.message.ReloadDataMessage;

@Mod(DataPackAnvilApi.MODID)
public class DataPackAnvil {
	
	public static final DataManagerWrapper WRAPPER = new DataManagerWrapper();
	
	public DataPackAnvil() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::serverStarted);
		MinecraftForge.EVENT_BUS.addListener(this::syncDataManagers);
		MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
	}

	public static ResourceLocation createRL(String name) {
		if (name.contains(":")) {
			return new ResourceLocation(name);
		}
		return new ResourceLocation(DataPackAnvilApi.MODID, name);
	}

	private void setup(FMLCommonSetupEvent event) {
		MessageHandler.setup();
	}

	private void serverStarted(ServerStartedEvent event) {
		onReloadCompleted(event.getServer());
	}

	private void processIMC(InterModProcessEvent event) {
		event.getIMCStream(DataManagerIMC.METHOD::equals).forEach(message -> WRAPPER.putManagerFromIMC(message.messageSupplier()));
	}

	private void syncDataManagers(OnDatapackSyncEvent event) {
		var message = new ReloadDataMessage(DataPackAnvil.WRAPPER.ids());
		var player = event.getPlayer();

		if (player != null) {
			MessageHelper.sendToRemotePlayer(player, message);
		} else {
			MessageHelper.sendToAllRemotePlayers(message);
			onReloadCompleted(event.getPlayerList().getServer());
		}
	}
	
	private static void onReloadCompleted(MinecraftServer server) {
		MinecraftForge.EVENT_BUS.post(new DataPackReloadCompleteEvent(server.getRecipeManager(), DataPackAnvil.WRAPPER.getDataManagers()));
	}

	private void addReloadListeners(AddReloadListenerEvent event) {
		if (!WRAPPER.getDataManagers().isEmpty()) {
			event.addListener(WRAPPER);
		}
	}

}
