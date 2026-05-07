package sirttas.dpanvil.api.data;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jspecify.annotations.Nullable;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.data.preprocessor.DataPreprocessor;
import sirttas.dpanvil.api.event.DataManagerReloadEvent;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
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
 * during {@link AddServerReloadListenersEvent}. <b>Don't add it yourself or it will
 * break!</b>.
 * <p>
 * It can also be used as a codec but it will only serialize the resource
  * location. It cannot be used to read datapack or sychronize its content, onlyly
 * help with NBT or network messages.
 * </p>
 * 
 * @param <T> the type of data the manager contains
 */
public interface IDataManager<T> extends PreparableReloadListener, Codec<T>, Keyable, HolderOwner<T> {

	/**
	 * The key used to register the manager
	 *
	 * @return The key used to register the manager
	 */
	ResourceKey<IDataManager<T>> getKey();

	/**
	 * The {@link Class} used to define the type of managed data
	 *
	 * @return The {@link Class} used to define the type of managed data
	 */
    Class<T> getContentType();

	/**
	 * The folder where the data are found in the datapack
	 *
	 * @return The folder where the data are found in the datapack
	 */
    String getFolder();

	/**
	 * Create a {@link ResourceKey} from a {@link Identifier}
	 *
	 * @param id the {@link Identifier}
	 * @return The {@link ResourceKey} created
	 */
	static <T> ResourceKey<T> createKey(ResourceKey<? super IDataManager<T>> managerKey, Identifier id) {
		return DataPackAnvilApi.createResourceKey(managerKey.identifier(), id);
	}

	static <T> ResourceKey<IDataManager<T>> createManagerKey(Identifier pLocation) {
		return DataPackAnvilApi.createResourceKey(DPAnvilNames.Identifiers.DATA_MANAGER_ROOT, pLocation);
	}

	static <T> Codec<ResourceKey<T>> keyCodec(ResourceKey<? super IDataManager<T>> managerKey) {
		return Identifier.CODEC.xmap(l -> createKey(managerKey, l), ResourceKey::identifier);
	}

	/**
	 * Retrieve the {@link Map} of data handled by this manager. it may be immutable
	 *
	 * @return a map of the data
	 * @see #setData(Map)
	 * @see ImmutableMap
	 */
    Map<Identifier, T> getData();

	/**
	 * Set the {@link Map} of data handled by this manager. It will be changed to an
	 * {@link ImmutableMap} and post a {@link DataManagerReloadEvent}
	 *
	 * @param map the new data {@link Map}
	 * @see #getData()
	 */
	void setData(Map<Identifier, T> map);

	/**
	 * Get a {@link Holder} that wrap a value contained in this manager.
	 *
	 * @param key A {@link ResourceKey<T>} that map a data
	 * @return A {@link Holder}
	 */
	default Holder<T> getOrCreateHolder(ResourceKey<T> key) {
		return getOrCreateHolder(key.identifier());
	}


	/**
	 * Get a {@link Holder} that wrap a value contained in this manager.
	 *
	 * @param key A {@link Identifier} that map a data
	 * @return A {@link Holder}
	 */
	Holder<T> getOrCreateHolder(Identifier key);

	/**
	 * Get a {@link Stream} containing {@link Holder} that wrap a value contained in this manager.
	 *
	 * @return A {@link Stream} of {@link Holder}
	 */
	default Stream<Holder<T>> holders() {
		return getData().keySet().stream()
				.map(this::getOrCreateHolder)
				.filter(Holder::isBound);
	}

	/**
	 * Get data mapped by the id
	 *
	 * @param key A {@link ResourceKey<T>} that map a data
	 * @return The corresponding data
	 */
	@Nullable
	default T get(ResourceKey<T> key) {
		return getData().get(key.identifier());
	}

	/**
	 * Get data mapped by the id
	 *
	 * @param id A {@link Identifier} that map a data
	 * @return The corresponding data
	 */
	@Nullable
	default T get(Identifier id) {
		return getData().get(id);
	}

