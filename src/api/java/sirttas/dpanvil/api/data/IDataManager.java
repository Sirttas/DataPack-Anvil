package sirttas.dpanvil.api.data;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;

import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.AddReloadListenerEvent;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.event.DataManagerReloadEvent;

/**
 * <p>
 * A manager used to retrieve data from the datapack.
 * </p>
 * <p>
 * It is a {@link IFutureReloadListener} and will be automatically register
 * during {@link AddReloadListenerEvent}. <b>Don't add it yourself or it will
 * break!</b>.
 * <p>
 * It can also be used as a codec but it will only serialize the resource
 * location. It cannot be used to read datapack or sychronize it's conten, only
 * help with NBT or network messages.
 * </p>
 * 
 * @param <T> the type of data the manager contains
 */
public interface IDataManager<T> extends IFutureReloadListener, Codec<T>, Keyable {

	/**
	 * The {@link Class} used to define the type of managed data
	 * 
	 * @return The {@link Class} used to define the type of managed data
	 */
	Class<T> getContentType();

	/**
	 * Retrieve the {@link Map} of data handled by this manager. it may be immutable
	 * 
	 * @return a map of the data
	 * @see #setData()
	 * @see ImmutableMap
	 */
	Map<ResourceLocation, T> getData();

	/**
	 * Set the {@link Map} of data handled by this manager. It will be changed to an
	 * {@link ImmutableMap} and post a {@link DataManagerReloadEvent}
	 * 
	 * @param map the new data {@link Map}
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
	 * Get a {@link Lazy} version of the data mapped by the id
	 * 
	 * @param id A {@link ResourceLocation} that map a data
	 * @return A {@link Lazy} of the corresponding data
	 */
	default Lazy<T> getLazy(ResourceLocation id) {
		return Lazy.of(() -> this.get(id));
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
	 * Get the ID for a value
	 * 
	 * @param value the value to search
	 * @return the id used for this value
	 */
	default ResourceLocation getId(final T value) {
		return getData().entrySet().stream().filter(e -> e.getValue().equals(value)).map(Entry::getKey).findAny().orElse(DataPackAnvilApi.ID_NONE);
	}

	/**
	 * get a list of {@link T} corresponding to each of the ids in a collection
	 * 
	 * @param ids the ids to use
	 * @return the list of corresponding data
	 */
	default List<T> getAll(Collection<ResourceLocation> ids) {
		return ids.stream().map(this::get).collect(Collectors.toList());
	}

	/**
	 * get a{@link Lazy} version of a list of {@link T} corresponding to each of the
	 * ids in a collection
	 * 
	 * @param ids the ids to use
	 * @return a {@link Lazy} of the list of corresponding data
	 */
	default Lazy<List<T>> getAllLazy(Collection<ResourceLocation> ids) {
		return Lazy.of(() -> this.getAll(ids));
	}

	default boolean hasId(ResourceLocation id) {
		return getData().containsKey(id);
	}

	default boolean has(T value) {
		return getData().containsValue(value);
	}

	@Override
	default <U> DataResult<Pair<T, U>> decode(final DynamicOps<U> ops, final U input) {
		return ResourceLocation.CODEC.decode(ops, input).map(pair -> pair.mapFirst(this::get));
	}

	@Override
	default <U> DataResult<U> encode(final T input, final DynamicOps<U> ops, final U prefix) {
		return ResourceLocation.CODEC.encode(getId(input), ops, prefix);
    }

	@Override
	default <U> Stream<U> keys(DynamicOps<U> dynOps) {
		return getData().keySet().stream().map(id -> dynOps.createString(id.toString()));
	}

	@SuppressWarnings("unchecked")
	public static <T> Builder<T> builder(Class<T> type, String folder) {
		try {
			Constructor<?> constructor = Class.forName("sirttas.dpanvil.data.DataManagerBuilder", true, IDataManager.class.getClassLoader()).getConstructor(Class.class, String.class);

			return (Builder<T>) constructor.newInstance(type, folder);
		} catch (SecurityException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException e) {
			DataPackAnvilApi.LOGGER.error("Couldn't get constructor", e);
			return null;
		}
	}

	public interface Builder<T> {

		Builder<T> withIdSetter(BiConsumer<T, ResourceLocation> idSetter);

		default Builder<T> withDefault(T defaultValue) {
			return withDefault(id -> defaultValue);
		}

		Builder<T> withDefault(Function<ResourceLocation, T> factory);

		IDataManager<T> build();
	}
}