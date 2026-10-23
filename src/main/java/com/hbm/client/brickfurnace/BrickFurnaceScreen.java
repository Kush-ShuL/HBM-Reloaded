package com.hbm.client.brickfurnace;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.brickfurnace.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/** Original 176x166 GUI; progress is drawn only while burning, exactly as the source. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BrickFurnaceScreen extends LegacyContainerScreen<BrickFurnaceMenu> {
    public BrickFurnaceScreen(BrickFurnaceMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_furnace_brick.png"),176,166);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(BrickFurnaceContent.MENU.get(),BrickFurnaceScreen::new);}
    @Override protected void renderLabels(GuiGraphics graphics,int mouseX,int mouseY){graphics.drawString(font,title,imageWidth/2-font.width(title)/2,6,0xffffff,false);graphics.drawString(font,playerInventoryTitle,8,imageHeight-96+2,0xffffff,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY){super.renderBg(graphics,partial,mouseX,mouseY);if(menu.burnTime()>0){int burn=menu.maxBurnTime()==0?0:menu.burnTime()*13/menu.maxBurnTime();drawLegacyRegion(graphics,62,54+12-burn,176,12-burn,14,burn+1);drawLegacyRegion(graphics,85,34,176,14,menu.progress()*24/200+1,16);}}
}
