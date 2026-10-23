package com.hbm.client.batterysocket;

import com.hbm.HbmReloaded;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.battery.BatteryItem;
import com.hbm.item.battery.HbmBattery;
import com.hbm.machine.batterysocket.BatterySocketMenu;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

/** Original 176x181 GUI, 35/35 slot and 106/125 controls with the original34x52 charge graphic. */
public final class BatterySocketScreen extends LegacyContainerScreen<BatterySocketMenu> {
    public BatterySocketScreen(BatterySocketMenu menu,Inventory inventory,Component title){super(menu,inventory,title,HbmReloaded.id("textures/gui/storage/gui_battery_socket.png"),176,181);}
    @Override protected void renderLabels(GuiGraphics graphics,int mouseX,int mouseY){graphics.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partialTick,int mouseX,int mouseY){
        super.renderBg(graphics,partialTick,mouseX,mouseY);
        if(menu.getSlot(0).getItem().getItem() instanceof HbmBattery){long power=menu.rawPower(),capacity=menu.capacity();if(power>Long.MAX_VALUE/100){power/=100;capacity/=100;}if(capacity<=1)capacity=1;int p=(int)(power*52/capacity);drawLegacyRegion(graphics,62,69-p,176,52-p,34,p);}
        drawLegacyRegion(graphics,106,16,176,52+menu.redLow()*18,18,18);drawLegacyRegion(graphics,106,52,176,52+menu.redHigh()*18,18,18);drawLegacyRegion(graphics,125,35,194,52+menu.priority()*16-16,16,16);
    }
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);int action=legacyInfoHovered(x,y,106,16,18,18)?0:legacyInfoHovered(x,y,106,52,18,18)?1:legacyInfoHovered(x,y,125,35,16,16)?2:-1;if(action<0||minecraft==null||minecraft.gameMode==null)return handled;minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));minecraft.gameMode.handleInventoryButtonClick(menu.containerId,action);return true;}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partialTick){
        super.render(graphics,mouseX,mouseY,partialTick);
        if(menu.getSlot(0).getItem().getItem() instanceof HbmBattery&&legacyInfoHovered(mouseX,mouseY,62,17,34,52)){
            long delta=menu.delta();String text=(delta<0?"-":"+")+BatteryItem.shortNumber(Math.abs(delta))+"HE/s";
            graphics.renderComponentTooltip(font,List.of(Component.literal(BatteryItem.shortNumber(menu.rawPower())+"/"+BatteryItem.shortNumber(menu.capacity())+"HE"),Component.literal(text).withStyle(delta>0?ChatFormatting.GREEN:delta<0?ChatFormatting.RED:ChatFormatting.YELLOW)),mouseX,mouseY);
        }
        if(legacyInfoHovered(mouseX,mouseY,125,35,16,16)){String priority=menu.priority()==1?"low":menu.priority()==3?"high":"normal";var lines=new ArrayList<Component>();lines.add(Component.translatable("battery.priority."+priority));lines.add(Component.translatable("battery.priority.recommended"));for(String line:I18n.get("battery.priority."+priority+".desc").split("\\$"))lines.add(Component.literal(line));graphics.renderComponentTooltip(font,lines,mouseX,mouseY);}
    }
}
