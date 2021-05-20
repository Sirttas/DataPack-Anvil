package sirttas.dpanvil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
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
import sirttas.dpanvil.data.network.proxy.ClientProxy;
import sirttas.dpanvil.data.network.proxy.IProxy;
import sirttas.dpanvil.data.network.proxy.ServerProxy;
import sirttas.dpanvil.tag.DataTagManager;

@Mod(DataPackAnvilApi.MODID)
public class DataPackAnvil {
	
	public static final DataManagerWrapper WRAPPER = new DataManagerWrapper();
	public static final DataTagManager DATA_TAG_MANAGER = new DataTagManager();
	public static final DPAnvilAnnotationProcessor ANNOTATION_PROCESSOR = new DPAnvilAnnotationProcessor();
	public static final IProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	
	public DataPackAnvil() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::serverStarted);
		MinecraftForge.EVENT_BUS.addListener(this::playerLogin);
		MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);
		PROXY.registerHandlers();
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

	private void serverStarted(FMLServerStartedEvent event) {
		MinecraftServer server = event.getServer();

		MinecraftForge.EVENT_BUS.post(new DataPackReloadCompletEvent(server.getRecipeManager(), server.func_244266_aF(), DataPackAnvil.WRAPPER.getDataManagers()));
	}

	private void processIMC(InterModProcessEvent event) {
		event.getIMCStream(DataManagerIMC.METHOD::equals).forEach(message -> WRAPPER.putManagerFromIMC(message.getMessageSupplier()));
		event.getIMCStream(DataTagIMC.METHOD::equals).forEach(message -> DATA_TAG_MANAGER.putTagRegistryFromIMC(message.getMessageSupplier()));
	}

	private void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		PlayerEntity player = event.getPlayer();

		if (player instanceof ServerPlayerEntity) {
			MessageHelper.sendToRemotePlayer((ServerPlayerEntity) player, new ReloadDataMessage(DataPackAnvil.WRAPPER.ids()));
		}
	}

	private void addReloadListeners(AddReloadListenerEvent event) {
		if (!WRAPPER.getDataManagers().isEmpty()) {
			event.addListener(WRAPPER);
		}
	}

}
