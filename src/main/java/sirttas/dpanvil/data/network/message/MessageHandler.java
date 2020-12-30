package sirttas.dpanvil.data.network.message;

import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.data.ReloadDataMessage;

public class MessageHandler {

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(DataPackAnvil.createRL("main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);

	public static void setup() {
		CHANNEL.registerMessage(0, ReloadDataMessage.class, ReloadDataMessage::encode, ReloadDataMessage::decode, ReloadDataMessage::handle);
	}
}
