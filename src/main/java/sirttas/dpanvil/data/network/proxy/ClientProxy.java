package sirttas.dpanvil.data.network.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;

@SuppressWarnings("resource")
public class ClientProxy implements IProxy {

	@Override
	public boolean isRemotePlayer(PlayerEntity player) {
		PlayerEntity clientPlayer = Minecraft.getInstance().player;

		return clientPlayer != null && !clientPlayer.equals(player);
	}

}
