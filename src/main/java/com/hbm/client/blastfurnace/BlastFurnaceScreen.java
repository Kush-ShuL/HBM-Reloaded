package com.hbm.client.blastfurnace;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.blastfurnace.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/** Original176x222 artwork, exact fuel/progress fill UVs and two smooth gauge needles. */
public final class BlastFurnaceScreen extends LegacyContainerScreen<BlastFurnaceMenu> {
    public BlastFurnaceScreen(BlastFurnaceMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/processing/gui_blast_furnace.png"),176,222);}
    private BlastFurnaceBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof BlastFurnaceBlockEntity furnace?furnace:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,88-font.width(title)/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,128,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);int fuel=(int)Math.round((double)menu.fuel()*26/BlastFurnaceBlockEntity.MAX_FUEL),progress=(int)Math.round(menu.progress()*(88D-fuel));if(progress>0)drawLegacyRegion(graphics,62,106-progress-fuel,176,102-progress-fuel,56,progress);if(fuel>0)drawLegacyRegion(graphics,62,106-fuel,176,128-fuel,56,fuel);if(menu.processing())drawLegacyRegion(graphics,81,64,176,0,14,14);var furnace=machine();gauge(graphics,leftPos+34,topPos+80,furnace==null?0:(double)furnace.tanks[0].getFill()/furnace.tanks[0].getMaxFill());gauge(graphics,leftPos+34,topPos+26,furnace==null?0:(double)furnace.tanks[1].getFill()/furnace.tanks[1].getMaxFill());}
    private static void gauge(GuiGraphics graphics,int x,int y,double progress){graphics.flush();RenderSystem.setShader(GameRenderer::getPositionColorShader);var buffer=Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES,DefaultVertexFormat.POSITION_COLOR);float angle=(float)Math.toRadians(-Mth.clamp(progress,0,1)*270-45);double sin=Math.sin(angle),cos=Math.cos(angle);for(int layer=0;layer<2;layer++){float scale=layer==0?1.5F:1;int color=layer==0?0xff000000:0xff800000;for(double[] point:new double[][]{{0,5},{1,-2},{-1,-2}})buffer.addVertex(graphics.pose().last().pose(),(float)(x+(point[0]*cos+point[1]*sin)*scale),(float)(y+(point[1]*cos-point[0]*sin)*scale),0).setColor(color);}BufferUploader.drawWithShader(buffer.buildOrThrow());}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var furnace=machine();if(furnace==null)return;if(menu.getCarried().isEmpty()&&!menu.getSlot(0).hasItem()&&legacyInfoHovered(x,y,80,81,16,16))graphics.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.blast.heat_bonuses").withStyle(ChatFormatting.RED),Component.translatable("gui.hbm.blast.wood_heat",Component.literal("-100%").withStyle(ChatFormatting.RED)).withStyle(ChatFormatting.YELLOW)),x,y);if(legacyInfoHovered(x,y,79,62,18,18))graphics.renderTooltip(font,Component.translatable("gui.hbm.blast.speed",(int)(menu.speed()*100)),x,y);if(legacyInfoHovered(x,y,25,71,18,18))tankInfo(graphics,furnace.tanks[0],x,y);if(legacyInfoHovered(x,y,25,17,18,18))tankInfo(graphics,furnace.tanks[1],x,y);}
    private void tankInfo(GuiGraphics graphics,FluidTank tank,int x,int y){var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
}
