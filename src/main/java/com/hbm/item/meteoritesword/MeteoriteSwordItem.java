package com.hbm.item.meteoritesword;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.item.legacysword.LegacySwordBlocking;
import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.*;
import net.minecraft.world.*;
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
/** True source ItemSwordMeteorite with empty original abilities, material/lore/blocking and indestructible0uses. */
public final class MeteoriteSwordItem extends SwordItem implements LegacySwordBlocking {
 public static final Tier MATERIAL=new Tier(){public int getUses(){return 0;}public float getSpeed(){return 50F;}public float getAttackDamageBonus(){return 0;}public int getEnchantmentValue(){return 200;}public TagKey<Block> getIncorrectBlocksForDrops(){return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;}public Ingredient getRepairIngredient(){return Ingredient.of(HbmContent.item("plate_paa"));}};
 private static final class UndamageableProperties extends Properties {@Override public Properties durability(int uses){return uses==0?stacksTo(1):super.durability(uses);}}
 public final String sourceName;public final float red,green,blue;
 public MeteoriteSwordItem(String name,float damage,float red,float green,float blue){super(MATERIAL,new UndamageableProperties().attributes(ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,damage,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND).add(Attributes.MOVEMENT_SPEED,new AttributeModifier(BASE_ATTACK_DAMAGE_ID,0,AttributeModifier.Operation.ADD_MULTIPLIED_BASE),EquipmentSlotGroup.MAINHAND).add(Attributes.ATTACK_SPEED,new AttributeModifier(BASE_ATTACK_SPEED_ID,-2.4F,AttributeModifier.Operation.ADD_VALUE),EquipmentSlotGroup.MAINHAND).build()));sourceName=name;this.red=red;this.green=green;this.blue=blue;}
 @Override public Component getName(ItemStack stack){return Component.translatable("item."+sourceName+".name");}@Override public boolean hurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){return true;}@Override public void postHurtEnemy(ItemStack stack,LivingEntity victim,LivingEntity attacker){} // Old damageItem(1) on maxDamage0 is a no-op, no abilities are assigned to these source instances.
 @Override public UseAnim getUseAnimation(ItemStack stack){return UseAnim.BLOCK;}@Override public int getUseDuration(ItemStack stack,LivingEntity entity){return 72000;}@Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){player.startUsingItem(hand);return InteractionResultHolder.consume(player.getItemInHand(hand));}@Override public boolean canPerformAction(ItemStack stack,ItemAbility ability){return !ability.equals(ItemAbilities.SWORD_SWEEP)&&!ability.equals(ItemAbilities.SHIELD_BLOCK)&&super.canPerformAction(stack,ability);}
 @Override public float getDestroySpeed(ItemStack stack,BlockState state){if(state.is(Blocks.COBWEB))return 15;return switch(LegacyMaterials.of(state).name()){case "plants","vine","coral","leaves","gourd"->1.5F;default->1;};}@Override public boolean isCorrectToolForDrops(ItemStack stack,BlockState state){return state.is(Blocks.COBWEB);}
 @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){for(var line:Language.getInstance().getOrDefault("item."+sourceName+".desc").split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.ITALIC));}
}
