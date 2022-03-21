package sirttas.dpanvil.data.manager;

import net.minecraft.util.ResourceLocation;
import sirttas.dpanvil.api.data.IDataWrapper;

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
