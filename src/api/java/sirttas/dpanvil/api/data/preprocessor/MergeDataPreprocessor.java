package sirttas.dpanvil.api.data.preprocessor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.json.merger.DeepJsonMerger;
import sirttas.dpanvil.api.json.merger.JsonMerger;

import java.util.List;

public class MergeDataPreprocessor implements DataPreprocessor {

    private static final String REPLACE_TAG_NAME = DPAnvilNames.Identifiers.REPLACE.toString();

    private final JsonMerger jsonMerger;

    public  MergeDataPreprocessor() {
        this(DeepJsonMerger.INSTANCE);
    }

    public MergeDataPreprocessor(JsonMerger jsonMerger) {
        this.jsonMerger = jsonMerger;
    }

    @Override
    public List<JsonElement> preprocess(Context context, List<JsonElement> source) {
        return source.stream()
                .reduce(this::merge)
                .stream()
                .toList();
    }

    JsonElement merge(JsonElement jsonElement1, JsonElement jsonElement2) {
        if (jsonElement2 instanceof JsonObject json && json.has(REPLACE_TAG_NAME) && json.get(REPLACE_TAG_NAME).getAsBoolean()) {
            return jsonElement2;
        }
        return jsonMerger.merge(jsonElement1, jsonElement2);
    }

}
