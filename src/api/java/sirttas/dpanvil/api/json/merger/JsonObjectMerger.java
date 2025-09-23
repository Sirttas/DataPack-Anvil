package sirttas.dpanvil.api.json.merger;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectMerger implements JsonMerger {

    private final Map<String, JsonMerger> mergerMap;

    private JsonObjectMerger(Map<String, JsonMerger> mergerMap) {
        this.mergerMap = mergerMap;
    }

    @Override
    public JsonElement merge(JsonElement jsonElement1, JsonElement jsonElement2) {
        if (!jsonElement1.isJsonObject() || !jsonElement2.isJsonObject()) {
            throw new IllegalArgumentException("Both elements must be JsonObjects");
        }

        var result = new JsonObject();
        var jsonObject1 = jsonElement1.getAsJsonObject();
        var jsonObject2 = jsonElement2.getAsJsonObject();
        var keys = Sets.union(jsonObject1.keySet(), jsonObject2.keySet());

        for (var key : keys) {
            var value1 = jsonObject1.get(key);
            var value2 = jsonObject2.get(key);

            if (value1 == null) {
                result.add(key, value2);
            } else if (value2 == null) {
                result.add(key, value1);
            } else if (value1.equals(value2)) {
                result.add(key, value1);
            } else {
                var merger = mergerMap.getOrDefault(key, DeepJsonMerger.INSTANCE);

                result.add(key, merger.merge(value1, value2));
            }
        }
        return result;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, JsonMerger> mergerMap;

        public Builder() {
            this.mergerMap = new HashMap<>();
        }

        public Builder with(String key, JsonMerger merger) {
            mergerMap.put(key, merger);
            return this;
        }

        public JsonObjectMerger build() {
            return new JsonObjectMerger(mergerMap);
        }
    }
}
