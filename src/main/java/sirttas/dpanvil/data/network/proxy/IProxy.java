package sirttas.dpanvil.data.network.proxy;

import net.minecraft.world.entity.player.Player;

public interface IProxy {

	default void registerHandlers() {}
	
	boolean isRemotePlayer(Player player);

}
