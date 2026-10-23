package com.hbm.item.structureloot;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.item.legacysword.LegacySwordBlocking;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.*;

/** Real ModSword PIPELEAD material; unrelated ModSword identities are not conflated with this pipe. */
public final class LeadPipeItem extends SwordItem implements LegacySwordBlocking {
    private static final Tier MATERIAL=new Tier(){
        @Override public int getUses(){return 250;}
        @Override public float getSpeed(){return 1.5F;}
        @Override public float getAttackDamageBonus(){return 3;}
        @Override public TagKey<Block> getIncorrectBlocksForDrops(){return BlockTags.INCORRECT_FOR_STONE_TOOL;}
        @Override public int getEnchantmentValue(){return 25;}
        @Override public Ingredient getRepairIngredient(){return Ingredient.EMPTY;}
    };
    public LeadPipeItem(){super(MATERIAL,new Properties().attributes(SwordItem.createAttributes(MATERIAL,4,-2.4F)));}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.weapon_pipe_lead.name");}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.BLOCK;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity consumer){return 72000;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){player.startUsingItem(hand);return InteractionResultHolder.consume(player.getItemInHand(hand));}
    @Override public boolean canPerformAction(ItemStack stack,ItemAbility action){return !action.equals(ItemAbilities.SWORD_SWEEP)&&!action.equals(ItemAbilities.SHIELD_BLOCK)&&super.canPerformAction(stack,action);}
    @Override public float getDestroySpeed(ItemStack stack,BlockState state){if(state.is(Blocks.COBWEB))return 15;return switch(LegacyMaterials.of(state).name()){case "plants","vine","coral","leaves","gourd"->1.5F;default->1;};}
    @Override public boolean isCorrectToolForDrops(ItemStack stack,BlockState state){return state.is(Blocks.COBWEB);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){tooltip.add(Component.translatable("tooltip.hbm.lead_pipe"));}

    @Override public boolean mineBlock(ItemStack stack,Level level,BlockState state,net.minecraft.core.BlockPos pos,LivingEntity miner){if(state.getDestroySpeed(level,pos)!=0)com.hbm.weapon.legacymelee.LegacySwordWear.damage(stack,2,miner);return true;}

    @Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){com.hbm.weapon.legacymelee.LegacySwordWear.damage(stack,1,attacker);}
}
