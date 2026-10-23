package com.hbm.item.toolability.schrabidium;

import com.hbm.item.toolability.AvailableAbilities;
import com.hbm.item.toolability.ItemToolAbility;
import com.hbm.item.toolability.swords.SourceSwordTier;
import net.minecraft.world.item.Rarity;

/** Actual source setRarity(rare), kept rare even with enchantments by the existing source rarity hook. */
public final class SchrabidiumToolItem extends ItemToolAbility {
    public SchrabidiumToolItem(String name, float damage, Type type, AvailableAbilities abilities) {
        super(name, damage, 0, SourceSwordTier.SCHRABIDIUM, type, abilities, new Properties().rarity(Rarity.RARE));
    }
    @Override public Rarity sourceRarityOverride() { return Rarity.RARE; }
}
