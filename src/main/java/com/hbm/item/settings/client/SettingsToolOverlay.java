package com.hbm.item.settings.client;

import com.hbm.item.settings.SettingsToolItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Source five-tick PlayerInform notices use the server-owned held-stack displayInfo/copyIndex sync. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class SettingsToolOverlay {
    private SettingsToolOverlay() {}
    @SubscribeEvent public static void tick(ClientTickEvent.Post event) {
        var minecraft=Minecraft.getInstance();if(minecraft.level==null||minecraft.player==null||minecraft.isPaused()||minecraft.level.getGameTime()%5!=0) return;
        var stack=minecraft.player.getMainHandItem();if(!(stack.getItem() instanceof SettingsToolItem)||!stack.has(DataComponents.CUSTOM_DATA)) return;
        var data=stack.get(DataComponents.CUSTOM_DATA).copyTag();var info=data.getList("displayInfo",Tag.TAG_COMPOUND);int selected=data.getInt("copyIndex");
        for(int index=0;index<info.size();index++) LegacyInfoSystem.push(Component.translatable(info.getCompound(index).getString("info")).withStyle(selected==index?ChatFormatting.AQUA:ChatFormatting.YELLOW),4000,897+index);
    }
}
