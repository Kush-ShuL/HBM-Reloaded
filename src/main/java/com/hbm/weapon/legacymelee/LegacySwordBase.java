package com.hbm.weapon.legacymelee;

import com.hbm.block.fluid.LegacyMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.*;

/** Actual old ItemSword damage/material/mining/blocking primitives, shared only by this family. */
public class LegacySwordBase extends SwordItem {
    private final String sourceName;
    private static final class SourceProperties extends Item.Properties {
        @Override public Item.Properties durability(int uses){return uses==0?stacksTo(1):super.durability(uses);}
    }
    protected LegacySwordBase(Tier material,String name,double movement,Rarity rarity) {
        super(material,new SourceProperties().attributes(attributes(material,movement)).rarity(rarity));sourceName=name;
    }
    private static ItemAttributeModifiers attributes(Tier material,double movement) {
        var attributes=SwordItem.createAttributes(material,4,-2.4F);
        return movement==0?attributes:attributes.withModifierAdded(Attributes.MOVEMENT_SPEED,
                new AttributeModifier(ResourceLocation.parse("hbm:legacy_melee_movement"),movement,AttributeModifier.Operation.ADD_MULTIPLIED_BASE),EquipmentSlotGroup.MAINHAND);
    }
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+sourceName+".name");}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.BLOCK;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 72000;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){player.startUsingItem(hand);return InteractionResultHolder.consume(player.getItemInHand(hand));}
    @Override public boolean canPerformAction(ItemStack stack,ItemAbility ability){return !ability.equals(ItemAbilities.SWORD_SWEEP)&&!ability.equals(ItemAbilities.SHIELD_BLOCK)&&super.canPerformAction(stack,ability);}
    @Override public float getDestroySpeed(ItemStack stack,BlockState state){if(state.is(Blocks.COBWEB))return 15;return switch(LegacyMaterials.of(state).name()){case "plants","vine","coral","leaves","gourd"->1.5F;default->1;};}
    @Override public boolean isCorrectToolForDrops(ItemStack stack,BlockState state){return state.is(Blocks.COBWEB);}
    @Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){LegacySwordWear.damage(stack,1,attacker);}
    @Override public boolean mineBlock(ItemStack stack,Level level,BlockState state,BlockPos pos,LivingEntity miner){if(state.getDestroySpeed(level,pos)!=0)LegacySwordWear.damage(stack,2,miner);return true;}
}
