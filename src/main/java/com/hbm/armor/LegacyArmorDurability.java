package com.hbm.armor;

/** Opt-in only for independently ported original HBM armor. Never applied by name or tag. */
public interface LegacyArmorDurability {
    /** Original virtual setters consume power/fuel and keep actual ItemStack damage unchanged. */
    default boolean legacyUsesPostSetDamage(){return this instanceof com.hbm.damage.PoweredFullSuitArmor;}
}
