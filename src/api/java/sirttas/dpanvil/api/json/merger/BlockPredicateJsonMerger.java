package sirttas.dpanvil.api.json.merger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.Identifier;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.predicate.block.logical.AndBlockPredicate;
import sirttas.dpanvil.api.predicate.block.logical.OrBlockPredicate;

public class BlockPredicateJsonMerger implements JsonMerger {

    private final Type type;

    public BlockPredicateJsonMerger() {
        this(Type.AND);
    }

    public BlockPredicateJsonMerger(Type type) {
        this.type = type;
    }

    @Override
    public JsonElement merge(JsonElement jsonElement1, JsonElement jsonElement2) {
        var result = new JsonObject();
        var values = new JsonArray();

        values.add(jsonElement1);
        values.add(jsonElement2);
        result.add("type", new JsonPrimitive(type.name.toString()));
        result.add(DPAnvilNames.VALUES, values);
        return result;
    }

    public enum Type {
        AND(AndBlockPredicate.NAME),
        OR(OrBlockPredicate.NAME);

        private final Identifier name;

        Type(String name) {
            this.name = DPAnvilNames.Identifiers.create(name);
        }
    }
}
