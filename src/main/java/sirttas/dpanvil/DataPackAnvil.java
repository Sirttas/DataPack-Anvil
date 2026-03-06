package sirttas.dpanvil;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.remap.RemapKeys;
import sirttas.dpanvil.api.imc.DataManagerIMC;
import sirttas.dpanvil.api.predicate.block.BlockPosPredicateType;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.data.network.payload.DPAnvilPacketDistributor;
import sirttas.dpanvil.data.network.payload.ReloadDataPayload;

@Mod(DataPackAnvilApi.MODID)
public class DataPackAnvil {

	public static final DataManagerWrapper WRAPPER = new DataManagerWrapper();
	
	public DataPackAnvil(IEventBus modBus) {
		BlockPosPredicateType.register(modBus);

		modBus.addListener(this::processIMC);
		NeoForge.EVENT_BUS.addListener(this::syncDataManagers);
		NeoForge.EVENT_BUS.addListener(this::addReloadListeners);

		// Preload the service
		DataPackAnvilApi.service();
	}

	private void processIMC(InterModProcessEvent event) {
		WRAPPER.putManagerFromIMC(() -> new DataManagerIMC<>(DataPackAnvilApi.REMAP_KEYS_MANAGER).withCodec(RemapKeys.CODEC));
		event.getIMCStream(DataManagerIMC.METHOD::equals).forEach(message -> WRAPPER.putManagerFromIMC(message.messageSupplier()));
	}

	private void syncDataManagers(OnDatapackSyncEvent event) {
		if (event.getPlayer() != null) {
			return;
		}

		DPAnvilPacketDistributor.sendToAllRemotePlayers(new ReloadDataPayload(DataPackAnvil.WRAPPER.ids()));
	}


	private void addReloadListeners(AddServerReloadListenersEvent event) {
		if (!WRAPPER.getDataManagers().isEmpty()) {
			event.addListener(DPAnvilNames.Identifiers.DATA_MANAGER_ROOT, WRAPPER);
		}
	}

}
