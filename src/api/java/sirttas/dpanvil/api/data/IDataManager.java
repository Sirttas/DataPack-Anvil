package sirttas.dpanvil.api.data;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import sirttas.dpanvil.api.event.DataManagerReloadEvent;
import sirttas.dpanvil.data.serializer.IJsonDataSerializer;

/**
 * <p>
 * A manager used to retrieve data from the datapack.
 * </p>
 * <p>
 * it is a {@link IFutureReloadListener} and will be automatically register
 * during {@link AddReloadListenerEvent}.
 * </p>
 * 
 * @param <T> the type of data the manager contains
 * @see #enqueueIMC(ResourceLocation)
 * @see IJsonDataSerializer
 */
public interface IDataManager<T> extends IFutureReloadListener {

	/**
	 * Retrieve the {@link Map} of data handled by this manager.
	 * 
	 * @return a map of the data
	 * @see #setData()
	 */
	Map<ResourceLocation, T> getData();

	/**
	 * Set the {@link Map} of data handled by this manager. It will be changed to an
	 * {@link ImmutableMap} and post a {@link DataManagerReloadEvent}
	 * 
	 * @param map the new data {@link Map}
	 * @see #data
	 * @see #getData()
	 */
	void setData(Map<ResourceLocation, T> map);

	/**
	 * Get data mapped by the id
	 * 
	 * @param id A {@link ResourceLocation} that map a data
	 * @return The corresponding data
	 */
	default T get(ResourceLocation id) {
		return getData().get(id);
	}

	/**
	 * Get data mapped by the id or a default value
	 * 
	 * @param id           A {@link ResourceLocation} that map a data
	 * @param defaultValue the default value
	 * @return The corresponding data or the default value
	 */
	default T getOrDefault(ResourceLocation id, T defaultValue) {
		return getData().getOrDefault(id, defaultValue);
	}

	/**
	 * The {@link Class} used to define the type of managed data
	 * 
	 * @return The {@link Class} used to define the type of managed data
	 */
	Class<T> getContentType();
}