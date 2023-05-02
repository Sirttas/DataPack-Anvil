package sirttas.dpanvil.api.data.remap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Encoder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import sirttas.dpanvil.api.DataPackAnvilApi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record RemapKeys(Map<ResourceLocation, ResourceLocation> keys) {
    public static final String NAME = "remap_keys";
    public static final String FOLDER = DataPackAnvilApi.MODID + "/" + NAME;
    public static final RemapKeys EMPTY = new RemapKeys(Collections.emptyMap());

    public static final Codec<RemapKeys> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).xmap(RemapKeys::new, RemapKeys::keys);

    public static RemapKeys merge(Stream<RemapKeys> keys) {
        return new RemapKeys(keys
                .<Map.Entry<ResourceLocation, ResourceLocation>>mapMulti((r, downstream) -> r.keys().forEach((k, v) -> downstream.accept(Map.entry(k, v))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2)));
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        public static final Encoder<Builder> ENCODER = RemapKeys.CODEC.comap(b -> new RemapKeys(b.keys));
        private final Map<ResourceLocation, ResourceLocation> keys;

        private Builder() {
            keys = new HashMap<>();
        }

        public Builder add(ResourceLocation key, ResourceLocation value) {
            keys.put(key, value);
            return this;
        }

        public Builder add(ResourceLocation key, ResourceKey<?> value) {
            return add(key, value.location());
        }

        public Builder addAll(Map<ResourceLocation, ResourceLocation> keys) {
            this.keys.putAll(keys);
            return this;
        }
    }
}
