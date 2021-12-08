package sirttas.dpanvil.api.data;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.AddReloadListenerEvent;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.codec.CodecHelper;
import sirttas.dpanvil.api.event.DataManagerReloadEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * <p>
 * A manager used to retrieve data from the datapack.
 * </p>
 * <p>
 * It is a {@link PreparableReloadListener} and will be automatically register
 * during {@link AddReloadListenerEvent}. <b>Don't add it yourself or it will
 * break!</b>.
 * <p>
 * It can also be used as a codec but it will only serialize the resource
 * location. It cannot be used to read datapack or sychronize it's content, only
 * help with NBT or network messages.
 * </p>
 * 
 * @param <T> the type of data the manager contains
 */
public interface IDataManager<T> extends PreparableReloadListener, Codec<T>, Keyable {

	/**
	 * The {@link Class} used to define the type of managed data
	 * 
	 * @return The {@link Class} used to define the type of managed data
	 */
	@Nonnull
	Class<T> getContentType();
	
	/**
	 * The folder where the data are found in the datapack
	 * 
	 * @return The folder where the data are found in the datapack
	 */
	@Nonnull
	String getFolder();

	/**
	 * Retrieve the {@link Map} of data handled by this manager. it may be immutable
	 * 
	 * @return a map of the data
	 * @see #setData(Map)
	 * @see ImmutableMap
	 */
	@Nonnull
	Map<ResourceLocation, T> getData();

	/**
	 * Set the {@link Map} of data handled by this manager. It will be changed to an
	 * {@link ImmutableMap} and post a {@link DataManagerReloadEvent}
	 * 
	 * @param map the new data {@link Map}
	 * @see #getData()
	 */
	void setData(@Nonnull Map<ResourceLocation, T> map);

	/**
	 * Get a {@link IDataWrapper} that wrap a value contained in this manager.
	 * 
	 * @param id A {@link ResourceLocation} that map a data
	 * @return A {@link IDataWrapper}
	 */
	@Nonnull
	IDataWrapper<T> getWrapper(@Nonnull ResourceLocation id);
	
	/**
	 * Get data mapped by the id
	 * 
	 * @param id A {@link ResourceLocation} that map a data
	 * @return The corresponding data
	 */
	@Nullable
	default T get(@Nonnull ResourceLocation id) {
		return getData().get(id);
	}

	/**
	 * Get a {@link Lazy} version of the data mapped by the id
	 * 
	 * @param id A {@link ResourceLocation} that map a data
	 * @return A {@link Lazy} of the corresponding data
	 * @deprecated use {@link IDataManager#getWrapper(net.minecraft.resources.ResourceLocation)} instead.
	 */
	@Deprecated
	@Nonnull
	default Lazy<T> getLazy(@Nonnull ResourceLocation id) {
		return Lazy.of(() -> this.get(id));
	}

	/**
	 * Get data mapped by the id or a default value
	 * 
	 * @param id           A {@link ResourceLocation} that map a data
	 * @param defaultValue the default value
	 * @return The corresponding data or the default value
	 */
	@Nullable
	default T getOrDefault(@Nonnull ResourceLocation id, @Nullable T defaultValue) {
		return getData().getOrDefault(id, defaultValue);
	}

	/**
	 * Get an {@link Optional} of a data mapped by the id
	 * 
	 * @param id A {@link ResourceLocation} that map a data
	 * @return an {@link Optional} of the corresponding data or an empty {@link Optional}
	 */
	@Nonnull
	default Optional<T> getOptional(@Nonnull ResourceLocation id) {
		return Optional.ofNullable(get(id));
	}
	
	/**
	 * Get the ID for a value
	 * 
	 * @param value the value to search
	 * @return the id used for this value
	 */
	@Nonnull
	default ResourceLocation getId(final @Nullable T value) {
		return getData().entrySet().stream().filter(e -> e.getValue().equals(value)).map(Entry::getKey).findAny().orElse(DataPackAnvilApi.ID_NONE);
	}

	/**
	 * get a list of {@link T} corresponding to each of the ids in a collection
	 * 
	 * @param ids the ids to use
	 * @return the list of corresponding data
	 */
	@Nonnull
	default List<T> getAll(@Nonnull Collection<ResourceLocation> ids) {
		return ids.stream().map(this::get).toList();
	}

	/**
	 * get a{@link Lazy} version of a list of {@link T} corresponding to each of the
	 * ids in a collection
	 * 
	 * @param ids the ids to use
	 * @return a {@link Lazy} of the list of corresponding data
	 * @deprecated use {@link IDataManager#getWrapper(net.minecraft.resources.ResourceLocation)} instead.
	 */
	@Deprecated
	@Nonnull
	default Lazy<List<T>> getAllLazy(@Nonnull Collection<ResourceLocation> ids) {
		return Lazy.of(() -> this.getAll(ids));
	}

	default boolean hasId(@Nonnull ResourceLocation id) {
		return getData().containsKey(id);
	}

	default boolean has(@Nonnull T value) {
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
	static <T> Builder<T> builder(Class<T> type, String folder) {
		try {
			Constructor<?> constructor = Class.forName("sirttas.dpanvil.data.manager.SimpleDataManagerBuilder", true, IDataManager.class.getClassLoader()).getConstructor(Class.class, String.class);

			return (Builder<T>) constructor.newInstance(type, folder);
		} catch (Exception e) {
			DataPackAnvilApi.LOGGER.error("Couldn't get constructor", e);
			return null;
		}
	}

	interface Builder<T> {

		Builder<T> withIdSetter(BiConsumer<T, ResourceLocation> idSetter);

		default Builder<T> withDefault(T defaultValue) {
			return withDefault(id -> defaultValue);
		}

		Builder<T> withDefault(Function<ResourceLocation, T> factory);
		
		<R> Builder<T> merged(Function<Stream<R>, T> merger, Function<JsonElement, R> rawParser);
		
		default <R> Builder<T> merged(Function<Stream<R>, T> merger, Decoder<R> rawDecoder) {
			return this.merged(merger, json -> CodecHelper.decode(rawDecoder, json));
		}

		default Builder<T> merged(Function<Stream<T>, T> merger) {
			return this.merged(merger, (Function<JsonElement, T>) null);
		}
		
		IDataManager<T> build();
	}
}
