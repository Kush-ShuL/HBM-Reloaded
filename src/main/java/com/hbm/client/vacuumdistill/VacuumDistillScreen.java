package com.hbm.client.vacuumdistill;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.vacuumdistill.*;
import java.util.ArrayList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Original176x238 pixels, hidden deprecated slots, source54-pixel HE gauge and pressure2 input warning. */
public final class VacuumDistillScreen extends LegacyContainerScreen<VacuumDistillMenu> {
    public VacuumDistillScreen(VacuumDistillMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/processing/gui_vacuum_distill.png"),176,238);}
    private VacuumDistillBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof VacuumDistillBlockEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,88-font.width(title)/2,5,0xffffff,false);graphics.drawString(font,playerInventoryTitle,8,144,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);int power=(int)(menu.power()*54/1000000);if(power>0)drawLegacyRegion(graphics,26,70-power,176,52-power,16,power);var machine=machine();if(machine==null)return;tank(graphics,machine.tanks[0],44);for(int i=1;i<5;i++)tank(graphics,machine.tanks[i],80+(i-1)*18);}
    private void tank(GuiGraphics graphics,FluidTank tank,int x){if(tank.getMaxFill()==0)return;int height=tank.getFill()*52/tank.getMaxFill();if(height<=0)return;int color=tank.getTankType().getTint();graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);graphics.blit(tank.getTankType().getTexture(),leftPos+x,topPos+70-height,0,16-height,16,height,16,16);graphics.setColor(1,1,1,1);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var machine=machine();if(machine!=null){if(legacyInfoHovered(x,y,44,18,16,52))tankInfo(graphics,machine.tanks[0],x,y);for(int i=1;i<5;i++)if(legacyInfoHovered(x,y,80+(i-1)*18,18,16,52))tankInfo(graphics,machine.tanks[i],x,y);}if(legacyInfoHovered(x,y,26,18,16,52))graphics.renderTooltip(font,Component.literal(menu.power()+"/1000000HE"),x,y);}
    private void tankInfo(GuiGraphics graphics,FluidTank tank,int x,int y){var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0){lines.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));lines.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_RED));}FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
}
