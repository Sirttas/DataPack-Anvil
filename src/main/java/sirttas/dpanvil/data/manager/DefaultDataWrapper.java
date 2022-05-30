package sirttas.dpanvil.data.manager;

import net.minecraft.resources.ResourceLocation;
import sirttas.dpanvil.api.data.IDataWrapper;

@Deprecated(since = "1.18.2-3.3.3", forRemoval = true)
public class DefaultDataWrapper<T> implements IDataWrapper<T> {

	private final ResourceLocation id;
	private T value;
	
	public DefaultDataWrapper(ResourceLocation id) {
		this.id = id;
	}
	
	@Override
	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public boolean isPresent() {
		return value != null;
	}
}
