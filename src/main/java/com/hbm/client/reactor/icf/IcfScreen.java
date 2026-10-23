package com.hbm.client.reactor.icf;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.battery.BatteryItem;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.reactor.icf.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Exact248x222 source cropped/two-part background,70pixel tanks and original smooth triangular heat gauge. */
public final class IcfScreen extends LegacyContainerScreen<IcfReactorMenu>{
 public IcfScreen(IcfReactorMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_icf.png"),248,222);}
 private IcfReactorEntity reactor(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof IcfReactorEntity reactor?reactor:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,124-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,44,129,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int mouseX,int mouseY){drawLegacyRegion(g,0,0,0,0,248,114);drawLegacyRegion(g,36,122,36,122,176,108);var reactor=reactor();if(reactor==null)return;if(reactor.maxLaser>0){int size=(int)(reactor.laser*70/reactor.maxLaser);drawLegacyRegion(g,8,88-size,212,192-size,16,size);}gauge(g,196,98,(double)reactor.heat/IcfReactorEntity.MAX_HEAT);tank(g,reactor.tanks[0],44);tank(g,reactor.tanks[1],188);tank(g,reactor.tanks[2],224);}
 private void tank(GuiGraphics g,FluidTank tank,int x){int size=tank.getFill()*70/tank.getMaxFill();if(size<=0)return;int color=tank.getTankType().getTint();g.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);g.blit(tank.getTankType().getTexture(),leftPos+x,topPos+88-size,0,16-size,16,size,16,16);g.setColor(1,1,1,1);}
 private void gauge(GuiGraphics g,int x,int y,double value){value=Math.max(0,Math.min(1,value));float angle=(float)Math.toRadians(-value*270-45);double cos=Math.cos(angle),sin=Math.sin(angle);double[][] vertices={{0,5},{1,-2},{-1,-2}};g.flush();RenderSystem.setShader(GameRenderer::getPositionColorShader);var out=Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES,DefaultVertexFormat.POSITION_COLOR);for(int layer=0;layer<2;layer++){double scale=layer==0?1.5:1;int color=layer==0?0xff000000:0xffff00af;for(var v:vertices){double vx=v[0]*cos+v[1]*sin,vy=v[1]*cos-v[0]*sin;out.addVertex(g.pose().last().pose(),(float)(leftPos+x+vx*scale),(float)(topPos+y+vy*scale),0).setColor(color);}}BufferUploader.drawWithShader(out.buildOrThrow());}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var reactor=reactor();if(reactor==null)return;int[] positions={44,188,224};for(int i=0;i<3;i++)if(legacyInfoHovered(x,y,positions[i],18,16,70)){var tank=reactor.tanks[i];var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());g.renderComponentTooltip(font,lines,x,y);}if(legacyInfoHovered(x,y,8,18,16,70))g.renderTooltip(font,reactor.maxLaser<=0?Component.translatable("gui.hbm.icf.offline"):Component.literal(BatteryItem.shortNumber(reactor.laser)+"TU/t - "+(reactor.laser*1000/reactor.maxLaser)/10D+"%"),x,y);if(legacyInfoHovered(x,y,187,89,18,18))g.renderTooltip(font,Component.literal(BatteryItem.shortNumber(reactor.heat)+" / "+BatteryItem.shortNumber(IcfReactorEntity.MAX_HEAT)+"TU"),x,y);}
}
