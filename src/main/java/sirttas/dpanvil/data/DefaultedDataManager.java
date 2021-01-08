package sirttas.dpanvil.data;

import net.minecraft.util.ResourceLocation;

public class DefaultedDataManager<T> extends SimpleDataManager<T> {

	private final T defaultValue;
	
	public DefaultedDataManager(Class<T> contentType, String folder, T defaultValue) {
		super(contentType, folder);
		this.defaultValue = defaultValue;
	}


	@Override
	public T get(ResourceLocation id) {
		return getData().getOrDefault(id, defaultValue);
	}
	
}