	/**
	 * Get data mapped by the id or a default value
	 *
	 * @param id           A {@link Identifier} that map a data
	 * @param defaultValue the default value
	 * @return The corresponding data or the default value
	 */
	@Nullable
	default T getOrDefault(Identifier id, @Nullable T defaultValue) {
		if (defaultValue == null) {
			return get(id);
		}
		return getData().getOrDefault(id, defaultValue);
	}

	/**
	 * Get an {@link Optional} of a data mapped by the id
	 *
	 * @param id A {@link Identifier} that map a data
	 * @return an {@link Optional} of the corresponding data or an empty {@link Optional}
	 */
	default Optional<T> getOptional(Identifier id) {
		return Optional.ofNullable(get(id));
	}

	/**
	 * Get the ID for a value
	 *
	 * @param value the value to search
	 * @return the id used for this value
	 */
	default Identifier getId(final @Nullable T value) {
		return getData().entrySet().stream()
				.filter(e -> e.getValue().equals(value)).map(Entry::getKey)
				.findAny()
				.orElse(DPAnvilNames.Identifiers.NONE);
	}

	/**
	 * Get the ID for an holder
	 *
	 * @param holder the holder to search
	 * @return the id used for this holder
	 */
	default Identifier getId(Holder<T> holder) {
		if (holder instanceof Holder.Reference<T> r) {
			return r.key().identifier();
		}
		return getId(holder.value());
	}

	/**
	 * get a list of {@link T} corresponding to each of the ids in a collection
	 *
	 * @param ids the ids to use
	 * @return the list of corresponding data
	 */
	default List<T> getAll(Collection<Identifier> ids) {
		return ids.stream()
				.map(this::get)
				.filter(Objects::nonNull)
				.toList();
	}

	default boolean hasId(Identifier id) {
		return getData().containsKey(id);
	}

	default boolean has(T value) {
		return getData().containsValue(value);
	}

	@Override
	default <U> DataResult<Pair<@Nullable T, U>> decode(final DynamicOps<U> ops, final U input) {
		return Identifier.CODEC.decode(ops, input).map(pair -> pair.mapFirst(this::get));
	}

	@Override
	default <U> DataResult<U> encode(final T input, final DynamicOps<U> ops, final U prefix) {
		return Identifier.CODEC.encode(getId(input), ops, prefix);
    }

	@Override
	default <U> Stream<U> keys(DynamicOps<U> dynOps) {
		return getData().keySet().stream().map(id -> dynOps.createString(id.toString()));
	}

	default DeferredHolder<DataComponentType<?>, DataComponentType<Holder<T>>> registerComponentType(DeferredRegister<DataComponentType<?>> deferredRegister) {
		var registryNamespace = deferredRegister.getNamespace();
		var location = getKey().identifier();
		var managerNamespace = location.getNamespace();

		if (!registryNamespace.equals(managerNamespace)) {
			throw new IllegalArgumentException("The deferred register namespace (" + registryNamespace + ") must be the same as the manager namespace (" + managerNamespace + ").");
		}
		return deferredRegister.register(location.getPath(), () -> DataComponentType.<Holder<T>>builder()
				.persistent(Identifier.CODEC.xmap(this::getOrCreateHolder, this::getId))
				.networkSynchronized(Identifier.STREAM_CODEC.map(this::getOrCreateHolder, this::getId))
				.cacheEncoding()
				.build());
	}

	static <T> Builder<T> builder(Class<T> type, ResourceKey<IDataManager<T>> key) {
		return DataPackAnvilApi.service().createDataManagerBuilder(type, key);
	}

	interface Builder<T> {

		Builder<T> idSetter(BiConsumer<T, Identifier> idSetter);

		Builder<T> folder(String folder);

		Builder<T> withDefault(Function<Identifier, T> factory);

		default Builder<T> withDefault(T defaultValue) {
			return withDefault(id -> defaultValue);
		}

		Builder<T> preprocessor(DataPreprocessor preprocessor);

		default Builder<T> defaultPreprocessors() {
			return this;
		}

		IDataManager<T> build();

	}
}
