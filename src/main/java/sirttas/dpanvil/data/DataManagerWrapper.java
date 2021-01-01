package sirttas.dpanvil.data;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoader;
import sirttas.dpanvil.annotation.DataHolderProcessor;
import sirttas.dpanvil.api.data.DataManager;
import sirttas.dpanvil.api.imc.DataManagerIMC;

public class DataManagerWrapper implements IFutureReloadListener {

	private final Map<ResourceLocation, DataManager<?>> managers = Maps.newHashMap();

	@SuppressWarnings("unchecked")
	public <T, M extends DataManager<T>> M getManager(ResourceLocation id) {
		return (M) managers.get(id);
	}

	public <T> void putManagerFromIMC(Supplier<DataManagerIMC<T>> imc) {
		DataManagerIMC<T> message = imc.get();

		putManager(message.getId(), message.getManager());
	}

	public <T, M extends DataManager<T>> void putManager(ResourceLocation id, M manager) {
		managers.put(id, manager);
	}

	public Collection<ResourceLocation> ids() {
		return managers.keySet();
	}

	@SuppressWarnings("unchecked")
	public <T, M extends DataManager<T>> M getManager(Class<T> clazz) {
		return (M) managers.values().stream().filter(manager -> manager.getContentType().isAssignableFrom(clazz)).findAny().orElse(null);
	}

	@Override
	public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor,
			Executor gameExecutor) {
		if (ModLoader.isLoadingStateValid()) {
			return CompletableFuture.allOf(managers.values().stream().map(mapper -> mapper.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor))
					.toArray(CompletableFuture[]::new)).thenRun(DataHolderProcessor::apply);
		}
		return CompletableFuture.completedFuture(null);
	}
}
