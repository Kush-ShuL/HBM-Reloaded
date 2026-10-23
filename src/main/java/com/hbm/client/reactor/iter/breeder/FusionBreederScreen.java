package com.hbm.client.reactor.iter.breeder;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.reactor.iter.breeder.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
/** Exact original176×200GUI/background/3slots/progress/fluxgauge/two52pxfluids. */
public final class FusionBreederScreen extends LegacyContainerScreen<FusionBreederMenu> {
 public FusionBreederScreen(FusionBreederMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_fusion_breeder.png"),176,200);}
 private FusionBreederEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof FusionBreederEntity breeder?breeder:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,35,107,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var breeder=machine();if(breeder==null)return;int p=(int)Math.ceil(breeder.progress*42/FusionBreederEntity.CAPACITY);if(p>0)drawLegacyRegion(g,67,48,176,0,p,10);double gauge=1D-Math.pow(Math.E,-breeder.neutronEnergy*10/FusionBreederEntity.CAPACITY);gauge(g,leftPos+88,topPos+32,gauge);tank(g,breeder.tanks[0],26);tank(g,breeder.tanks[1],134);}
 private void tank(GuiGraphics g,FluidTank tank,int x){if(tank.getMaxFill()==0)return;int fill=tank.getFill()*52/tank.getMaxFill();if(fill<=0)return;int color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);g.blit(tank.getTankType().getTexture(),leftPos+x,topPos+70-fill,0,16-fill,16,fill,16,16);g.setColor(1,1,1,1);}
 private static void gauge(GuiGraphics graphics,int x,int y,double progress){graphics.flush();RenderSystem.setShader(GameRenderer::getPositionColorShader);var buffer=Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES,DefaultVertexFormat.POSITION_COLOR);float angle=(float)Math.toRadians(-Mth.clamp(progress,0,1)*270-45);double sin=Math.sin(angle),cos=Math.cos(angle);for(int layer=0;layer<2;layer++){float scale=layer==0?1.5F:1;for(double[] p:new double[][]{{0,5},{1,-2},{-1,-2}})buffer.addVertex(graphics.pose().last().pose(),(float)(x+(p[0]*cos+p[1]*sin)*scale),(float)(y+(p[1]*cos-p[0]*sin)*scale),0).setColor(layer==0?0xff000000:0xffa00000);}BufferUploader.drawWithShader(buffer.buildOrThrow());}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var breeder=machine();if(breeder==null)return;if(legacyInfoHovered(x,y,79,23,18,18))g.renderTooltip(font,Component.translatable("gui.hbm.fusion_breeder.flux_rate",(int)Math.ceil(breeder.neutronEnergy)),x,y);if(legacyInfoHovered(x,y,67,46,42,14))g.renderTooltip(font,Component.translatable("gui.hbm.fusion_breeder.flux",(int)Math.ceil(breeder.progress),(int)Math.ceil(FusionBreederEntity.CAPACITY)),x,y);tankInfo(g,breeder.tanks[0],x,y,26);tankInfo(g,breeder.tanks[1],x,y,134);}
 private void tankInfo(GuiGraphics g,FluidTank tank,int mouseX,int mouseY,int x){if(!legacyInfoHovered(mouseX,mouseY,x,18,16,52))return;var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0)lines.add(Component.literal(tank.getPressure()+" PU"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,mouseX,mouseY);}
}
