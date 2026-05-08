package sirttas.dpanvil.api.data.preprocessor;

import com.google.gson.JsonElement;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;
import sirttas.dpanvil.api.json.merger.DeepJsonMerger;
import sirttas.dpanvil.api.json.merger.JsonMerger;

import java.util.List;
import java.util.function.Function;

public class InheritanceDataPreprocessor implements DataPreprocessor {

    private static final String PARENT_TAG_NAME = DPAnvilNames.Identifiers.PARENT.toString();

    private final JsonMerger jsonMerger;

    public InheritanceDataPreprocessor() {
        this(DeepJsonMerger.INSTANCE);
    }

    public InheritanceDataPreprocessor(JsonMerger jsonMerger) {
        this.jsonMerger = jsonMerger;
    }

    public static <O> RecordCodecBuilder<O, @Nullable Identifier> fieldOf(Function<O, Identifier> getter) {
        return Identifier.CODEC.optionalFieldOf(PARENT_TAG_NAME, null).forGetter(getter);
    }

    @Override
    public List<JsonElement> preprocess(Context context, List<JsonElement> source) {
        return source.stream()
                .map(element -> preprocess(context, element))
                .toList();
    }

    private JsonElement preprocess(Context context, JsonElement element) {
        if (!element.isJsonObject()) {
            throw new IllegalArgumentException("Inheritance can only be applied to json objects.");
        }

        var jsonObject = element.getAsJsonObject();

        if (jsonObject.has(PARENT_TAG_NAME)) {
            var parentId = Identifier.parse(jsonObject.get(PARENT_TAG_NAME).getAsString());
            var parentElements = context.getProcessed(parentId).stream()
                    .filter(JsonElement::isJsonObject)
                    .map(JsonElement::getAsJsonObject)
                    .findFirst()
                    .orElse(null);

            if (parentElements != null) {
                return jsonMerger.merge(parentElements, jsonObject);
            } else {
                DataPackAnvilApi.LOGGER.error("Couldn't find parent {}.", parentId);
            }
        }
        return jsonObject;
    }

}
