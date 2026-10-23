package com.hbm.item.starmetal;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.item.ability.SourceWeaponDrops;
import com.hbm.item.legacysword.LegacySwordBlocking;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.*;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.*;

/** Original25 attack modifier, HBM_STARMETAL and its three actual weapon abilities. */
public final class StarmetalSwordItem extends SwordItem implements LegacySwordBlocking {
    private static final Tier MATERIAL=new Tier(){
        public int getUses(){return 3000;}public float getSpeed(){return 20;}public float getAttackDamageBonus(){return 2.5F;}
        public TagKey<Block> getIncorrectBlocksForDrops(){return BlockTags.INCORRECT_FOR_DIAMOND_TOOL;}
        public int getEnchantmentValue(){return 100;}public Ingredient getRepairIngredient(){return Ingredient.of(HbmContent.item("ingot_starmetal"));}
    };
    private enum Ability { STUN,BEHEADER,BOBBLE }
    private final HashMap<Ability,Integer> abilities=new HashMap<>();
    public StarmetalSwordItem(){super(MATERIAL,new Properties().attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,25,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND).build()));abilities.put(Ability.BEHEADER,0);abilities.put(Ability.STUN,1);abilities.put(Ability.BOBBLE,0);}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.starmetal_sword.name");}
    @Override public boolean hurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){
        if(!attacker.level().isClientSide&&attacker instanceof Player){
            abilities.keySet().stream().collect(java.util.stream.Collectors.toMap(key->key,abilities::get)).forEach((ability,tier)->{
                switch(ability){case STUN->{victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,60,4));victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,60,4));}case BEHEADER->{if(victim.getHealth()<=0)SourceWeaponDrops.behead(victim);}case BOBBLE->SourceWeaponDrops.bobble(victim);}
            });
        }
        return true;
    }
    @Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){stack.hurtAndBreak(1,attacker,EquipmentSlot.MAINHAND);}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.BLOCK;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 72000;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){player.startUsingItem(hand);return InteractionResultHolder.consume(player.getItemInHand(hand));}
    @Override public boolean canPerformAction(ItemStack stack,ItemAbility ability){return !ability.equals(ItemAbilities.SWORD_SWEEP)&&!ability.equals(ItemAbilities.SHIELD_BLOCK)&&super.canPerformAction(stack,ability);}
    @Override public float getDestroySpeed(ItemStack stack,BlockState state){if(state.is(Blocks.COBWEB))return 15;return switch(LegacyMaterials.of(state).name()){case "plants","vine","coral","leaves","gourd"->1.5F;default->1;};}
    @Override public boolean isCorrectToolForDrops(ItemStack stack,BlockState state){return state.is(Blocks.COBWEB);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){tooltip.add(Component.translatable("tooltip.hbm.gavel.weapon_modifiers"));tooltip.add(Component.literal("  ").append(Component.translatable("weapon.ability.stun")).append(" (3)").withStyle(ChatFormatting.RED));for(String ability:new String[]{"beheader","bobble"})tooltip.add(Component.literal("  ").append(Component.translatable("weapon.ability."+ability)).withStyle(ChatFormatting.RED));}
}
