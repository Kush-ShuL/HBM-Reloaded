package com.hbm.client.ironfurnace;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.ironfurnace.*;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** The original 176x166 GUI, original fuel multipliers and process/fuel hover regions. */
public final class IronFurnaceScreen extends LegacyContainerScreen<IronFurnaceMenu> {
    public IronFurnaceScreen(IronFurnaceMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_furnace_iron.png"),176,166);}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,0x404040,false);g.drawString(font,playerInventoryTitle,8,72,0x404040,false);}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);drawLegacyRegion(g,53,36,176,18,menu.progress()*70/Math.max(menu.processingTime(),1),5);drawLegacyRegion(g,53,45,176,23,menu.burnTime()*70/Math.max(menu.maxBurnTime(),1),5);if(menu.canSmelt())drawLegacyRegion(g,70,16,176,0,18,18);}
    @Override public void render(GuiGraphics g,int x,int y,float partial){
        super.render(g,x,y,partial);
        if(menu.getCarried().isEmpty())for(int i=1;i<3;i++){var slot=menu.slots.get(i);if(!slot.hasItem()&&legacyInfoHovered(x,y,slot.x,slot.y,16,16))g.renderComponentTooltip(font,IronFurnaceEntity.BURN.getTimeDesc().stream().map(Component::literal).map(value->(Component)value).toList(),x,y);}
        if(legacyInfoHovered(x,y,52,35,71,7))g.renderTooltip(font,Component.literal(menu.progress()*100/Math.max(menu.processingTime(),1)+"%"),x,y);
        if(legacyInfoHovered(x,y,52,44,71,7))g.renderTooltip(font,Component.literal(menu.burnTime()/20+"s"),x,y);
    }
}
