package com.hbm.armor;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.EventHooks;

/** Original hfrFilter attachment and filter wear order, including the > max removal boundary. */
public final class GasMaskFilters {
    public static final String KEY="hfrFilter";
    private GasMaskFilters(){}
    public static ItemStack filter(ItemStack mask,HolderLookup.Provider lookup){return mask.isEmpty()?ItemStack.EMPTY:ArmorModStorage.parse(ArmorModStorage.data(mask).getCompound(KEY),lookup);}
    public static ItemStack recursive(ItemStack mask,LivingEntity entity){
        ItemStack filter=filter(mask,entity.level().registryAccess());
        if(filter.isEmpty()&&ArmorModStorage.hasMods(mask)){
            ItemStack attachment=ArmorModStorage.pryMod(mask,ArmorModStorage.HELMET,entity.level().registryAccess());
            if(attachment.getItem() instanceof GasMask item)filter=item.filter(attachment,entity);
        }
        return filter;
    }
    public static void install(ItemStack mask,ItemStack filter,HolderLookup.Provider lookup){if(mask.isEmpty()||filter.isEmpty())return;var data=ArmorModStorage.data(mask);data.put(KEY,filter.save(ArmorModStorage.lookup(lookup)));ArmorModStorage.data(mask,data);}
    public static void remove(ItemStack mask){if(mask.isEmpty())return;var data=ArmorModStorage.data(mask);data.remove(KEY);ArmorModStorage.data(mask,data);}
    public static void damage(LivingEntity entity,int damage){
        ItemStack mask=entity.getItemBySlot(EquipmentSlot.HEAD);if(mask.isEmpty())return;
        if(!(mask.getItem() instanceof GasMask)&&ArmorModStorage.hasMods(mask)){
            ItemStack attachment=ArmorModStorage.pryMod(mask,ArmorModStorage.HELMET,entity.level().registryAccess());
            if(attachment.getItem() instanceof GasMask)mask=attachment;
        }
        damage(mask,damage,entity.level().registryAccess());
        // ArmorUtil's original attachment damage path operates on the extracted
        // copy and does not applyMod it back. Retain that source behavior.
    }
    public static void damage(ItemStack mask,int damage,HolderLookup.Provider lookup){
        ItemStack filter=filter(mask,lookup);
        if(filter.isEmpty()&&ArmorModStorage.hasMods(mask)){
            ItemStack attachment=ArmorModStorage.pryMod(mask,ArmorModStorage.HELMET,lookup);
            if(attachment.getItem() instanceof GasMask)filter=filter(attachment,lookup);
        }
        if(filter.isEmpty()||filter.getMaxDamage()==0)return;
        int next=filter.getDamageValue()+damage;
        if(next>filter.getMaxDamage())remove(mask);else{filter.setDamageValue(next);install(mask,filter,lookup);}
    }
    public static void tooltip(ItemStack mask,Item.TooltipContext context,List<Component> lines,TooltipFlag flags){
        ItemStack filter=filter(mask,context.registries());
        if(filter.isEmpty()){lines.add(Component.translatable("tooltip.hbm.gasmask.no_filter").withStyle(ChatFormatting.RED));return;}
        lines.add(Component.translatable("tooltip.hbm.gasmask.installed_filter").withStyle(ChatFormatting.GOLD));
        String percent=filter.getMaxDamage()>0?" ("+((filter.getMaxDamage()-filter.getDamageValue())*100/filter.getMaxDamage())+"%)":"";
        lines.add(Component.literal("  ").append(filter.getHoverName()).append(percent));
        var lore=new java.util.ArrayList<Component>();
        filter.getItem().appendHoverText(filter,context,lore,flags);
        EventHooks.onItemTooltip(filter,null,lore,flags,context);
        for(Component line:lore)lines.add(Component.literal("  ").append(line).withStyle(ChatFormatting.YELLOW));
    }
}
