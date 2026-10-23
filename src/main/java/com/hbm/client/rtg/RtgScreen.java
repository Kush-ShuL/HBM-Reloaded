package com.hbm.client.rtg;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.battery.BatteryItem;
import com.hbm.machine.rtg.*;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Original 176x188 GUI, unscaled original sprite regions and exact hover rectangles. */
public final class RtgScreen extends LegacyContainerScreen<RtgMenu> {
    public RtgScreen(RtgMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/gui_rtg.png"),176,188);}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,60-font.width(title)/2,7,10925486,false);graphics.drawString(font,playerInventoryTitle,8,94,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){
        super.renderBg(graphics,partial,x,y);
        if(menu.hasHeat()){int height=menu.heat()*51/menu.maximumHeat();drawLegacyRegion(graphics,124,61-height,176,61-height,16,height);}
        if(menu.power()>0){int height=(int)(menu.power()*51/RtgBlockEntity.MAX_POWER);drawLegacyRegion(graphics,146,61-height,192,61-height,16,height);}
        graphics.blit(ResourceLocation.parse("hbm:textures/gui/gui_utility.png"),leftPos-12,topPos+25,8,0,16,16,256,256);
    }
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){
        super.render(graphics,x,y,partial);
        if(legacyInfoHovered(x,y,146,9,16,51))graphics.renderTooltip(font,Component.literal(BatteryItem.shortNumber(menu.power())+"/"+BatteryItem.shortNumber(RtgBlockEntity.MAX_POWER)+"HE"),x,y);
        if(legacyInfoHovered(x,y,124,9,16,51))graphics.renderComponentTooltip(font,split(Component.translatable("desc.gui.rtg.heat",menu.heat())),x,y);
        if(legacyInfoHovered(x,y,-12,25,16,16)){var lines=new ArrayList<Component>();lines.add(Component.translatable("desc.gui.rtg.pellets"));for(var entry:RtgContent.PELLETS){var pellet=entry.get();lines.add(Component.translatable("desc.gui.rtg.pelletPower",pellet.getName(new net.minecraft.world.item.ItemStack(pellet)),pellet.heat()*5));}graphics.renderComponentTooltip(font,lines,leftPos-8,topPos+52);}
    }
    private static List<Component> split(Component text){return java.util.Arrays.stream(text.getString().split("\\$",-1)).map(Component::literal).map(Component.class::cast).toList();}
}
