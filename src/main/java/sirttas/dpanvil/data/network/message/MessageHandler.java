package sirttas.dpanvil.data.network.message;



import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import sirttas.dpanvil.DataPackAnvil;

public class MessageHandler {

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(DataPackAnvil.createRL("main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);

	private MessageHandler() {}
	
	public static void setup() {
		CHANNEL.registerMessage(0, ReloadDataMessage.class, ReloadDataMessage::encode, ReloadDataMessage::decode, ReloadDataMessage::handle);
	}
}
