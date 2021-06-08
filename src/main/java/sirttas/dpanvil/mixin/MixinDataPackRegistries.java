package sirttas.dpanvil.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.command.Commands;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourcePack;
import net.minecraftforge.common.MinecraftForge;
import sirttas.dpanvil.DataPackAnvil;
import sirttas.dpanvil.api.event.DataPackReloadCompletEvent;

@Mixin(DataPackRegistries.class)
public abstract class MixinDataPackRegistries implements AutoCloseable {

	@Inject(method = "loadResources", at = @At("RETURN"), cancellable = true)
	private static void onCreate(List<IResourcePack> resourcePacks, Commands.EnvironmentType environmentType, int functionLevel,
			Executor backgroundExecutor, Executor gameExecutor, CallbackInfoReturnable<CompletableFuture<DataPackRegistries>> cir) {
		CompletableFuture<DataPackRegistries> completableFuture = cir.getReturnValue();

		if (completableFuture != null) {
			cir.setReturnValue(completableFuture.thenApply(dataPackRegistries -> {
				MinecraftForge.EVENT_BUS.post(new DataPackReloadCompletEvent(dataPackRegistries.getRecipeManager(), dataPackRegistries.getTags(), DataPackAnvil.WRAPPER.getDataManagers()));
				return dataPackRegistries;
			}));
		}
	}
}
