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

@Mixin(DataPackRegistries.class)
public abstract class MixinDataPackRegistries implements AutoCloseable { // NOSONAR mixin

	@Inject(method = "func_240961_a_", at = @At("RETURN"), cancellable = true)
	private static void onCreate(List<IResourcePack> resourcePacks, Commands.EnvironmentType environmentType, int functionLevel,
			Executor backgroundExecutor, Executor gameExecutor, CallbackInfoReturnable<CompletableFuture<DataPackRegistries>> cir) {
		CompletableFuture<DataPackRegistries> completableFuture = cir.getReturnValue();

		if (completableFuture != null) {
			cir.setReturnValue(completableFuture.thenApply(dataPackRegistries -> {
				return dataPackRegistries;
			}));
		}
	}
}
