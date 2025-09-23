package sirttas.dpanvil.api.data.remap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Encoder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record RemapKeys(Map<ResourceLocation, ResourceLocation> keys) {
    public static final String NAME = "remap_keys";

    public static final RemapKeys EMPTY = new RemapKeys(Collections.emptyMap());

    public static final Codec<RemapKeys> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).xmap(RemapKeys::new, RemapKeys::keys);

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
