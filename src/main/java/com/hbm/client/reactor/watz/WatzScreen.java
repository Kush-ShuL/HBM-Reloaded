package com.hbm.client.reactor.watz;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.reactor.watz.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Unaltered source176×229 split artwork, log heat tint/gauge,24slots and shared-chain fluid display. */
public final class WatzScreen extends LegacyContainerScreen<WatzMenu> {
    public WatzScreen(WatzMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_watz.png"),176,229);}
    private WatzEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof WatzEntity watz?watz:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,playerInventoryTitle,8,imageHeight-93,4210752,false);var watz=machine();if(watz==null)return;String flux=String.format(Locale.US,"%,.1f",watz.fluxDisplay);graphics.pose().pushPose();graphics.pose().scale(.8F,.8F,1);graphics.drawString(font,flux,(int)(161*1.25-font.width(flux)),(int)(107*1.25),0x00ff00,false);graphics.pose().popPose();}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){var watz=machine();if(watz==null)return;float color=Mth.clamp(1-(float)Math.log(watz.heat/100000D+1)*.4F,0,1);graphics.flush();graphics.setColor(1,color,color,1);drawLegacyRegion(graphics,0,0,0,0,131,122);graphics.flush();graphics.setColor(1,1,1,1);drawLegacyRegion(graphics,131,0,131,0,36,122);drawLegacyRegion(graphics,0,130,0,130,176,99);drawLegacyRegion(graphics,126,31,176,31,9,60);drawLegacyRegion(graphics,105,96,185,26,30,26);drawLegacyRegion(graphics,9,96,184,0,26,26);if(watz.isOn)drawLegacyRegion(graphics,147,8,176,0,8,8);if(watz.isLocked)drawLegacyRegion(graphics,142,70,210,0,18,18);gauge(graphics,leftPos+22,topPos+109,1D-color);for(int i=0;i<3;i++)tank(graphics,watz.tanks[i],143+i*6,69,4,43);}
    private static void gauge(GuiGraphics graphics,int x,int y,double progress){graphics.flush();RenderSystem.setShader(GameRenderer::getPositionColorShader);var buffer=Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLES,DefaultVertexFormat.POSITION_COLOR);float angle=(float)Math.toRadians(-Mth.clamp(progress,0,1)*270-45);double sin=Math.sin(angle),cos=Math.cos(angle);for(int layer=0;layer<2;layer++){float scale=layer==0?1.5F:1;for(double[] point:new double[][]{{0,5},{1,-2},{-1,-2}})buffer.addVertex(graphics.pose().last().pose(),(float)(x+(point[0]*cos+point[1]*sin)*scale),(float)(y+(point[1]*cos-point[0]*sin)*scale),0).setColor(layer==0?0xff000000:0xff7f0000);}BufferUploader.drawWithShader(buffer.buildOrThrow());}
    private void tank(GuiGraphics graphics,FluidTank tank,int x,int bottom,int width,int size){int height=tank.getFill()*size/tank.getMaxFill();if(height<=0)return;int color=tank.getTankType().getTint();graphics.flush();graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);for(int offset=0;offset<height;offset+=16){int strip=Math.min(16,height-offset);graphics.blit(tank.getTankType().getTexture(),leftPos+x,topPos+bottom-offset-strip,0,16-strip,width,strip,16,16);}graphics.flush();graphics.setColor(1,1,1,1);}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var watz=machine();if(watz==null)return;if(legacyInfoHovered(x,y,13,100,18,18))graphics.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d",watz.heat)+" TU"),x,y);if(legacyInfoHovered(x,y,143,71,16,16))graphics.renderTooltip(font,Component.translatable(watz.isLocked?"gui.hbm.watz.unlock":"gui.hbm.watz.lock"),x,y);for(int i=0;i<3;i++)if(legacyInfoHovered(x,y,142+i*6,23,6,45)){var tank=watz.tanks[i];var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+" / "+tank.getMaxFill()+" mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}}
    @Override public boolean mouseClicked(double x,double y,int button){boolean result=super.mouseClicked(x,y,button);if(leftPos+142<=x&&x<leftPos+160&&topPos+70<y&&y<=topPos+88){PacketDistributor.sendToServer(new WatzControl(menu.position));minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));}return result;}
}
