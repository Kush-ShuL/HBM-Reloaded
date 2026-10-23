package com.hbm.satellite.control.client;
import com.hbm.client.gui.LegacyContainerScreen;import com.hbm.satellite.control.SatelliteLinkerMenu;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.client.resources.language.I18n;import net.minecraft.network.chat.Component;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.entity.player.Inventory;import java.util.*;
/** Source176x186 programmer with original three slots, info icons and text/hover rectangles. */
public final class SatelliteLinkerScreen extends LegacyContainerScreen<SatelliteLinkerMenu>{
 private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/machine/gui_sat_linker.png"),UTILITY=ResourceLocation.parse("hbm:textures/gui/gui_utility.png");
 public SatelliteLinkerScreen(SatelliteLinkerMenu menu,Inventory inventory,Component title){super(menu,inventory,title,TEXTURE,176,186);}
 @Override protected void renderBg(GuiGraphics graphics,float partial,int mx,int my){super.renderBg(graphics,partial,mx,my);graphics.blit(UTILITY,leftPos+12,topPos+28,8,0,16,16);graphics.blit(UTILITY,leftPos+12,topPos+44,24,0,16,16);}
 @Override protected void renderLabels(GuiGraphics graphics,int mx,int my){var name=menu.machine.getDisplayName();graphics.drawString(font,name,88-font.width(name)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,92,4210752,false);}
 @Override public void render(GuiGraphics graphics,int mx,int my,float partial){super.render(graphics,mx,my,partial);renderTooltip(graphics,mx,my);if(legacyInfoHovered(mx,my,12,28,16,16))tooltip(graphics,"desc.gui.satlinker.chip",mx,my);if(legacyInfoHovered(mx,my,12,44,16,16))tooltip(graphics,"desc.gui.satlinker.random",mx,my);}
 private void tooltip(GuiGraphics graphics,String key,int x,int y){var lines=Arrays.stream(I18n.get(key).split("\\$",-1)).map(line->(Component)Component.literal(line)).toList();graphics.renderComponentTooltip(font,lines,x,y);}
}
