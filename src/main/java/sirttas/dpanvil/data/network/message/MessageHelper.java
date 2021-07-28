package sirttas.dpanvil.data.network.message;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import sirttas.dpanvil.DataPackAnvil;

public class MessageHelper {

	public static final PacketDistributor<Void> ALL_REMOTE = new PacketDistributor<>(MessageHelper::playerListAllRemote, NetworkDirection.PLAY_TO_CLIENT);

	private MessageHelper() {}
	
	public static <T> void sendToPlayer(ServerPlayer serverPlayer, T message) {
		MessageHandler.CHANNEL.sendTo(message, serverPlayer.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
	}

	public static <T> void sendToRemotePlayer(ServerPlayer serverPlayer, T message) {
		if (DataPackAnvil.PROXY.isRemotePlayer(serverPlayer)) {
			sendToPlayer(serverPlayer, message);
		}
	}

	public static <T> void sendToAllPlayers(T message) {
		MessageHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), message);
	}

	public static <T> void sendToAllRemotePlayers(T message) {
		MessageHandler.CHANNEL.send(ALL_REMOTE.noArg(), message);
	}

	private static Consumer<Packet<?>> playerListAllRemote(PacketDistributor<Void> distributor, final Supplier<Void> voidSupplier) {
		return p -> ((MinecraftServer) LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER)).getPlayerList().getPlayers().stream()
				.filter(DataPackAnvil.PROXY::isRemotePlayer)
				.forEach(player -> player.connection.send(p));
	}
}
