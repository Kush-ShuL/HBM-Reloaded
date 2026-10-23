package com.hbm.item.toolability.schrabidium;

import com.hbm.item.toolability.SteelHoeItem;
import com.hbm.item.toolability.swords.SourceSwordTier;
import net.minecraft.world.item.Rarity;

/** Original HoeSchrabidium is ItemHoe with only fixed-rare override, not another area tilling implementation. */
public final class SchrabidiumHoeItem extends SteelHoeItem {
    public SchrabidiumHoeItem() { super(SourceSwordTier.SCHRABIDIUM); }
    @Override public Rarity sourceRarityOverride() { return Rarity.RARE; }
}
