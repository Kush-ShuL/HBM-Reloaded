package com.hbm.weapon.legacymelee;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
/** Source BigSword does not start blocking and its whole ability block is commented out. */
public final class BigSwordItem extends LegacySwordBase {
    public BigSwordItem(){super(Tiers.DIAMOND,"big_sword",0,Rarity.COMMON);}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){return InteractionResultHolder.pass(player.getItemInHand(hand));}
}
