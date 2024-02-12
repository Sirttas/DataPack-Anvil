package sirttas.dpanvil.data.network.payload;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import sirttas.dpanvil.api.DataPackAnvilApi;

@Mod.EventBusSubscriber(modid = DataPackAnvilApi.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PayloadHandler {

	private static final String PROTOCOL_VERSION = "1";

	private PayloadHandler() {}

	@SubscribeEvent
	public static void register(final RegisterPayloadHandlerEvent event) {
		var registrar = event.registrar(DataPackAnvilApi.MODID).versioned(PROTOCOL_VERSION);

 		registrar.configuration(ReloadDataPayload.ID, ReloadDataPayload::new, ReloadDataPayload::handle);
 		registrar.play(ReloadDataPayload.ID, ReloadDataPayload::new, ReloadDataPayload::handle);
	}

	@SubscribeEvent
	public static void onGameConfiguration(OnGameConfigurationEvent event) {
		event.register(new ReloadDataTask(event.getListener()));

	}
}
