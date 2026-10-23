package com.hbm.hazard;

import java.util.List;
import java.util.OptionalDouble;

/** Per-item levels; stack count scaling belongs to each original hazard's effect. */
public record HazardEvaluation(List<Entry> entries, boolean blacklisted) {
    public HazardEvaluation { entries = List.copyOf(entries); }

    /**
     * Legacy getHazardLevelFromStack returned the first matching entry.
     * Empty means that first entry needs an unported state modifier; no entry is 0.
     */
    public OptionalDouble firstLevel(HazardType type) {
        for (var entry : entries) {
            if (entry.type() == type) return entry.level() == null ? OptionalDouble.empty() : OptionalDouble.of(entry.level());
        }
        return OptionalDouble.of(0);
    }

    public boolean fullyResolved() { return entries.stream().allMatch(entry -> entry.level() != null); }

    public record Entry(HazardType type, Float level, List<String> pendingModifiers) {
        public Entry { pendingModifiers = List.copyOf(pendingModifiers); }
    }
}
