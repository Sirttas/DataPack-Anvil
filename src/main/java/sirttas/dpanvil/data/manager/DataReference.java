package sirttas.dpanvil.data.manager;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import sirttas.dpanvil.api.data.IDataManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DataReference<T> implements Holder<T> {

    private final IDataManager<T> dataManager;
    private Set<TagKey<T>> tags = Set.of();
    @Nullable
    private ResourceKey<T> key;
    @Nullable
    private T value;

    public DataReference(IDataManager<T> dataManager, @Nullable ResourceKey<T> key, @Nullable T value) {
        this.dataManager = dataManager;
        this.key = key;
        this.value = value;
    }

    public ResourceKey<T> key() {
        if (this.key == null) {
            throw new IllegalStateException("Trying to access unbound value '" + this.value + "' from data manager " + this.dataManager);
        } else {
            return this.key;
        }
    }

    @Override
    @Nonnull
    public T value() {
        if (this.value == null) {
            throw new IllegalStateException("Trying to access unbound value '" + this.key + "' from data manager " + this.dataManager);
        } else {
            return this.value;
        }
    }

    @Override
    public boolean is(@Nonnull ResourceLocation id) {
        return this.key().location().equals(id);
    }

    @Override
    public boolean is(@Nonnull ResourceKey<T> key) {
        return this.key() == key;
    }

    @Override
    public boolean is(@Nonnull TagKey<T> tagKey) {
        return this.tags.contains(tagKey);
    }

    @Override
    public boolean is(Predicate<ResourceKey<T>> predicate) {
        return predicate.test(this.key());
    }

    @Override
    @Nonnull
    public Either<ResourceKey<T>, T> unwrap() {
        return Either.left(this.key());
    }

    @Override
    @Nonnull
    public Optional<ResourceKey<T>> unwrapKey() {
        return Optional.of(this.key());
    }

    @Override
    @Nonnull
    public Holder.Kind kind() {
        return Holder.Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(@Nonnull HolderOwner<T> owner) {
        return true;
    }

    @Override
    public boolean isBound() {
        return this.key != null && this.value != null;
    }

    public void bind(ResourceKey<T> key, T value) {
        if (this.key != null && key != this.key) {
            throw new IllegalStateException("Can't change holder key: existing=" + this.key + ", new=" + key);
        }  else {
            this.key = key;
            this.value = value;
        }
    }

    public void bindTags(Collection<TagKey<T>> tagKeys) {
        this.tags = Set.copyOf(tagKeys);
    }

    @Override
    @Nonnull
    public Stream<TagKey<T>> tags() {
        return this.tags.stream();
    }

    public String toString() {
        return "Reference{" + this.key + "=" + this.value + "}";
    }
}
