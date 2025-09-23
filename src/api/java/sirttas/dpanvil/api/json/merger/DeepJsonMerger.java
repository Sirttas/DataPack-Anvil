package sirttas.dpanvil.api.json.merger;

import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class DeepJsonMerger implements JsonMerger {

    public static final DeepJsonMerger INSTANCE = new DeepJsonMerger();

    private DeepJsonMerger() {}

    @Override
    public JsonElement merge(JsonElement jsonElement1, JsonElement jsonElement2) {
        if (jsonElement1 == null) {
            return jsonElement2;
        } else if (jsonElement2 == null) {
            return jsonElement1;
        } else if (jsonElement1.equals(jsonElement2)) {
            return jsonElement1;
        }

        if (jsonElement1.isJsonObject() && jsonElement2.isJsonObject()) {
            return mergeObjects(jsonElement1.getAsJsonObject(), jsonElement2.getAsJsonObject());
        } else if (jsonElement1.isJsonArray() && jsonElement2.isJsonArray()) {
            return mergeArrays(jsonElement1.getAsJsonArray(), jsonElement2.getAsJsonArray());
        }
        return jsonElement2;
    }

    private JsonElement mergeObjects(JsonObject jsonObject1, JsonObject jsonObject2) {
        var result = new JsonObject();
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
                result.add(key, merge(value1, value2));
            }
        }
        return result;
    }

    private JsonElement mergeArrays(JsonArray jsonArray1, JsonArray jsonArray2) {
        var result = new JsonArray(jsonArray1.size() + jsonArray2.size());

        for (var element : jsonArray1) {
            result.add(element);
        }
        for (var element : jsonArray2) {
            result.add(element);
        }
        return result;
    }
}
