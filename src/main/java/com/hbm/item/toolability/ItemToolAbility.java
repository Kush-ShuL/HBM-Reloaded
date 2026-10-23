package com.hbm.item.toolability;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.block.ore.DepthRockTool;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.*;

/** Actual source ItemToolAbility's mining-tool slice, sharing existing CobaltPresets/Breaks rather than replacing abilities with vanilla tools. */
public class ItemToolAbility extends DiggerItem implements SourceAbilityTool,DepthRockTool {
    public enum Type {PICKAXE(BlockTags.MINEABLE_WITH_PICKAXE),AXE(BlockTags.MINEABLE_WITH_AXE),SHOVEL(BlockTags.MINEABLE_WITH_SHOVEL),MINER(BlockTags.MINEABLE_WITH_PICKAXE);final TagKey<Block> tag;Type(TagKey<Block> tag){this.tag=tag;}}
    private final String sourceName;private final Type type;private final AvailableAbilities abilities;private boolean depthRockBreaker;
    protected static class SourceProperties extends Properties {@Override public Properties durability(int uses){return uses==0?stacksTo(1):super.durability(uses);}}
    public ItemToolAbility setDepthRockBreaker(){depthRockBreaker=true;return this;}
    public ItemToolAbility(String sourceName,float damage,Type type,AvailableAbilities abilities){this(sourceName,damage,0,SteelTier.INSTANCE,type,abilities);}
    public ItemToolAbility(String sourceName,float damage,double movement,Tier material,Type type,AvailableAbilities abilities){
        this(sourceName,damage,movement,material,type,abilities,new SourceProperties());
    }
    /** Actual source fueled/powered subclasses can declare their raw maxDamage before native registration. */
    protected ItemToolAbility(String sourceName,float damage,double movement,Tier material,Type type,AvailableAbilities abilities,Properties properties){
        super(material,type.tag,properties.attributes(ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,damage,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND)
            .add(Attributes.MOVEMENT_SPEED,new AttributeModifier(ResourceLocation.parse("hbm:source_tool_movement"),movement,AttributeModifier.Operation.ADD_MULTIPLIED_BASE),EquipmentSlotGroup.MAINHAND).build()));
        this.sourceName=sourceName;this.type=type;this.abilities=abilities;
    }
    @Override public AvailableAbilities abilities(){return abilities;}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+sourceName+".name");}
    @Override public float getDestroySpeed(ItemStack stack,BlockState state){
        if(!canOperate(stack))return 1;
        String material=LegacyMaterials.of(state).name();
        boolean fast=switch(type){
            case MINER->switch(material){case "grass","iron","anvil","glass","rock","clay","sand","ground","snow","craftedSnow","crafted_snow"->true;default->false;};
            case PICKAXE->switch(material){case "iron","anvil","rock","glass"->true;default->state.is(Blocks.ICE)||state.is(BlockTags.RAILS);};
            case AXE->switch(material){case "wood","plants","vine"->true;default->state.is(Blocks.PUMPKIN)||state.is(Blocks.CARVED_PUMPKIN)||state.is(Blocks.JACK_O_LANTERN);};
            case SHOVEL->switch(material){case "clay","sand","ground","snow","craftedSnow","crafted_snow"->true;default->state.is(Blocks.GRASS_BLOCK)||state.is(Blocks.FARMLAND)||state.is(Blocks.MYCELIUM)||state.is(Blocks.SOUL_SAND);};
        };
        return fast?getTier().getSpeed():1;
    }
    @Override public boolean sourceCanHarvest(ItemStack stack,BlockState state){return canOperate(stack)&&(CobaltPresets.read(stack).active().harvest==1||getDestroySpeed(stack,state)>1);}
    @Override public boolean isCorrectToolForDrops(ItemStack stack,BlockState state){return canOperate(stack)&&(CobaltPresets.read(stack).active().harvest==1||!state.requiresCorrectToolForDrops()||(state.is(type.tag)||type==Type.MINER&&state.is(BlockTags.MINEABLE_WITH_SHOVEL))&&!state.is(getTier().getIncorrectBlocksForDrops()));}
    @Override public boolean canBreakRock(BlockGetter level,Player player,ItemStack tool,BlockState rock,BlockPos pos){return canOperate(tool)&&depthRockBreaker;}
    @Override public boolean hurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){if(!attacker.level().isClientSide&&attacker instanceof Player player&&canOperate(stack))abilities.onHit(player,victim);return true;}
    @Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){com.hbm.weapon.legacymelee.LegacySwordWear.damage(stack,1,attacker);}
    @Override public boolean mineBlock(ItemStack stack,Level level,BlockState state,BlockPos pos,LivingEntity miner){if(state.getDestroySpeed(level,pos)!=0)com.hbm.weapon.legacymelee.LegacySwordWear.damage(stack,1,miner);return true;}
    @Override public boolean isFoil(ItemStack stack){return stack.has(DataComponents.ENCHANTMENTS)&&!stack.get(DataComponents.ENCHANTMENTS).isEmpty()||!CobaltPresets.read(stack).active().isNone();}
    @Override public boolean canPerformAction(ItemStack stack,ItemAbility ability){return type==Type.MINER?(ability.equals(ItemAbilities.PICKAXE_DIG)||ability.equals(ItemAbilities.SHOVEL_DIG)):ability.equals(switch(type){case MINER,PICKAXE->ItemAbilities.PICKAXE_DIG;case AXE->ItemAbilities.AXE_DIG;case SHOVEL->ItemAbilities.SHOVEL_DIG;});}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){abilities.addInformation(tooltip);if(depthRockBreaker){tooltip.add(Component.empty());tooltip.add(Component.translatable("tooltip.hbm.tool_ability.depth_rock").withStyle(net.minecraft.ChatFormatting.RED));}}
}
