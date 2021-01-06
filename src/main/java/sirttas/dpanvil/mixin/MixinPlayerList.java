package sirttas.dpanvil.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.management.PlayerList;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.data.ReloadDataMessage;
import sirttas.dpanvil.data.network.message.MessageHelper;

@Mixin(PlayerList.class)
public class MixinPlayerList {

	@Inject(method = "reloadResources", at = @At("RETURN"))
	public void onReloadResources(CallbackInfo ci) {
		MessageHelper.sendToAllPlayers(new ReloadDataMessage(DataPackAnvil.WRAPPER.ids()));
	}

}
