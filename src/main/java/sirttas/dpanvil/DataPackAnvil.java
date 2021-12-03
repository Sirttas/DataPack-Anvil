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
import sirttas.dpanvil.annotation.DPAnvilAnnotationProcessor;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.event.DataPackReloadCompletEvent;
import sirttas.dpanvil.api.imc.DataManagerIMC;
import sirttas.dpanvil.api.imc.DataTagIMC;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.data.network.message.MessageHandler;
import sirttas.dpanvil.data.network.message.MessageHelper;
import sirttas.dpanvil.data.network.message.ReloadDataMessage;
import sirttas.dpanvil.tag.DataTagManager;

@Mod(DataPackAnvilApi.MODID)
public class DataPackAnvil {
	
	public static final DataManagerWrapper WRAPPER = new DataManagerWrapper();
	public static final DataTagManager DATA_TAG_MANAGER = new DataTagManager();
	public static final DPAnvilAnnotationProcessor ANNOTATION_PROCESSOR = new DPAnvilAnnotationProcessor();
	
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
		ANNOTATION_PROCESSOR.setup();
	}

	private void serverStarted(ServerStartedEvent event) {
		onReloadComplet(event.getServer());
	}

	private void processIMC(InterModProcessEvent event) {
		event.getIMCStream(DataManagerIMC.METHOD::equals).forEach(message -> WRAPPER.putManagerFromIMC(message.messageSupplier()));
		event.getIMCStream(DataTagIMC.METHOD::equals).forEach(message -> DATA_TAG_MANAGER.putTagRegistryFromIMC(message.messageSupplier()));
	}

	private void syncDataManagers(OnDatapackSyncEvent event) {
		var message = new ReloadDataMessage(DataPackAnvil.WRAPPER.ids());
		var player = event.getPlayer();

		if (player != null) {
			MessageHelper.sendToRemotePlayer(player, message);
		} else {
			MessageHelper.sendToAllRemotePlayers(message);
			onReloadComplet(event.getPlayerList().getServer());
		}
	}
	
	private static void onReloadComplet(MinecraftServer server) {
		MinecraftForge.EVENT_BUS.post(new DataPackReloadCompletEvent(server.getRecipeManager(), server.getTags(), DataPackAnvil.WRAPPER.getDataManagers()));
	}

	private void addReloadListeners(AddReloadListenerEvent event) {
		if (!WRAPPER.getDataManagers().isEmpty()) {
			event.addListener(WRAPPER);
		}
	}

}
