package com.hbm.armor.taurun;

import com.hbm.armor.LegacyArmorDurability;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Actual ArmorTaurun extends source ArmorFSB: maxDamage0, source armor material and full-set lore. */
public final class ArmorTaurun extends ArmorItem implements LegacyArmorDurability {
    public ArmorTaurun(Holder<ArmorMaterial> material,Type slot){super(material,slot,new Item.Properties().stacksTo(1));}
    @Override public InteractionResultHolder<ItemStack> use(Level level,Player player,InteractionHand hand){
        ItemStack stack=player.getItemInHand(hand);
        if(!player.getItemBySlot(getEquipmentSlot()).isEmpty())return InteractionResultHolder.pass(stack);
        player.setItemSlot(getEquipmentSlot(),stack.copy());stack.setCount(0);
        return InteractionResultHolder.sidedSuccess(stack,level.isClientSide);
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){
        super.appendHoverText(stack,context,tooltip,flag);
        tooltip.add(Component.translatable("armor.fullSetBonus").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("effect.minecraft.strength").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.literal("  ").append(Component.translatable("armor.stepSize",1)).withStyle(ChatFormatting.BLUE));
    }
}
