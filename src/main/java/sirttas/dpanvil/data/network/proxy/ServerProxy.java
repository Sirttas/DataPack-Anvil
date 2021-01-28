package sirttas.dpanvil.data.network.proxy;

import net.minecraft.entity.player.PlayerEntity;

public class ServerProxy implements IProxy {

	@Override
	public boolean isRemotePlayer(PlayerEntity player) {
		return true;
	}

}
