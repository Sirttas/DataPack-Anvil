package sirttas.dpanvil.data.network.proxy;

import net.minecraft.world.entity.player.Player;

public class ServerProxy implements IProxy {

	@Override
	public boolean isRemotePlayer(Player player) {
		return true;
	}

}
