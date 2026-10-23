package com.hbm.world.structure.loot;
import com.hbm.armor.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
/** Real source ItemModMorningGlory, an extra armor mod, not a decorative crafting placeholder. */
public final class MorningGloryItem extends ArmorModItem {
    public MorningGloryItem(){super(ArmorModStorage.EXTRA,true,true,true,true);}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.morning_glory.name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.morning_glory").withStyle(ChatFormatting.LIGHT_PURPLE));lines.add(Component.empty());super.appendHoverText(stack,context,lines,flag);}
    public static void register(){NeoForge.EVENT_BUS.addListener(MorningGloryItem::tick);NeoForge.EVENT_BUS.addListener(MorningGloryItem::damaged);}
    private static int equipped(LivingEntity living){int count=0;for(var slot:new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD}){var armor=living.getItemBySlot(slot);if(ArmorModStorage.hasMods(armor)&&ArmorModStorage.pryMod(armor,ArmorModStorage.EXTRA,living.level().registryAccess()).getItem() instanceof MorningGloryItem)count++;}return count;}
    private static void tick(EntityTickEvent.Post event){if(event.getEntity() instanceof LivingEntity living&&!living.level().isClientSide&&equipped(living)>0&&living.hasEffect(MobEffects.WITHER))living.removeEffect(MobEffects.WITHER);}
    private static void damaged(LivingIncomingDamageEvent event){var living=event.getEntity();if(living.level().isClientSide)return;for(int i=0,count=equipped(living);i<count;i++)if(living.getRandom().nextInt(20)==0)living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,100,4));}
}
