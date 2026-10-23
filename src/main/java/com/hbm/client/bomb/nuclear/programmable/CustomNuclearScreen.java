package com.hbm.client.bomb.nuclear.programmable;
import com.hbm.bomb.nuclear.programmable.*;
import com.hbm.client.gui.LegacyContainerScreen;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Original 176x222 gunBombSchematic, exact mutually-exclusive stage sprites and source tooltip content. */
public final class CustomNuclearScreen extends LegacyContainerScreen<CustomNuclearMenu> {
 public CustomNuclearScreen(CustomNuclearMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/weapon/gunbombschematic.png"),176,222);}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,88-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,128,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var p=menu.payload();if(p.euph()>0)drawLegacyRegion(g,142,89,176,108,18,18);else if(p.schrab()>0)drawLegacyRegion(g,106,89,176,90,18,18);else if(p.amat()>0)drawLegacyRegion(g,70,89,176,54,18,18);else if(p.hydro()>0)drawLegacyRegion(g,52,89,176,36,18,18);else if(p.nuke()>0)drawLegacyRegion(g,34,89,176,18,18,18);else if(p.tnt()>0)drawLegacyRegion(g,16,89,176,0,18,18);if(p.dirty()>0&&p.nuke()>0&&p.amat()==0&&p.schrab()==0&&p.euph()==0)drawLegacyRegion(g,88,89,176,72,18,18);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);int[] positions={16,34,52,70,88,106,142};for(int i=0;i<positions.length;i++)if(legacyInfoHovered(x,y,positions[i],89,18,18)){g.renderComponentTooltip(font,tooltip(i,menu.payload()),x,y);break;}}
 public static List<Component> tooltip(int stage,CustomBombPayload p){String key="gui.hbm.custom_nuke."+new String[]{"tnt","nuke","hydro","amat","dirty","schrab","euph"}[stage];float[] values={p.tnt(),p.nuke(),p.hydro(),p.amat(),p.dirty(),p.schrab()},adjusted={Math.min(p.tnt(),150),p.nukeAdjusted(),p.hydroAdjusted(),p.amatAdjusted(),Math.min(p.dirty(),100),p.schrabAdjusted()};List<Component> lines=new ArrayList<>();lines.add((stage==6?Component.translatable(key):Component.translatable(key,values[stage],adjusted[stage])).withStyle(ChatFormatting.YELLOW));int[] counts={2,3,3,2,3,2,0},quotes={1,1,2,1,1,2,1};for(int i=0;i<counts[stage];i++)lines.add(Component.translatable(key+".line"+(i+1)));for(int i=0;i<quotes[stage];i++)lines.add(Component.translatable(key+".quote"+(i+1)).withStyle(ChatFormatting.ITALIC));return lines;}
}
