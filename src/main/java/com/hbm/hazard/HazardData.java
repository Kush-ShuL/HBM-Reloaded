package com.hbm.hazard;

import java.util.List;

/** Immutable source registration payload. Override clears entries before mutex. */
public record HazardData(List<HazardEntry> entries, boolean doesOverride, int mutex) {
    public HazardData {
        entries = List.copyOf(entries);
    }

    public HazardData(List<HazardEntry> entries) {
        this(entries, false, 0);
    }
}
