package sirttas.dpanvil.data.network.payload;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DPAnvilPacketDistributor {

	private static final Method MAKE_CLIENTBOUND_PACKET = ObfuscationReflectionHelper.findMethod(PacketDistributor.class, "makeClientboundPacket", CustomPacketPayload.class, CustomPacketPayload.class.arrayType());

	private DPAnvilPacketDistributor() {}

	private static void sendToRemotePlayer(ServerPlayer player, CustomPacketPayload payload, CustomPacketPayload... payloads) {
		if (isRemotePlayer(player)) {
			player.connection.send(makeClientboundPacket(payload, payloads));
		}
	}

	public static void sendToAllRemotePlayers(CustomPacketPayload payload, CustomPacketPayload... payloads) {
        ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().forEach(player -> sendToRemotePlayer(player, payload, payloads));
	}
	
	private static boolean isRemotePlayer(Player player) {
		var server = player.level() instanceof ServerLevel level ? level.getServer() : null;
		
		if (server != null) {
			return !server.isSingleplayerOwner(new NameAndId(player.getGameProfile()));
		}
		return true;
	}

	private static Packet<?> makeClientboundPacket(CustomPacketPayload payload, CustomPacketPayload... payloads) {
        try {
            return (Packet<?>) MAKE_CLIENTBOUND_PACKET.invoke(null, payload, payloads);
        } catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Reflection error", e);
        }
    }
}
