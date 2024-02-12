package sirttas.dpanvil;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.remap.RemapKeys;
import sirttas.dpanvil.api.event.DataPackReloadCompleteEvent;
import sirttas.dpanvil.api.imc.DataManagerIMC;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.data.network.payload.PayloadHelper;
import sirttas.dpanvil.data.network.payload.ReloadDataPayload;

@Mod(DataPackAnvilApi.MODID)
public class DataPackAnvil {
	
	public static final DataManagerWrapper WRAPPER = new DataManagerWrapper();
	
	public DataPackAnvil(IEventBus modBus) {
		BlockPosPredicateType.register(modBus);

		modBus.addListener(this::processIMC);
		NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST, this::serverStarted);
		NeoForge.EVENT_BUS.addListener(this::syncDataManagers);
		NeoForge.EVENT_BUS.addListener(this::addReloadListeners);

		// Preload the service
		DataPackAnvilApi.service();
	}

	private void serverStarted(ServerStartedEvent event) {
		onReloadCompleted(event.getServer());
	}

	private void processIMC(InterModProcessEvent event) {
		WRAPPER.putManagerFromIMC(() -> new DataManagerIMC<>(DataPackAnvilApi.REMAP_KEYS_MANAGER).withCodec(RemapKeys.CODEC));
		event.getIMCStream(DataManagerIMC.METHOD::equals).forEach(message -> WRAPPER.putManagerFromIMC(message.messageSupplier()));
	}

	private void syncDataManagers(OnDatapackSyncEvent event) {
		if (event.getPlayer() != null) {
			return;
		}

		PayloadHelper.sendToAllRemotePlayers(new ReloadDataPayload(DataPackAnvil.WRAPPER.ids()));
		onReloadCompleted(event.getPlayerList().getServer());
	}
	
	private static void onReloadCompleted(MinecraftServer server) {
		NeoForge.EVENT_BUS.post(new DataPackReloadCompleteEvent(server.getRecipeManager(), DataPackAnvil.WRAPPER.getDataManagers(), server.registryAccess()));
	}

	private void addReloadListeners(AddReloadListenerEvent event) {
		if (!WRAPPER.getDataManagers().isEmpty()) {
			event.addListener(WRAPPER);
		}
	}

}
