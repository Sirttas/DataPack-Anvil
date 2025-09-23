package sirttas.dpanvil.api.data.preprocessor;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.codec.CodecHelper;

import java.util.List;

public class NeoForgeConditionsPreprocessor implements DataPreprocessor {

    private static final String CONDITIONS_TAG_NAME = DPAnvilNames.ResourceLocations.NEOFORGE_CONDITIONS.toString();

    @Override
    public List<JsonElement> preprocess(Context context, List<JsonElement> source) {
        return source.stream()
                .filter(jsonElement -> testNeoforgeConditions(context, jsonElement))
                .toList();
    }

    private boolean testNeoforgeConditions(Context context, JsonElement jsonElement) {
        if (jsonElement == null) {
            return false;
        } else if (!jsonElement.isJsonObject()) {
            return true;
        } else if (jsonElement.getAsJsonObject().has(CONDITIONS_TAG_NAME)) {
            var conditions = CodecHelper.decode(ICondition.LIST_CODEC, context.getRegistryLookup().createSerializationContext(JsonOps.INSTANCE), jsonElement.getAsJsonObject().get(CONDITIONS_TAG_NAME));
            return !conditions.stream().allMatch(c -> c.test(context.getConditionContext()));
        }
        return true;
    }
}
