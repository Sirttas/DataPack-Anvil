package sirttas.dpanvil.data.network.proxy;

import net.minecraft.entity.player.PlayerEntity;

public interface IProxy {

	default void registerHandlers() {}
	
	boolean isRemotePlayer(PlayerEntity player);

}
