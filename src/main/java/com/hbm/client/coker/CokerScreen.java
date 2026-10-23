package com.hbm.client.coker;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.coker.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x204 GUI/texture regions; source retained TU progress is displayed only while an actual task exists. */
public final class CokerScreen extends LegacyContainerScreen<CokerMenu> {
 public CokerScreen(CokerMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_coker.png"),176,204);}
 private CokerEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof CokerEntity be?be:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,88-font.width(title)/2,6,0xC7C1A3,false);g.drawString(font,playerInventoryTitle,8,110,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var m=machine();if(m==null)return;if(m.hasTask)drawLegacyRegion(g,61,46,176,0,m.progress*53/CokerEntity.PROCESS_TIME,5);drawLegacyRegion(g,61,55,176,5,m.heat*52/CokerEntity.MAX_HEAT,5);tank(g,m.tanks[0],35);tank(g,m.tanks[1],125);}
 private void tank(GuiGraphics g,FluidTank t,int x){int height=t.getMaxFill()>0?t.getFill()*52/t.getMaxFill():0;if(height<=0)return;int color=t.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int done=0;done<height;done+=16){int part=Math.min(16,height-done);g.blit(t.getTankType().getTexture(),leftPos+x,topPos+70-done-part,0,16-part,16,part,16,16);}g.setColor(1,1,1,1);}
 private void tankInfo(GuiGraphics g,FluidTank t,int x,int y){var lines=new ArrayList<Component>();lines.add(t.getTankType().displayName());lines.add(Component.literal(t.getFill()+"/"+t.getMaxFill()+"mB"));if(t.getPressure()!=0)lines.add(Component.translatable("gui.hbm.chemical.pressure",t.getPressure()).withStyle(ChatFormatting.RED));FluidInfo.append(t.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var m=machine();if(m==null)return;if(legacyInfoHovered(x,y,35,18,16,52))tankInfo(g,m.tanks[0],x,y);if(legacyInfoHovered(x,y,125,18,16,52))tankInfo(g,m.tanks[1],x,y);if(m.hasTask&&legacyInfoHovered(x,y,60,45,54,7))g.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d / %,dTU",m.progress,CokerEntity.PROCESS_TIME)),x,y);if(legacyInfoHovered(x,y,60,54,54,7))g.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d / %,dTU",m.heat,CokerEntity.MAX_HEAT)),x,y);}
}
