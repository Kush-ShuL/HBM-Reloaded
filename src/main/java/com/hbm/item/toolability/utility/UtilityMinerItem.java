package com.hbm.item.toolability.utility;

import com.hbm.item.toolability.AvailableAbilities;
import com.hbm.item.toolability.ItemToolAbility;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

/** Source setMaxDamage overrides material uses; execution remains the one ItemToolAbility. */
public final class UtilityMinerItem extends ItemToolAbility {
    private static final class RawDamageProperties extends SourceProperties {
        private final int maximum;
        RawDamageProperties(int maximum) { this.maximum = maximum; }
        @Override public Item.Properties durability(int ignoredMaterialUses) { return super.durability(maximum); }
    }
    public UtilityMinerItem(String name, float damage, double movement, Tier material, int maxDamage, AvailableAbilities abilities) {
        super(name, damage, movement, material, Type.MINER, abilities, new RawDamageProperties(maxDamage));
    }
}
