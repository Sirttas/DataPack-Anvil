package sirttas.dpanvil.mixin;

//@Mixin(ServerResources.class)
public abstract class MixinDataPackRegistries implements AutoCloseable {

//	@Inject(method = "loadResources", at = @At("RETURN"), cancellable = true)
//	private static void onCreate(List<PackResources> resourcePacks, Commands.CommandSelection environmentType, int functionLevel,
//			Executor backgroundExecutor, Executor gameExecutor, CallbackInfoReturnable<CompletableFuture<ServerResources>> cir) {
//		CompletableFuture<ServerResources> completableFuture = cir.getReturnValue();
//
//		if (completableFuture != null) {
//			cir.setReturnValue(completableFuture.thenApply(dataPackRegistries -> {
//				MinecraftForge.EVENT_BUS.post(new DataPackReloadCompletEvent(dataPackRegistries.getRecipeManager(), dataPackRegistries.getTags(), DataPackAnvil.WRAPPER.getDataManagers()));
//				return dataPackRegistries;
//			}));
//		}
//	}
}
