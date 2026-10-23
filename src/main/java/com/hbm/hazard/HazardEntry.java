package com.hbm.hazard;

import java.util.List;
import java.util.Objects;

public record HazardEntry(HazardType type, float baseLevel, List<HazardModifier> modifiers,
                          List<String> pendingModifiers) {
    public HazardEntry {
        Objects.requireNonNull(type);
        modifiers = List.copyOf(modifiers);
        pendingModifiers = List.copyOf(pendingModifiers);
    }

    public HazardEntry(HazardType type, float baseLevel) {
        this(type, baseLevel, List.of(), List.of());
    }

    public HazardEvaluation.Entry evaluate(HazardStack stack) {
        if (!pendingModifiers.isEmpty()) return new HazardEvaluation.Entry(type, null, pendingModifiers);
        float level = baseLevel;
        try {
            for (var modifier : modifiers) level = modifier.modify(stack, level);
            if (!Float.isFinite(level)) throw new HazardStateUnavailable("Source item state produced a non-finite hazard level");
        } catch (HazardStateUnavailable unresolved) {
            return new HazardEvaluation.Entry(type, null, List.of(unresolved.getMessage()));
        }
        return new HazardEvaluation.Entry(type, level, List.of());
    }
}
