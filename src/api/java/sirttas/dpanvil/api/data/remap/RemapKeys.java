package sirttas.dpanvil.api.data.remap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Encoder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record RemapKeys(Map<Identifier, Identifier> keys) {
    public static final String NAME = "remap_keys";

    public static final RemapKeys EMPTY = new RemapKeys(Collections.emptyMap());

    public static final Codec<RemapKeys> CODEC = Codec.unboundedMap(Identifier.CODEC, Identifier.CODEC).xmap(RemapKeys::new, RemapKeys::keys);

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        public static final Encoder<Builder> ENCODER = RemapKeys.CODEC.comap(b -> new RemapKeys(b.keys));
        private final Map<Identifier, Identifier> keys;

        private Builder() {
            keys = new HashMap<>();
        }

        public Builder add(Identifier key, Identifier value) {
            keys.put(key, value);
            return this;
        }

        public Builder add(Identifier key, ResourceKey<?> value) {
            return add(key, value.identifier());
        }

        public Builder addAll(Map<Identifier, Identifier> keys) {
            this.keys.putAll(keys);
            return this;
        }
    }
}
