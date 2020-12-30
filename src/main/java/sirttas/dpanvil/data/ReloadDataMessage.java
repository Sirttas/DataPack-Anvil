package sirttas.dpanvil.data;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ReloadDataMessage {

	private final List<DataManagerMessage<?>> messages;

	public ReloadDataMessage() {
		messages = DataManagers.MANAGERS.keySet().stream().map(DataManagerMessage::new).collect(Collectors.toList());
	}

	public static ReloadDataMessage decode(PacketBuffer buf) {
		ReloadDataMessage message = new ReloadDataMessage();
		int size = buf.readInt();

		for (int i = 0; i < size; i++) {
			DataManagerMessage<?> managerMessage = new DataManagerMessage<>(buf.readResourceLocation());
			
			managerMessage.decode(buf);
			message.messages.add(managerMessage);
		}
		return null;
	}

	public void encode(PacketBuffer buf) {
		buf.writeInt(messages.size());
		for (DataManagerMessage<?> message : messages) {
			buf.writeResourceLocation(message.getId());
			message.encode(buf);
		}
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> messages.forEach(DataManagerMessage::process));
		ctx.get().setPacketHandled(true);
	}
}
