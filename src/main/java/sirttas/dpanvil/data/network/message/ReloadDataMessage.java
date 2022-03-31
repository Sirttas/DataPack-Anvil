package sirttas.dpanvil.data.network.message;

import com.google.common.collect.Lists;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.data.DataHandler;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ReloadDataMessage {

	private final List<DataManagerMessage<?>> messages;

	public ReloadDataMessage() {
		messages = Lists.newArrayList();
	}

	public ReloadDataMessage(Collection<ResourceLocation> managers) {
		messages = managers.stream().map(DataManagerMessage::new).collect(Collectors.toList());
	}

	public static ReloadDataMessage decode(FriendlyByteBuf buf) {
		ReloadDataMessage message = new ReloadDataMessage();
		int size = buf.readInt();

		for (int i = 0; i < size; i++) {
			DataManagerMessage<?> managerMessage = new DataManagerMessage<>(buf.readResourceLocation());
			
			managerMessage.decode(buf);
			message.messages.add(managerMessage);
		}
		return message;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(messages.size());
		for (DataManagerMessage<?> message : messages) {
			buf.writeResourceLocation(message.getId());
			message.encode(buf);
		}
		DataPackAnvilApi.LOGGER.debug("Sending DataPack Anvil packet with size: {} bytes", buf::writerIndex);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> messages.forEach(DataManagerMessage::process));
		DataHandler.onDPAnvilUpdate();
		ctx.get().setPacketHandled(true);
	}
}
