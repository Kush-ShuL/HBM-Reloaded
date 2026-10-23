package com.hbm.item.bottle;

import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.item.legacysword.LegacySwordBlocking;
import com.hbm.registry.HbmContent;
import java.util.List;
import java.util.Random;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.*;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.*;

/** Actual WeaponSpecial bottle-opener branch, not a generic key item. */
public final class BottleOpenerItem extends SwordItem implements LegacySwordBlocking {
    private static final Tier MATERIAL=new Tier(){
        @Override public int getUses(){return 250;}
        @Override public float getSpeed(){return 1.5F;}
        @Override public float getAttackDamageBonus(){return .5F;}
        @Override public TagKey<Block> getIncorrectBlocksForDrops(){return BlockTags.INCORRECT_FOR_STONE_TOOL;}
        @Override public int getEnchantmentValue(){return 200;}
        @Override public Ingredient getRepairIngredient(){return Ingredient.of(HbmContent.item("plate_steel"));}
    };
    private final Random random=new Random();
    public BottleOpenerItem(){super(MATERIAL,new Properties().attributes(SwordItem.createAttributes(MATERIAL,4,-2.4F)));}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.bottle_opener.name");}
    @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.BLOCK;}
    @Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 72000;}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){player.startUsingItem(hand);return InteractionResultHolder.consume(player.getItemInHand(hand));}
    @Override public boolean canPerformAction(ItemStack stack,ItemAbility action){return !action.equals(ItemAbilities.SWORD_SWEEP)&&!action.equals(ItemAbilities.SHIELD_BLOCK)&&super.canPerformAction(stack,action);}
    @Override public float getDestroySpeed(ItemStack stack,BlockState state){if(state.is(Blocks.COBWEB))return 15;return switch(LegacyMaterials.of(state).name()){case "plants","vine","coral","leaves","gourd"->1.5F;default->1;};}
    @Override public boolean isCorrectToolForDrops(ItemStack stack,BlockState state){return state.is(Blocks.COBWEB);}
    @Override public boolean hurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){
        if(!victim.level().isClientSide){int roll=random.nextInt(7);Holder<MobEffect> effect=switch(roll){case 0->MobEffects.BLINDNESS;case 1->MobEffects.MOVEMENT_SLOWDOWN;case 2->MobEffects.DIG_SLOWDOWN;case 3->MobEffects.CONFUSION;default->null;};if(effect!=null)victim.addEffect(new MobEffectInstance(effect,roll==3?1200:6000,roll==1||roll==2?2:0));}
        var level=victim.level();if(level.isClientSide)level.playLocalSound(victim.getX(),victim.getY(),victim.getZ(),SoundEvents.ANVIL_LAND,SoundSource.PLAYERS,3,1,false);else level.playSound(null,victim.getX(),victim.getY(),victim.getZ(),SoundEvents.ANVIL_LAND,SoundSource.PLAYERS,3,1);
        // Source returns false without calling ItemSword.hitEntity: no hit durability.
        return false;
    }
    @Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){}
    @Override public void inventoryTick(ItemStack stack,Level level,Entity holder,int slot,boolean selected){if(holder instanceof ServerPlayer player){if(equipped(player,"jackt","shimmer_sledge"))award(player,"fiend");else if(equipped(player,"jackt2","shimmer_axe"))award(player,"fiend2");}}
    private static boolean equipped(Player player,String armor,String weapon){var registry=net.minecraft.core.registries.BuiltInRegistries.ITEM;var chest=registry.getOptional(ResourceLocation.parse("hbm:"+armor));var held=registry.getOptional(ResourceLocation.parse("hbm:"+weapon));return chest.isPresent()&&held.isPresent()&&player.getItemBySlot(EquipmentSlot.CHEST).is(chest.get())&&player.getMainHandItem().is(held.get());}
    private static void award(ServerPlayer player,String id){var advancement=player.server.getAdvancements().get(ResourceLocation.parse("hbm:"+id));if(advancement!=null)for(String criterion:advancement.value().criteria().keySet())player.getAdvancements().award(advancement,criterion);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){tooltip.add(Component.translatable("tooltip.hbm.bottle_opener.0"));tooltip.add(Component.translatable("tooltip.hbm.bottle_opener.1"));}

    @Override public boolean mineBlock(ItemStack stack,Level level,BlockState state,net.minecraft.core.BlockPos pos,LivingEntity miner){if(state.getDestroySpeed(level,pos)!=0)com.hbm.weapon.legacymelee.LegacySwordWear.damage(stack,2,miner);return true;}
}
