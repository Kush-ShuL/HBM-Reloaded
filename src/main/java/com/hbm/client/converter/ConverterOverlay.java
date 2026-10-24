package com.hbm.client.converter;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.item.battery.BatteryItem;
import com.hbm.machine.converter.ConverterBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

/** Original two ILookOverlay lines, with source names/units, arrow colors and exact placement. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ConverterOverlay {
    private ConverterOverlay(){}
    @SubscribeEvent public static void render(RenderGuiEvent.Post event){if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;
        var mc=Minecraft.getInstance();if(mc.options.hideGui||mc.level==null||!(mc.hitResult instanceof BlockHitResult hit)||!(mc.level.getBlockEntity(hit.getBlockPos()) instanceof ConverterBlockEntity converter))return;
        var graphics=event.getGuiGraphics();var font=LegacyFonts.get();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2;var title=converter.getBlockState().getBlock().getName();
        graphics.drawString(font,title,x+1,y-9,0x404000,false);graphics.drawString(font,title,x,y-10,0xFFFF00,false);
        String he=BatteryItem.shortNumber(converter.getPower())+"HE",rf=BatteryItem.shortNumber(converter.rfStored())+"RF";
        graphics.drawString(font,Component.literal("-> ").withStyle(ChatFormatting.GREEN).append(Component.literal(converter.heToRf()?he:rf).withStyle(ChatFormatting.WHITE)),x,y,0xFFFFFF,true);
        graphics.drawString(font,Component.literal("<- ").withStyle(ChatFormatting.RED).append(Component.literal(converter.heToRf()?rf:he).withStyle(ChatFormatting.WHITE)),x,y+10,0xFFFFFF,true);
    }
}
