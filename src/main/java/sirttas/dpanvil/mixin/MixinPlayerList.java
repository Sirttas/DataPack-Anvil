package sirttas.dpanvil.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.DataPackAnvilHooks;
import sirttas.dpanvil.data.network.message.MessageHelper;
import sirttas.dpanvil.data.network.message.ReloadDataMessage;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

	@Shadow
	private MinecraftServer server;
	
	@Inject(method = "reloadResources", at = @At("RETURN"))
	public void onReloadResources(CallbackInfo ci) {
		MessageHelper.sendToAllRemotePlayers(new ReloadDataMessage(DataPackAnvil.WRAPPER.ids()));
		DataPackAnvilHooks.onReloadComplet(server);
	}

}
