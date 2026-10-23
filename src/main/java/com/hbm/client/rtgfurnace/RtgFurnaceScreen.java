package com.hbm.client.rtgfurnace;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.rtgfurnace.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Exact original176x166 GUI, resource positions and progress-width+1. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RtgFurnaceScreen extends LegacyContainerScreen<RtgFurnaceMenu> {
    public RtgFurnaceScreen(RtgFurnaceMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/rtgfurnace.png"),176,166);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RtgFurnaceContent.MENU.get(),RtgFurnaceScreen::new);}
    @Override protected void renderLabels(GuiGraphics graphics,int mouseX,int mouseY){graphics.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY){super.renderBg(graphics,partial,mouseX,mouseY);if(menu.hasPower())drawLegacyRegion(graphics,55,35,176,0,18,16);drawLegacyRegion(graphics,79,34,176,16,menu.scaledProgress(24)+1,17);}
}
