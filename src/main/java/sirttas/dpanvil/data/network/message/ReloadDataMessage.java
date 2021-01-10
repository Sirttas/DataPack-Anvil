package sirttas.dpanvil.data.network.message;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.data.DataHandler;

public class ReloadDataMessage {

	private final List<DataManagerMessage<?>> messages;

	public ReloadDataMessage() {
		messages = Lists.newArrayList();
	}

	public ReloadDataMessage(Collection<ResourceLocation> managers) {
		messages = managers.stream().map(DataManagerMessage::new).collect(Collectors.toList());
	}

	public static ReloadDataMessage decode(PacketBuffer buf) {
		ReloadDataMessage message = new ReloadDataMessage();
		int size = buf.readInt();

		for (int i = 0; i < size; i++) {
			DataManagerMessage<?> managerMessage = new DataManagerMessage<>(buf.readResourceLocation());
			
			managerMessage.decode(buf);
			message.messages.add(managerMessage);
		}
		return message;
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
		DataPackAnvil.ANNOTATION_PROCESSOR.applyDataHolder();
		DataHandler.onDPAnvilUpdate();
		ctx.get().setPacketHandled(true);
	}
}
