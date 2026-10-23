package com.hbm.client.pneumatic;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.pneumatic.*;
import com.hbm.pneumatic.tube.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
/** Original176x185 compressor/endpoint texture, controls, filter mode tooltip and two-tone smooth needle. */
public final class PneumoTubeScreen extends LegacyContainerScreen<PneumoTubeMenu> {
 public PneumoTubeScreen(PneumoTubeMenu menu,Inventory inventory,Component title){super(menu,inventory,title,PneumaticTubeContent.id("textures/gui/storage/"+(menu.endpointOnly?"gui_pneumatic_endpoint.png":"gui_pneumatic_pipe.png")),176,185);}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,88-font.width(title)/2,5,4210752,false);g.drawString(font,playerInventoryTitle,8,91,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var tube=menu.tube;drawLegacyRegion(g,139,tube.whitelist?33:47,176,0,3,6);if(menu.endpointOnly)return;if(tube.redstone)drawLegacyRegion(g,7,52,179,0,18,18);drawLegacyRegion(g,151,16,197,18*tube.receiveOrder,18,18);drawLegacyRegion(g,151,52,215,18*tube.sendOrder,18,18);drawLegacyRegion(g,6+4*(tube.compair.getPressure()-1),36,179,18,4,8);needle(g,leftPos+16,topPos+25,(double)tube.compair.getFill()/tube.compair.getMaxFill());}
 private static void needle(GuiGraphics g,int x,int y,double progress){int left=x-9,top=y-7;g.fill(left-1,top-2,left+19,top+16,0xFFC6C6C6);g.fill(left,top,left+18,top+14,0xFF373737);g.fill(left+1,top+1,left+17,top+13,0xFF8B8B8B);int width=(int)(Math.clamp(progress,0,1)*16);if(width>0)g.fill(left+1,top+1,left+1+width,top+13,0xFFCA6C43);}
 @Override public boolean mouseClicked(double x,double y,int button){boolean result=super.mouseClicked(x,y,button);int control=-1;if(!menu.endpointOnly){if(legacyInfoHovered(x,y,7,52,18,18))control=0;else if(legacyInfoHovered(x,y,6,36,20,8))control=1;else if(legacyInfoHovered(x,y,151,16,18,18))control=2;else if(legacyInfoHovered(x,y,151,52,18,18))control=3;}if(legacyInfoHovered(x,y,128,30,14,26))control=4;if(control>=0){minecraft.gameMode.handleInventoryButtonClick(menu.containerId,control);return true;}return result;}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var tube=menu.tube;if(!menu.endpointOnly){if(legacyInfoHovered(x,y,7,16,18,18)){var lines=new ArrayList<Component>();lines.add(tube.compair.getTankType().displayName());lines.add(Component.literal(tube.compair.getFill()+"/"+tube.compair.getMaxFill()+"mB"));FluidInfo.append(tube.compair.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}if(legacyInfoHovered(x,y,7,52,18,18))g.renderTooltip(font,Component.translatable("gui.hbm.pneumatic.redstone",Component.translatable(tube.redstone?"gui.hbm.pneumatic.on":"gui.hbm.pneumatic.off").withStyle(tube.redstone?ChatFormatting.GREEN:ChatFormatting.RED)),x,y);if(legacyInfoHovered(x,y,6,36,20,8))g.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.pneumatic.pressure",tube.compair.getPressure()),Component.translatable("gui.hbm.pneumatic.range",PneumaticNetwork.getRangeFromPressure(tube.compair.getPressure()))),x,y);if(legacyInfoHovered(x,y,151,16,18,18))g.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.pneumatic.receiver_order").withStyle(ChatFormatting.YELLOW),Component.translatable(tube.receiveOrder==0?"gui.hbm.pneumatic.round_robin":"gui.hbm.pneumatic.random")),x,y);if(legacyInfoHovered(x,y,151,52,18,18))g.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.pneumatic.sender_order").withStyle(ChatFormatting.YELLOW),Component.translatable(tube.sendOrder==0?"gui.hbm.pneumatic.first_last":tube.sendOrder==1?"gui.hbm.pneumatic.last_first":"gui.hbm.pneumatic.random")),x,y);}
  if(menu.getCarried().isEmpty())for(int i=0;i<15;i++){var slot=menu.slots.get(i);if(legacyInfoHovered(x,y,slot.x,slot.y,16,16)&&tube.pattern.modes[i]!=null)g.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.pneumatic.change_mode").withStyle(ChatFormatting.RED),PatternMatcher.label(tube.pattern.modes[i])),x,y-30);}
 }
}
