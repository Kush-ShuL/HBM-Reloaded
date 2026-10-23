package com.hbm.client.fensu;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.fensu.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** Source176×181 GUI, original texture UVs, half-scale exact bigint labels and original three hitboxes. */
public final class FensuScreen extends LegacyContainerScreen<FensuMenu> {
    public FensuScreen(FensuMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/storage/gui_battery_redd.png"),176,181);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY){super.renderBg(graphics,partial,mouseX,mouseY);drawLegacyRegion(graphics,133,16,176,52+menu.redLow()*18,18,18);drawLegacyRegion(graphics,133,52,176,52+menu.redHigh()*18,18,18);drawLegacyRegion(graphics,152,35,194,52+menu.priority()*16-16,16,16);}
    @Override protected void renderLabels(GuiGraphics graphics,int mouseX,int mouseY){graphics.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);graphics.pose().pushPose();graphics.pose().scale(.5F,.5F,1);String stored=FensuNumbers.format(menu.power())+" HE";graphics.drawString(font,stored,242-font.width(stored),45,0x00ff00,false);int comparison=menu.delta().signum();String delta=(comparison>0?ChatFormatting.GREEN.toString()+"+":comparison<0?ChatFormatting.RED.toString():ChatFormatting.YELLOW.toString()+"+")+FensuNumbers.format(menu.delta())+" HE/s";graphics.drawString(font,delta,242-font.width(delta),65,0x00ff00,false);graphics.pose().popPose();}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);int action=legacyInfoHovered(x,y,133,16,18,18)?0:legacyInfoHovered(x,y,133,52,18,18)?1:legacyInfoHovered(x,y,152,35,16,16)?2:-1;if(action<0||minecraft==null||minecraft.gameMode==null)return handled;minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));minecraft.gameMode.handleInventoryButtonClick(menu.containerId,action);return true;}
}
