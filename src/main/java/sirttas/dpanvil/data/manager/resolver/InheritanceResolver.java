package sirttas.dpanvil.data.manager.resolver;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import sirttas.dpanvil.api.DPAnvilNames;
import sirttas.dpanvil.api.DataPackAnvilApi;

import java.util.HashMap;
import java.util.Map;

public class InheritanceResolver {

    private final Map<ResourceLocation, JsonElement> sourceMap;
    private final Map<ResourceLocation, JsonObject> targetMap;

    private static final String PARENT_TAG_NAME = DPAnvilNames.ResourceLocations.PARENT.toString();

    public InheritanceResolver(Map<ResourceLocation, JsonElement> sourceMap) {
        this.sourceMap = sourceMap;
        this.targetMap = new HashMap<>();
    }

    public Map<ResourceLocation, JsonElement> resolve() {
        for (Map.Entry<ResourceLocation, JsonElement> entry : sourceMap.entrySet()) {
            targetMap.computeIfAbsent(entry.getKey(), k -> resolve(k, entry.getValue()));
        }
        return Map.copyOf(targetMap);
    }

    private JsonObject resolve(ResourceLocation id, JsonElement element) {
        if (!element.isJsonObject()) {
            throw new IllegalArgumentException("Inheritance can only be applied to json objects.");
        }

        var jsonObject = element.getAsJsonObject();

        if (jsonObject.has(PARENT_TAG_NAME)) {
            var parentId = ResourceLocation.parse(jsonObject.get(PARENT_TAG_NAME).getAsString());
            var parentElement = resolveParent(parentId);

            if (parentElement != null) {
                var newElement = parentElement.deepCopy();

                jsonObject.entrySet().forEach(e -> newElement.add(e.getKey(), e.getValue()));
                return newElement;
            } else {
                DataPackAnvilApi.LOGGER.error("Couldn't find parent {} for {}", parentId, id);
            }
        }
        return jsonObject;
    }

    private JsonObject resolveParent(ResourceLocation parentId) {
        if (targetMap.containsKey(parentId)) {
            return targetMap.get(parentId);
        }

        var parentElement = sourceMap.get(parentId);

        if (parentElement != null) {
            return resolve(parentId, parentElement);
        }
        return null;
    }
}
