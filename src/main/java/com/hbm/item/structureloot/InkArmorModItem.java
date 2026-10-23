package com.hbm.item.structureloot;

import com.hbm.armor.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/** Source ItemModInk: independently rolls each equipped extra mod, cancels damage and drops actual flower variants. */
public final class InkArmorModItem extends ArmorModItem {
    private static final Item[] RED_FLOWERS={Items.POPPY,Items.BLUE_ORCHID,Items.ALLIUM,Items.AZURE_BLUET,Items.RED_TULIP,Items.ORANGE_TULIP,Items.WHITE_TULIP,Items.PINK_TULIP,Items.OXEYE_DAISY};
    public InkArmorModItem(){super(ArmorModStorage.EXTRA,true,true,true,true);}
    @Override public Component getName(ItemStack stack){return Component.translatable("item.ink.name");}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> tooltip,TooltipFlag flag){tooltip.add(Component.translatable("tooltip.hbm.ink.nullify").withStyle(ChatFormatting.LIGHT_PURPLE));tooltip.add(Component.translatable("tooltip.hbm.ink.flowers").withStyle(ChatFormatting.LIGHT_PURPLE));tooltip.add(Component.empty());super.appendHoverText(stack,context,tooltip,flag);}
    public Component installedDescription(ItemStack stack){return Component.translatable("tooltip.hbm.ink.installed",stack.getHoverName()).withStyle(ChatFormatting.LIGHT_PURPLE);}
    public static void register(){NeoForge.EVENT_BUS.addListener(InkArmorModItem::damaged);}
    private static void damaged(LivingIncomingDamageEvent event){
        var living=event.getEntity();if(living.level().isClientSide)return;
        for(var slot:new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST,EquipmentSlot.HEAD}){
            var armor=living.getItemBySlot(slot);if(!ArmorModStorage.hasMods(armor)||!(ArmorModStorage.pryMod(armor,ArmorModStorage.EXTRA,living.level().registryAccess()).getItem() instanceof InkArmorModItem))continue;
            var random=living.level().random;if(random.nextInt(10)!=0)continue;event.setAmount(0);
            if(random.nextInt(10)==0)living.spawnAtLocation(new ItemStack(Items.DANDELION),1);
            living.spawnAtLocation(new ItemStack(RED_FLOWERS[random.nextInt(9)]),1);
        }
    }
}
