package sirttas.dpanvil.data.network.payload;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.function.Consumer;

public class PayloadHelper {

	public static final PacketDistributor<ServerPlayer> REMOTE = new PacketDistributor<>(PayloadHelper::remotePlayer, PacketFlow.SERVERBOUND);
	public static final PacketDistributor<Void> ALL_REMOTE = new PacketDistributor<>(PayloadHelper::playerListAllRemote, PacketFlow.SERVERBOUND);

	private PayloadHelper() {}
	
	public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer serverPlayer, T payload) {
		PacketDistributor.PLAYER.with(serverPlayer).send(payload);
	}

	public static <T extends CustomPacketPayload> void sendToRemotePlayer(ServerPlayer serverPlayer, T payload) {
		REMOTE.with(serverPlayer).send(payload);
	}

	public static <T extends CustomPacketPayload> void sendToAllPlayers(T payload) {
		PacketDistributor.ALL.noArg().send(payload);
	}

	public static <T extends CustomPacketPayload> void sendToAllRemotePlayers(T payload) {
		ALL_REMOTE.noArg().send(payload);
	}

	private static Consumer<Packet<?>> remotePlayer(PacketDistributor<ServerPlayer> distributor, ServerPlayer player) {
		return p -> {
			if (isRemotePlayer(player)) {
				player.connection.send(p);
			}
		};
	}

	private static Consumer<Packet<?>> playerListAllRemote(PacketDistributor<Void> distributor) {
		return p -> ((MinecraftServer) LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER)).getPlayerList().getPlayers().stream()
				.filter(PayloadHelper::isRemotePlayer)
				.forEach(player -> player.connection.send(p));
	}
	
	private static boolean isRemotePlayer(Player player) {
		var server = player.getServer();
		
		if (server != null) {
			return !server.isSingleplayerOwner(player.getGameProfile());
		}
		return true;
	}
}
