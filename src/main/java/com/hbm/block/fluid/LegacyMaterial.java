package com.hbm.block.fluid;

/** The five 1.7 Material predicates used by the archived fluid algorithms. */
public record LegacyMaterial(String name, boolean solid, boolean blocksMovement, boolean liquid,
                             boolean replaceable, boolean flammable) {
    public boolean isSolid() { return solid; }
    public boolean isLiquid() { return liquid; }
    public boolean isReplaceable() { return replaceable; }
    public static final LegacyMaterial water = new LegacyMaterial("water", false, false, true, true, false);
    public static final LegacyMaterial lava = new LegacyMaterial("lava", false, false, true, true, false);
    public static final LegacyMaterial portal = new LegacyMaterial("portal", false, false, false, false, false);
}
