package sirttas.dpanvil.api.data;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.imc.DataManagerIMC;

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
public abstract class DataManager<T> extends JsonReloadListener {

	private final Class<T> contentType;
	private final IJsonDataSerializer<T> serializer;

	/**
	 * <p>
	 * A {@link Map} containing all data loaded from the datapack.
	 * </p>
	 * <p>
	 * It is an {@link ImmutableMap} and as such it cannot be modified.
	 * </p>
	 */
	protected Map<ResourceLocation, T> data = ImmutableMap.of();

	/**
	 * @param clazz      the class corresponding to {@link T}
	 * @param serializer the serializer used to deserialize data from JSON and to
	 *                   send it to clients see {@link IJsonDataSerializer} for more
	 *                   details
	 * @param folder     the folder where the data is stored its recommended to name
	 *                   it "modeid_dataname"
	 */
	public DataManager(Class<T> contentType, IJsonDataSerializer<T> serializer, String folder) {
		super(new GsonBuilder().registerTypeAdapter(contentType, serializer).create(), folder);
		this.serializer = serializer;
		this.contentType = contentType;
	}

	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objects, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		ImmutableMap.Builder<ResourceLocation, T> builder = ImmutableMap.builder();

		objects.forEach((loc, jsonObject) -> builder.put(loc, gson.fromJson(jsonObject, contentType)));
		data = builder.build();
		refresh();
		DataPackAnvilApi.LOGGER.info("Loaded {} {}", data.size(), this.getName());
	}

	/**
	 * Called after {@link #data} has been changed
	 */
	protected void refresh() { // TODO create an event
		// for override
	}

	/**
	 * Used to get the name of the manager for logging purpose.
	 * 
	 * @return the name of the data manager
	 */
	public abstract String getName();

	/**
	 * Retrieve the {@link Map} of data handled by this manager.
	 * 
	 * @return {@link #data}
	 * @see #setData()
	 */
	public Map<ResourceLocation, T> getData() {
		return data;
	}

	/**
	 * Set the {@link Map} of data handled by this manager. It will be changed to an
	 * {@link ImmutableMap} and call {@link #refresh()}
	 * 
	 * @param map the new data {@link Map}
	 * @see #data
	 * @see #getData()
	 */
	public void setData(Map<ResourceLocation, T> map) {
		data = ImmutableMap.copyOf(map);
		refresh();
		DataPackAnvilApi.LOGGER.info("Loaded {} {}", data.size(), this.getName());
	}

	/**
	 * Get data mapped by the id
	 * 
	 * @param id A {@link ResourceLocation} that map a data
	 * @return The corresponding data
	 */
	public T get(ResourceLocation id) {
		return data.get(id);
	}

	/**
	 * The {@link Class} used to define the type of managed data
	 * 
	 * @return The {@link Class} used to define the type of managed data
	 */
	public Class<T> getContentType() {
		return contentType;
	}

	/**
	 * The serializer used to serialize and deserialize data
	 * 
	 * @return The serializer used to serialize and deserialize data
	 */
	public IJsonDataSerializer<T> getSerializer() {
		return serializer;
	}

	/**
	 * <P>
	 * Enqueue this manager to InterModComms so it can be handled.
	 * </p>
	 * <p>
	 * This method must be called during {@link InterModEnqueueEvent}.
	 * </p>
	 * 
	 * @param id the id of the manager
	 */
	public void enqueueIMC(ResourceLocation id) {
		InterModComms.sendTo(DataPackAnvilApi.MODID, DataManagerIMC.METHOD, () -> new DataManagerIMC<>(id, this));
	}

}