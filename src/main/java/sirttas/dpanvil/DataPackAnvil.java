package sirttas.dpanvil;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sirttas.dpanvil.annotation.DataHolderProcessor;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.imc.DataManagerIMC;
import sirttas.dpanvil.data.DataManagerWrapper;
import sirttas.dpanvil.data.ReloadDataMessage;
import sirttas.dpanvil.data.network.message.MessageHandler;
import sirttas.dpanvil.data.network.message.MessageHelper;

@Mod(DataPackAnvilApi.MODID)
public class DataPackAnvil {

	public static final DataManagerWrapper WRAPPER = new DataManagerWrapper();
	
	public DataPackAnvil() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		MinecraftForge.EVENT_BUS.addListener(this::playerLogin);
		MinecraftForge.EVENT_BUS.addListener(this::addReloadListeners);

	}

	public static ResourceLocation createRL(String name) {
		if (name.contains(":")) {
			return new ResourceLocation(name);
		}
		return new ResourceLocation(DataPackAnvilApi.MODID, name);
	}

	private void setup(FMLCommonSetupEvent event) {
		MessageHandler.setup();
		DataHolderProcessor.setup();
	}

	private void processIMC(InterModProcessEvent event) {
		event.getIMCStream(DataManagerIMC.METHOD::equals).forEach(message -> WRAPPER.putManagerFromIMC(message.getMessageSupplier()));
	}

	private void playerLogin(PlayerEvent.PlayerLoggedInEvent event) {
		PlayerEntity player = event.getPlayer();

		if (player instanceof ServerPlayerEntity) {
			MessageHelper.sendToPlayer((ServerPlayerEntity) player, new ReloadDataMessage(DataPackAnvil.WRAPPER.ids()));
		}
	}

	private void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(WRAPPER);
	}

}
