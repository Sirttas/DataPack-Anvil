package sirttas.dpanvil.api.predicate.block;

import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class BlockPosPredicateTooltipHelper {

    private BlockPosPredicateTooltipHelper() {}

    public static Component include(Component component) {
        return Component.translatable("tooltip.dpanvil.predicate.prefix.include", component);
    }

    public static Component exclude(Component component) {
        return Component.translatable("tooltip.dpanvil.predicate.prefix.exclude", component);
    }

    public static Component last(Component component) {
        return Component.translatable("tooltip.dpanvil.predicate.prefix.last", component);
    }

    public static Component blank(Component component) {
        return Component.translatable("tooltip.dpanvil.predicate.prefix.blank", component);
    }

    public static <T> List<Component> or(List<T> values, Function<T, List<Component>> mapper) {
        return list(values, mapper, Component.translatable("tooltip.dpanvil.predicate.or"));
    }

    public static <T> List<Component> and(List<T> values, Function<T, List<Component>> mapper) {
        return list(values, mapper, Component.translatable("tooltip.dpanvil.predicate.and"));
    }

    private static <T> List<Component> list(List<T> values, Function<T, List<Component>> mapper, Component first) {
        var list = new ArrayList<Component>(values.size() + 1);

        list.add(first);
        applyWithFirstAndLast(values,
                v -> applyWithFirstAndLast(mapper.apply(v),
                        c -> list.add(BlockPosPredicateTooltipHelper.include(c)),
                        c -> list.add(BlockPosPredicateTooltipHelper.exclude(c)),
                        c -> list.add(BlockPosPredicateTooltipHelper.exclude(c))),
                v -> applyWithFirstAndLast(mapper.apply(v),
                        c -> list.add(BlockPosPredicateTooltipHelper.include(c)),
                        c -> list.add(BlockPosPredicateTooltipHelper.exclude(c)),
                        c -> list.add(BlockPosPredicateTooltipHelper.exclude(c))),
                v -> applyWithFirstAndLast(mapper.apply(v),
                        c -> list.add(BlockPosPredicateTooltipHelper.last(c)),
                        c -> list.add(BlockPosPredicateTooltipHelper.blank(c)),
                        c -> list.add(BlockPosPredicateTooltipHelper.blank(c))));
        return list;
    }

    public static <T> List<Component> not(T source, Function<T, List<Component>> mapper) {
        var value = mapper.apply(source);
        var list = new ArrayList<Component>(value.size() + 1);

        list.add(Component.translatable("tooltip.dpanvil.predicate.not"));
        applyWithFirstAndLast(value,
                c -> list.add(BlockPosPredicateTooltipHelper.last(c)),
                c -> list.add(BlockPosPredicateTooltipHelper.blank(c)),
                c -> list.add(BlockPosPredicateTooltipHelper.blank(c)));
        return list;
    }

    private static <T> void applyWithFirstAndLast(List<T> value, Consumer<T> first, Consumer<T> other, Consumer<T> last) {
        if (!value.isEmpty()) {
            first.accept(value.getFirst());
            for (var i = 1; i < value.size() - 1; i++) {
                other.accept(value.get(i));
            }
            if (value.size() > 1) {
                last.accept(value.getLast());
            }
        }
    }
}
