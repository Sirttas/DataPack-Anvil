package sirttas.dpanvil.data.network.payload;


import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import sirttas.dpanvil.api.DataPackAnvilApi;

@EventBusSubscriber(modid = DataPackAnvilApi.MODID)
public class PayloadHandler {

	private static final String PROTOCOL_VERSION = "1";

	private PayloadHandler() {}

	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event) {
		var registrar = event.registrar(DataPackAnvilApi.MODID).versioned(PROTOCOL_VERSION);

 		registrar.commonToClient(ReloadDataPayload.TYPE, ReloadDataPayload.STREAM_CODEC, ReloadDataPayload::handle);
	}

	@SubscribeEvent
	public static void onGameConfiguration(RegisterConfigurationTasksEvent event) {
		event.register(new ReloadDataTask(event.getListener()));

	}
}
