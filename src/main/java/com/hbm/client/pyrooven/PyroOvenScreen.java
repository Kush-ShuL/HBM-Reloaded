package com.hbm.client.pyrooven;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.*;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.pyrooven.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x204 pixels, source electricity/progress regions and upgrade help placement. */
public final class PyroOvenScreen extends LegacyContainerScreen<PyroOvenMenu> {
 public PyroOvenScreen(PyroOvenMenu menu,Inventory inv,Component title){super(menu,inv,title,ResourceLocation.parse("hbm:textures/gui/processing/gui_pyrooven.png"),176,204);}
 private PyroOvenEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof PyroOvenEntity oven?oven:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,70-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,110,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var m=machine();if(m!=null){int power=(int)(m.getPower()*52/10000000);if(power>0)drawLegacyRegion(g,152,70-power,176,64-power,16,power);if(m.isProgressing&&m.progress>0)drawLegacyRegion(g,57,47,176,0,(int)(m.progress*27),12);tank(g,m.tanks[0],8);tank(g,m.tanks[1],116);}g.blit(ResourceLocation.parse("hbm:textures/gui/gui_utility.png"),leftPos+108,topPos+76,0,32,8,8,256,256);}
 private void tank(GuiGraphics g,FluidTank tank,int x){if(tank.getMaxFill()<=0||tank.getTankType()==HbmFluids.NONE)return;int height=tank.getFill()*52/tank.getMaxFill(),color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int y=0;y<height;y+=16){int h=Math.min(16,height-y);g.blit(tank.getTankType().getTexture(),leftPos+x,topPos+70-y-h,0,16-h,16,h,16,16);}g.setColor(1,1,1,1);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var m=machine();if(m==null)return;if(legacyInfoHovered(x,y,8,18,16,52))tankInfo(g,m.tanks[0],x,y);if(legacyInfoHovered(x,y,116,18,16,52))tankInfo(g,m.tanks[1],x,y);if(legacyInfoHovered(x,y,152,18,16,52))g.renderTooltip(font,Component.literal(m.power+"/10000000HE"),x,y);if(legacyInfoHovered(x,y,108,76,8,8))g.renderComponentTooltip(font,List.of(Component.translatable("upgrade.gui.title"),Component.translatable("upgrade.gui.speed",3),Component.translatable("upgrade.gui.power",3)),x,y);}
 private void tankInfo(GuiGraphics g,FluidTank tank,int x,int y){var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0){lines.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));lines.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(ChatFormatting.RED));}FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}
}
