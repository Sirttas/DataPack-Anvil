package sirttas.dpanvil.api.data.preprocessor;

import com.google.gson.JsonElement;

import java.util.List;

public class PickingDataPreprocessor implements DataPreprocessor {

    private final PickingType pickingType;

    public PickingDataPreprocessor() {
        this(PickingType.LAST);
    }

    public PickingDataPreprocessor(PickingType pickingType) {
        this.pickingType = pickingType;
    }

    @Override
    public List<JsonElement> preprocess(Context context, List<JsonElement> source) {
        if (source.isEmpty()) {
            return List.of();
        }

        return List.of(switch(pickingType) {
            case FIRST -> source.getFirst();
            case LAST -> source.getLast();
        });
    }

    public enum PickingType {
        FIRST,
        LAST
    }
}
