package com.hbm.world.structure.templates.microwave.client;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.battery.BatteryItem;
import com.hbm.world.structure.templates.microwave.*;
import java.math.BigInteger;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Original176x168 atlas, exact three meters and two unvoiced18px speed hitboxes. */
public final class MicrowaveScreen extends LegacyContainerScreen<MicrowaveMenu> {
    public MicrowaveScreen(MicrowaveMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_microwave.png"),176,168);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,88-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,74,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);int power=menu.power().multiply(BigInteger.valueOf(34)).divide(BigInteger.valueOf(MicrowaveBlockEntity.MAX_POWER)).intValue();drawLegacyRegion(graphics,8,51-power,176,34-power,16,power);int time=Math.min(menu.time()*23/MicrowaveBlockEntity.MAX_TIME,22);drawLegacyRegion(graphics,104,34,192,0,time,16);int speed=menu.speed()*34/MicrowaveBlockEntity.MAX_SPEED;drawLegacyRegion(graphics,62,60-speed,214,34-speed,4,speed);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);if(legacyInfoHovered(x,y,8,17,16,34))graphics.renderTooltip(font,Component.literal((menu.power().bitLength()<64?BatteryItem.shortNumber(menu.power().longValue()):menu.power().toString())+"/"+BatteryItem.shortNumber(MicrowaveBlockEntity.MAX_POWER)+"HE"),x,y);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,43,25,18,18)){PacketDistributor.sendToServer(new MicrowaveControl(menu.position,0));return true;}if(legacyInfoHovered(x,y,43,43,18,18)){PacketDistributor.sendToServer(new MicrowaveControl(menu.position,1));return true;}return handled;}
}
