package sirttas.dpanvil.data.network.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sirttas.dpanvil.interaction.jei.JeiLoadDelayer;

@SuppressWarnings("resource")
public class ClientProxy implements IProxy {

	@Override
	public void registerHandlers() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		modBus.addListener(this::setupClient);
	}
	
	@Override
	public boolean isRemotePlayer(PlayerEntity player) {
		PlayerEntity clientPlayer = Minecraft.getInstance().player;

		return clientPlayer != null && !clientPlayer.equals(player);
	}

	
	private void setupClient(FMLClientSetupEvent event) {
		JeiLoadDelayer.setup();
	}
}
