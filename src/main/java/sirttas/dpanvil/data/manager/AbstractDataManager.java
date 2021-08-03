package sirttas.dpanvil.data.manager;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraftforge.common.MinecraftForge;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.IDataManager;
import sirttas.dpanvil.api.data.IDataWrapper;
import sirttas.dpanvil.api.event.DataManagerReloadEvent;

public abstract class AbstractDataManager<T, U> extends SimplePreparableReloadListener<Map<ResourceLocation, U>> implements IDataManager<T> {

	protected static final Gson GSON = new GsonBuilder().create();
	
	private final Class<T> contentType;
	private final Function<ResourceLocation, T> defaultValueFactory;
	private final List<DefaultDataWrapper<T>> wrappers;
	private BiMap<ResourceLocation, T> data = ImmutableBiMap.of();
	protected final String folder;
	protected final BiConsumer<T, ResourceLocation> idSetter;
	protected ResourceLocation id;
	
	protected AbstractDataManager(Class<T> contentType, String folder, Function<ResourceLocation, T> defaultValueFactory, BiConsumer<T, ResourceLocation> idSetter) {
		this.contentType = contentType;
		this.defaultValueFactory = defaultValueFactory;
		this.idSetter = idSetter;
		this.folder = folder;
		this.wrappers = Lists.newArrayList();
	}

	@Override
	public Map<ResourceLocation, T> getData() {
		return data;
	}

	@Override
	public void setData(Map<ResourceLocation, T> map) {
		map.forEach((loc, value) -> idSetter.accept(value, loc));
		data = ImmutableBiMap.copyOf(map);
		this.wrappers.forEach(w -> w.set(this.get(w.getId())));
		DataPackAnvilApi.LOGGER.info("Loaded {} {}", data.size(), id);
		MinecraftForge.EVENT_BUS.post(new DataManagerReloadEvent<>(this));
	}

	@Override
	public Class<T> getContentType() {
		return contentType;
	}

	@Override
	public ResourceLocation getId(final T value) {
		return data.inverse().getOrDefault(value, DataPackAnvilApi.ID_NONE);
	}

	@Override
	public T get(ResourceLocation id) {
		T value = data.get(id);

		if (value != null) {
			return value;
		}
		return defaultValueFactory.apply(id);
	}

	@Override
	public IDataWrapper<T> getWrapper(ResourceLocation id) {
		DefaultDataWrapper<T> wrapper = new DefaultDataWrapper<>(id);
		
		wrapper.set(this.get(id));
		this.wrappers.add(wrapper);
		return wrapper;
	}
	
	@Override
	public String getFolder() {
		return folder;
	}
	
	public void setId(ResourceLocation id) {
		this.id = id;
	}
}
