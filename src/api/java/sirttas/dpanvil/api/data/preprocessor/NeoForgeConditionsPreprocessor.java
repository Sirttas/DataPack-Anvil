package sirttas.dpanvil.api.data.preprocessor;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jspecify.annotations.Nullable;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.codec.CodecHelper;

import java.util.List;
import java.util.function.Function;

public class NeoForgeConditionsPreprocessor implements DataPreprocessor {

    private static final String CONDITIONS_TAG_NAME = DPAnvilNames.Identifiers.NEOFORGE_CONDITIONS.toString();

    public static <O> RecordCodecBuilder<O, List<ICondition>> fieldOf(Function<O, List<ICondition>> getter) {
        return ICondition.LIST_CODEC.optionalFieldOf(CONDITIONS_TAG_NAME, List.of()).forGetter(getter);
    }

    @Override
    public List<JsonElement> preprocess(Context context, List<JsonElement> source) {
        return source.stream()
                .filter(jsonElement -> testNeoforgeConditions(context, jsonElement))
                .toList();
    }

    private boolean testNeoforgeConditions(Context context, @Nullable JsonElement jsonElement) {
        if (jsonElement == null) {
            return false;
        } else if (!jsonElement.isJsonObject()) {
            return true;
        } else if (jsonElement.getAsJsonObject().has(CONDITIONS_TAG_NAME)) {
            var conditions = CodecHelper.decode(ICondition.LIST_CODEC, context.getRegistryLookup().createSerializationContext(JsonOps.INSTANCE), jsonElement.getAsJsonObject().get(CONDITIONS_TAG_NAME));
            return conditions.isEmpty() || conditions.stream().allMatch(c -> c.test(context.getConditionContext()));
        }
        return true;
    }
}
