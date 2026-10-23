package com.hbm.hazard;

/** Ordered source level transformation; live fuel adapters preserve state semantics. */
@FunctionalInterface
public interface HazardModifier {
    float modify(HazardStack stack, float level);
}
