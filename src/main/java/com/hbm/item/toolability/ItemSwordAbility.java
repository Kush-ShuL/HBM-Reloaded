package com.hbm.item.toolability;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.item.legacysword.LegacySwordBlocking;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.*;

/** Source sword passive abilities, blocking and explicit attack modifier, with no mining preset GUI. */
public class ItemSwordAbility extends SwordItem implements SourceAbilityCombat,LegacySwordBlocking {
    private final String sourceName;private final AvailableAbilities abilities;
    /** Source maxDamage0 means no damage component; modern MAX_DAMAGE has a positive codec. */
    protected static class SourceProperties extends Properties {
        public SourceProperties(){}
        @Override public Properties durability(int uses){return uses==0?stacksTo(1):super.durability(uses);}
    }
    /** Null retains original vanilla enchanted rarity; an explicit source override remains fixed. */
    public Rarity sourceRarityOverride(){return null;}
    public ItemSwordAbility(String sourceName,float damage,AvailableAbilities abilities){this(sourceName,damage,0,SteelTier.INSTANCE,abilities);}
    public ItemSwordAbility(String sourceName,float damage,double movement,Tier material,AvailableAbilities abilities){this(sourceName,damage,movement,material,abilities,new SourceProperties());}
    protected ItemSwordAbility(String sourceName,float damage,double movement,Tier material,AvailableAbilities abilities,Properties properties){super(material,properties.attributes(ItemAttributeModifiers.builder()
        .add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,damage,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND)
        .add(Attributes.MOVEMENT_SPEED,new AttributeModifier(net.minecraft.resources.ResourceLocation.parse("hbm:source_tool_movement"),movement,AttributeModifier.Operation.ADD_MULTIPLIED_BASE),EquipmentSlotGroup.MAINHAND).build()));this.sourceName=sourceName;this.abilities=abilities;}
    @Override public Component getName(ItemStack stack){return Component.translatable("item."+sourceName+".name");}
    @Override public boolean hurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){if(!attacker.level().isClientSide&&attacker instanceof Player player&&canOperate(stack))abilities.onHit(player,victim);return true;}
    @Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){com.hbm.weapon.legacymelee.LegacySwordWear.damage(stack,1,attacker);}
    @Override public boolean mineBlock(ItemStack stack,Level level,BlockState state,BlockPos pos,LivingEntity miner){if(state.getDestroySpeed(level,pos)!=0)com.hbm.weapon.legacymelee.LegacySwordWear.damage(stack,2,miner);return true;}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.BLOCK;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 72000;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){player.startUsingItem(hand);return InteractionResultHolder.consume(player.getItemInHand(hand));}
    @Override public boolean canPerformAction(ItemStack stack,ItemAbility ability){return !ability.equals(ItemAbilities.SWORD_SWEEP)&&!ability.equals(ItemAbilities.SHIELD_BLOCK)&&super.canPerformAction(stack,ability);}
    @Override public float getDestroySpeed(ItemStack stack,BlockState state){if(state.is(Blocks.COBWEB))return 15;return switch(LegacyMaterials.of(state).name()){case "plants","vine","coral","leaves","gourd"->1.5F;default->1;};}
    @Override public boolean isCorrectToolForDrops(ItemStack stack,BlockState state){return state.is(Blocks.COBWEB);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){abilities.addInformation(tooltip);}
}
