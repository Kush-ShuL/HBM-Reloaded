package com.hbm.client.armor;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.armor.HazmatRegistry;
import com.hbm.exposure.ExposureProtection.Hazard;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.glfw.GLFW;

/** Source protection, cladding and installed-mod sections of drawTooltip. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class HazmatTooltips {
    private static final Map<Hazard,String> LANG=Map.of(Hazard.GAS_LUNG,"hazard.gasChlorine",Hazard.GAS_MONOXIDE,"hazard.gasMonoxide",Hazard.GAS_INERT,"hazard.gasInert",Hazard.PARTICLE_COARSE,"hazard.particleCoarse",Hazard.PARTICLE_FINE,"hazard.particleFine",Hazard.BACTERIA,"hazard.bacteria",Hazard.GAS_BLISTERING,"hazard.corrosive",Hazard.SAND,"hazard.sand",Hazard.LIGHT,"hazard.light");
    private HazmatTooltips(){}
    private static Component hint(String key){return Component.translatable(key,Component.translatable("key.keyboard.left.shift").withStyle(ChatFormatting.YELLOW,ChatFormatting.ITALIC)).withStyle(ChatFormatting.DARK_GRAY,ChatFormatting.ITALIC);}
    @SubscribeEvent(priority=EventPriority.HIGH) public static void tooltip(ItemTooltipEvent event){
        var stack=event.getItemStack();var text=event.getToolTip();var client=Minecraft.getInstance();
        boolean shift=InputConstants.isKeyDown(client.getWindow().getWindow(),GLFW.GLFW_KEY_LEFT_SHIFT);
        var hazards=HazmatRegistry.baseHazards(stack);
        if(hazards!=null){if(shift){text.add(Component.translatable("hazard.prot").withStyle(ChatFormatting.GOLD));for(var hazard:hazards)text.add(Component.literal("  ").append(Component.translatable(LANG.get(hazard))).withStyle(ChatFormatting.YELLOW));}else text.add(hint("tooltip.hbm.armor.protection_hint"));}
        double rad=HazmatRegistry.getResistance(stack,event.getContext().registries());rad=((int)(rad*1000))/1000D;
        if(rad>0)text.add(Component.translatable("trait.radResistance",rad).withStyle(ChatFormatting.YELLOW));
        if(stack.getItem() instanceof ArmorItem&&ArmorModStorage.hasMods(stack)){
            if(!shift&&!(client.screen instanceof ArmorTableScreen))text.add(hint("tooltip.hbm.armor.mods_hint"));
            else{text.add(Component.translatable("tooltip.hbm.armor.mods").withStyle(ChatFormatting.YELLOW));var mods=ArmorModStorage.pryMods(stack,event.getContext().registries());for(int index=0;index<8;index++)if(mods[index].getItem() instanceof ArmorModItem installed){text.add(mods[index].getItem() instanceof com.hbm.equipment.jetpack.SourceJetpackItem jetpack?jetpack.installedDescription(mods[index]):mods[index].getItem() instanceof com.hbm.entity.mob.maskman.BottledCloudItem cloud?cloud.installedDescription(mods[index]):mods[index].getItem() instanceof com.hbm.world.meteor.MeteorCharmItem meteor?meteor.installedDescription(mods[index]):mods[index].getItem() instanceof com.hbm.item.redroom.RedroomArmorModItem redroom?redroom.installedDescription(mods[index]):mods[index].getItem() instanceof com.hbm.item.medicine.MedicineArmorModItem medical?medical.installedDescription(mods[index]):mods[index].getItem() instanceof com.hbm.item.defuser.GoldenDefuserItem defuser?defuser.installedDescription(mods[index]):mods[index].getItem() instanceof com.hbm.item.structureloot.InkArmorModItem ink?ink.installedDescription(mods[index]):mods[index].getItem() instanceof com.hbm.item.treasure.HealthArmorModItem health?health.installedDescription(mods[index]):mods[index].getItem() instanceof com.hbm.item.treasure.ReviveArmorModItem revive?revive.installedDescription(mods[index]):mods[index].getItem() instanceof com.hbm.machine.pedestal.PedestalCardItem card?card.installedDescription(mods[index]):((ArmorModItem)mods[index].getItem()).installedDescription(mods[index],stack));installed.appendInstalledDetails(mods[index],stack,event.getContext(),text,event.getFlags());}}
        }
    }
}
