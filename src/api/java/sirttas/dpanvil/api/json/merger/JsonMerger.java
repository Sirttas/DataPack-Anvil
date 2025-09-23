package sirttas.dpanvil.api.json.merger;

import com.google.gson.JsonElement;

@FunctionalInterface
public interface JsonMerger {

    JsonMerger FIRST = (j1, j2) -> j1;
    JsonMerger SECOND = (j1, j2) -> j2;

    JsonElement merge(JsonElement jsonElement1, JsonElement jsonElement2);

}
