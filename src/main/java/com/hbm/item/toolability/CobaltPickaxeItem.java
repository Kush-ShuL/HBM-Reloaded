package com.hbm.item.toolability;

import com.hbm.block.fluid.LegacyMaterials;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;

/** Real source ItemToolAbility cobalt identity, not an ordinary modern pickaxe replacement. */
public final class CobaltPickaxeItem extends PickaxeItem implements com.hbm.block.ore.DepthRockTool,SourceAbilityTool {
    private static final Tier COBALT=new Tier(){
        @Override public int getUses(){return 750;}
        @Override public float getSpeed(){return 9;}
        @Override public float getAttackDamageBonus(){return 2.5F;}
        @Override public TagKey<Block> getIncorrectBlocksForDrops(){return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;}
        @Override public int getEnchantmentValue(){return 60;}
        @Override public Ingredient getRepairIngredient(){return Ingredient.EMPTY;}
    };
    public CobaltPickaxeItem(){super(COBALT,new Properties().attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,4,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND).add(Attributes.MOVEMENT_SPEED,new AttributeModifier(ResourceLocation.parse("hbm:cobalt_tool_movement"),0,AttributeModifier.Operation.ADD_MULTIPLIED_BASE),EquipmentSlotGroup.MAINHAND).build()));}
    @Override public AvailableAbilities abilities(){return AvailableAbilities.COBALT;}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.cobalt_pickaxe.name");}
    @Override public boolean canBreakRock(net.minecraft.world.level.BlockGetter level,net.minecraft.world.entity.player.Player player,ItemStack tool,BlockState rock,net.minecraft.core.BlockPos position){return false;}
    @Override public float getDestroySpeed(ItemStack stack,BlockState state){return switch(LegacyMaterials.of(state).name()){case "iron","anvil","rock","glass"->9;default->state.is(Blocks.ICE)||state.is(BlockTags.RAILS)?9:1;};}
    public boolean sourceCanHarvest(ItemStack stack,BlockState state){return CobaltPresets.read(stack).active().harvest==1||getDestroySpeed(stack,state)>1;}
    @Override public boolean isCorrectToolForDrops(ItemStack stack,BlockState state){return !state.requiresCorrectToolForDrops()||state.is(BlockTags.MINEABLE_WITH_PICKAXE)&&!state.is(BlockTags.INCORRECT_FOR_DIAMOND_TOOL);}
    @Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){stack.hurtAndBreak(1,attacker,EquipmentSlot.MAINHAND);}
    @Override public boolean mineBlock(ItemStack stack,net.minecraft.world.level.Level level,BlockState state,net.minecraft.core.BlockPos pos,LivingEntity miner){if(state.getDestroySpeed(level,pos)!=0)stack.hurtAndBreak(1,miner,EquipmentSlot.MAINHAND);return true;}
    @Override public boolean isFoil(ItemStack stack){return stack.has(DataComponents.ENCHANTMENTS)&&!stack.get(DataComponents.ENCHANTMENTS).isEmpty()||!CobaltPresets.read(stack).active().isNone();}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flags){
        if(!CobaltToolConfig.VEIN.get()&&!CobaltToolConfig.SILK.get()&&!CobaltToolConfig.LUCK.get())return;
        tooltip.add(Component.translatable("gui.hbm.tool_ability.abilities"));if(CobaltToolConfig.VEIN.get())tooltip.add(Component.literal("  ").append(Component.translatable("tool.ability.recursion")).append(" (4)").withStyle(ChatFormatting.GOLD));if(CobaltToolConfig.SILK.get())tooltip.add(Component.literal("  ").append(Component.translatable("tool.ability.silktouch")).withStyle(ChatFormatting.GOLD));if(CobaltToolConfig.LUCK.get())tooltip.add(Component.literal("  ").append(Component.translatable("tool.ability.luck")).append(" (1)").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("gui.hbm.tool_ability.cycle_hint"));tooltip.add(Component.translatable("gui.hbm.tool_ability.sneak_hint"));tooltip.add(Component.translatable("gui.hbm.tool_ability.alt_hint"));
    }
}
