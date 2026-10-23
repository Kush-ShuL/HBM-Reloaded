package com.hbm.block.crate;

import com.hbm.registry.HbmContent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

/** Source ModSword has no crowbar-specific branch; use its real HBM_STEEL material. */
public final class CrowbarItem extends SwordItem implements com.hbm.item.legacysword.LegacySwordBlocking {
    private static final Tier STEEL=new Tier(){
        @Override public int getUses(){return 750;}
        @Override public float getSpeed(){return 8;}
        @Override public float getAttackDamageBonus(){return 2;}
        @Override public TagKey<Block> getIncorrectBlocksForDrops(){return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;}
        @Override public int getEnchantmentValue(){return 10;}
        @Override public Ingredient getRepairIngredient(){return Ingredient.of(HbmContent.item("ingot_steel"));}
    };
    public CrowbarItem(){super(STEEL,new Properties().attributes(SwordItem.createAttributes(STEEL,4,-2.4F)));}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.BLOCK;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 72000;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){player.startUsingItem(hand);return InteractionResultHolder.consume(player.getItemInHand(hand));}
    @Override public boolean canPerformAction(ItemStack stack,ItemAbility action){return !action.equals(ItemAbilities.SWORD_SWEEP)&&!action.equals(ItemAbilities.SHIELD_BLOCK)&&super.canPerformAction(stack,action);}
}
