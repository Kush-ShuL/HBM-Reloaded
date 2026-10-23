package com.hbm.client.refinery;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.refinery.*;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original182x240 GUI, exact slots, five gauges and original colored output-pipe UV rectangles. */
public final class RefineryScreen extends LegacyContainerScreen<RefineryMenu> {
    public RefineryScreen(RefineryMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/processing/gui_refinery.png"),182,240);}
    private RefineryBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof RefineryBlockEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,73-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,11,146,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);int power=(int)(menu.power()*88/1000);if(power>0)drawLegacyRegion(graphics,158,106-power,182,88-power,16,power);var machine=machine();if(machine==null)return;tank(graphics,machine.tanks[0],12,88,70);var recipe=RefineryRecipeConfig.find(minecraft.level.getRecipeManager(),machine.tanks[0].getTankType());int[][] pipes={{30,30,0,248,43,4},{30,26,0,240,61,8},{30,22,61,240,79,12},{30,18,140,240,97,16}};for(int i=0;i<4;i++){if(recipe!=null){int color=recipe.data().outputFluids().get(i).type.getColor();graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);}int[] p=pipes[i];drawLegacyRegion(graphics,p[0],p[1],p[2],p[3],p[4],p[5]);}graphics.setColor(1,1,1,1);for(int i=1;i<5;i++)tank(graphics,machine.tanks[i],64+(i-1)*18,88,52);}
    private void tank(GuiGraphics graphics,FluidTank tank,int x,int bottom,int capacityHeight){if(tank.getMaxFill()==0)return;int height=tank.getFill()*capacityHeight/tank.getMaxFill();if(height<=0)return;int color=tank.getTankType().getTint();graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);graphics.blit(tank.getTankType().getTexture(),leftPos+x,topPos+bottom-height,0,16-height,16,height,16,16);graphics.setColor(1,1,1,1);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var machine=machine();if(machine!=null){if(legacyInfoHovered(x,y,12,17,16,70))tankInfo(graphics,machine.tanks[0],x,y);for(int i=1;i<5;i++)if(legacyInfoHovered(x,y,64+(i-1)*18,35,16,52))tankInfo(graphics,machine.tanks[i],x,y);}if(legacyInfoHovered(x,y,158,18,16,88))graphics.renderTooltip(font,Component.literal(menu.power()+"/1000HE"),x,y);}
    private void tankInfo(GuiGraphics graphics,FluidTank tank,int x,int y){var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
}
