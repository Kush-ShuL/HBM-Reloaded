package com.hbm.client.steelfurnace;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.steelfurnace.*;
import java.util.Locale;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x166/256atlas; source shared wasOn icon intentionally applies to all three rows. */
public final class SteelFurnaceScreen extends LegacyContainerScreen<SteelFurnaceMenu> {
    public SteelFurnaceScreen(SteelFurnaceMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_furnace_steel.png"),176,166);}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,0x404040,false);g.drawString(font,playerInventoryTitle,8,72,0x404040,false);}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);int heat=menu.heat()*48/SteelFurnaceEntity.MAX_HEAT;drawLegacyRegion(g,152,67-heat,176,76-heat,7,heat);for(int i=0;i<3;i++){drawLegacyRegion(g,54,18+18*i,176,18,menu.progress(i)*69/SteelFurnaceEntity.PROCESS_TIME,5);drawLegacyRegion(g,54,27+18*i,176,23,menu.bonus(i)*69/100,5);if(menu.wasOn())drawLegacyRegion(g,16,16+18*i,176,0,18,18);}}
    @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);for(int i=0;i<3;i++){if(legacyInfoHovered(x,y,53,17+18*i,70,7))g.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d / %,dTU",menu.progress(i),SteelFurnaceEntity.PROCESS_TIME)),x,y);if(legacyInfoHovered(x,y,53,26+18*i,70,7))g.renderTooltip(font,Component.translatable("gui.hbm.steel_furnace.bonus",menu.bonus(i)),x,y);}if(legacyInfoHovered(x,y,151,18,9,50))g.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d / %,dTU",menu.heat(),SteelFurnaceEntity.MAX_HEAT)),x,y);}
}
