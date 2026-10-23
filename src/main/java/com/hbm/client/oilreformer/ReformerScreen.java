package com.hbm.client.oilreformer;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.oilreformer.*;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
/** Original176x238 with4tanks/54pixelenergy and source empty-catalyst-slot stack/name hover; no invented progressbar. */
public final class ReformerScreen extends LegacyContainerScreen<ReformerMenu> {
 public ReformerScreen(ReformerMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_catalytic_reformer.png"),176,238);}
 private ReformerEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof ReformerEntity m?m:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,88-font.width(title)/2,5,0xffffff,false);g.drawString(font,playerInventoryTitle,8,144,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);int power=(int)(menu.power()*54/1000000);drawLegacyRegion(g,17,70-power,176,52-power,16,power);var m=machine();if(m==null)return;int[] xs={35,107,125,143};for(int i=0;i<4;i++)tank(g,m.tanks[i],xs[i]);}
 private void tank(GuiGraphics g,FluidTank t,int x){int height=t.getMaxFill()>0?t.getFill()*52/t.getMaxFill():0;if(height<=0)return;int color=t.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int done=0;done<height;done+=16){int part=Math.min(16,height-done);g.blit(t.getTankType().getTexture(),leftPos+x,topPos+70-done-part,0,16-part,16,part,16,16);}g.setColor(1,1,1,1);}
 private void tankInfo(GuiGraphics g,FluidTank t,int x,int y){var lines=new ArrayList<Component>();lines.add(t.getTankType().displayName());lines.add(Component.literal(t.getFill()+"/"+t.getMaxFill()+"mB"));if(t.getPressure()!=0)lines.add(Component.translatable("gui.hbm.chemical.pressure",t.getPressure()).withStyle(ChatFormatting.RED));FluidInfo.append(t.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var m=machine();if(m==null)return;int[] xs={35,107,125,143};for(int i=0;i<4;i++)if(legacyInfoHovered(x,y,xs[i],18,16,52))tankInfo(g,m.tanks[i],x,y);if(legacyInfoHovered(x,y,17,18,16,52))g.renderTooltip(font,Component.literal(menu.power()+"/1000000HE"),x,y);if(menu.getCarried().isEmpty()&&!menu.getSlot(10).hasItem()&&legacyInfoHovered(x,y,71,36,16,16)){var converter=new ItemStack(HbmContent.item("catalytic_converter"));com.hbm.client.wiaj.actors.JarStackText.draw(g,java.util.List.<Object[]>of(new Object[]{converter},new Object[]{converter.getHoverName().getString()}),x,y,width,height);}}
}
