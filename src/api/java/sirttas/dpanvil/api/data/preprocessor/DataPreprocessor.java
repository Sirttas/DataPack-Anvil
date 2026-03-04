package sirttas.dpanvil.api.data.preprocessor;

import com.google.gson.JsonElement;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.List;

@FunctionalInterface
public interface DataPreprocessor {

    List<JsonElement> preprocess(Context context, List<JsonElement> source);

    interface Context {
        HolderLookup.Provider getRegistryLookup();
        ICondition.IContext getConditionContext();

        List<JsonElement> getProcessed(Identifier id);
    }

}
