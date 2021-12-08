package sirttas.dpanvil.data.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.data.IDataWrapper;
import sirttas.dpanvil.api.event.DataManagerReloadEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractDataManager<T, U> extends SimplePreparableReloadListener<Map<ResourceLocation, U>> implements IDataManager<T> {

	protected static final Gson GSON = new GsonBuilder().create();
	
	private final Class<T> contentType;
	private final Function<ResourceLocation, T> defaultValueFactory;
	private final Map<ResourceLocation, DefaultDataWrapper<T>> wrappers;
	private BiMap<ResourceLocation, T> data;
	protected final String folder;
	protected final BiConsumer<T, ResourceLocation> idSetter;
	protected ResourceLocation id;
	
	protected AbstractDataManager(Class<T> contentType, String folder, Function<ResourceLocation, T> defaultValueFactory, BiConsumer<T, ResourceLocation> idSetter) {
		this.contentType = contentType;
		this.defaultValueFactory = defaultValueFactory;
		this.idSetter = idSetter;
		this.folder = folder;
		this.data = ImmutableBiMap.of();
		this.wrappers = new HashMap<>();
	}

	@Override
	public @NotNull Map<ResourceLocation, T> getData() {
		return data;
	}

	@Override
	public void setData(@NotNull Map<ResourceLocation, T> map) {
		map.forEach((loc, value) -> idSetter.accept(value, loc));
		data = ImmutableBiMap.copyOf(map);
		this.wrappers.values().forEach(w -> w.set(this.get(w.getId())));
		DataPackAnvilApi.LOGGER.info("Loaded {} {}", data.size(), id);
		MinecraftForge.EVENT_BUS.post(new DataManagerReloadEvent<>(this));
	}

	@Override
	public @NotNull Class<T> getContentType() {
		return contentType;
	}

	@Override
	public @NotNull ResourceLocation getId(final T value) {
		return data.inverse().getOrDefault(value, DataPackAnvilApi.ID_NONE);
	}

	@Override
	public T get(@NotNull ResourceLocation id) {
		T value = data.get(id);

		if (value != null) {
			return value;
		}
		return defaultValueFactory.apply(id);
	}

	@Override
	public @NotNull IDataWrapper<T> getWrapper(@NotNull ResourceLocation id) {
		return this.wrappers.computeIfAbsent(id, i -> {
			DefaultDataWrapper<T> wrapper = new DefaultDataWrapper<>(id);
			
			wrapper.set(this.get(id));
			return wrapper;
		});
	}
	
	@Override
	public @NotNull String getFolder() {
		return folder;
	}
	
	public void setId(ResourceLocation id) {
		this.id = id;
	}
}
