package com.hbm.client.soyuzlauncher;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.soyuzlauncher.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

/** Original194x244 GUI, every source slot/indicator/button/text coordinate; private tanks remain vertical source bars. */
public final class SoyuzLauncherScreen extends LegacyContainerScreen<SoyuzLauncherMenu> {
    public SoyuzLauncherScreen(SoyuzLauncherMenu menu,Inventory inventory,Component title){super(menu,inventory,title,SoyuzLauncherContent.id("textures/gui/machine/gui_soyuz.png"),194,244);}
    private SoyuzLauncherBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof SoyuzLauncherBlockEntity m?m:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){
        graphics.drawString(font,title,imageWidth/2-font.width(title)/2,4,0xFFFFFF,false);graphics.drawString(font,playerInventoryTitle,17,imageHeight-96+2,4210752,false);
        var m=machine();if(m==null)return;String secs=Integer.toString(m.countdown/20),cents=Integer.toString((m.countdown%20)*5);if(secs.length()==1)secs="0"+secs;if(cents.length()==1)cents+="0";graphics.drawString(font,secs+":"+cents,85,121,0xFF0000,false);
    }
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){
        super.renderBg(graphics,partial,x,y);var m=machine();if(m==null)return;
        int power=(int)(m.power*52/SoyuzLauncherBlockEntity.MAX_POWER);if(power>0)drawLegacyRegion(graphics,134,96-power,194,52-power,16,power);
        drawLegacyRegion(graphics,97,79,210+(m.hasRocket()?18:0),8,18,18);int designator=m.designator();if(designator>0)drawLegacyRegion(graphics,79,79,210+(designator-1)*18,8,18,18);
        drawLegacyRegion(graphics,97-m.mode*18,52,228-m.mode*18,26,18,18);int orbital=m.orbital();if(orbital>0)drawLegacyRegion(graphics,79,25,210+(orbital-1)*18,8,18,18);int satellite=m.satellite();if(satellite>0)drawLegacyRegion(graphics,97,25,210+(satellite-1)*18,8,18,18);
        if(m.starting)drawLegacyRegion(graphics,88,97,210,44,18,18);
        drawLegacyRegion(graphics,157,31,m.hasFuel()?210:216,0,6,8);drawLegacyRegion(graphics,175,31,m.hasOxy()?210:216,0,6,8);drawLegacyRegion(graphics,139,31,m.hasPower()?210:216,0,6,8);
        tank(graphics,m.tanks[0],152);tank(graphics,m.tanks[1],170);
        graphics.blit(SoyuzLauncherContent.id("textures/gui/gui_utility.png"),leftPos-16,topPos+53,8,0,16,16,256,256);
    }
    private void tank(GuiGraphics graphics,FluidTank tank,int offset){
        int height=Mth.clamp(tank.getFill()*52/Math.max(tank.getMaxFill(),1),0,52);if(height==0)return;graphics.flush();var matrix=graphics.pose().last().pose();int color=tank.getTankType().getTint(),x=leftPos+offset,y=topPos+96;
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);RenderSystem.setShaderTexture(0,tank.getTankType().getTexture());RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();
        var out=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);
        out.addVertex(matrix,x,y,0).setUv(0,1).setColor(color>>16&255,color>>8&255,color&255,255);out.addVertex(matrix,x+16,y,0).setUv(1,1).setColor(color>>16&255,color>>8&255,color&255,255);out.addVertex(matrix,x+16,y-height,0).setUv(1,1-height/16F).setColor(color>>16&255,color>>8&255,color&255,255);out.addVertex(matrix,x,y-height,0).setUv(0,1-height/16F).setColor(color>>16&255,color>>8&255,color&255,255);BufferUploader.drawWithShader(out.buildOrThrow());RenderSystem.disableBlend();
    }
    private List<Component> description(String key){return Arrays.stream(Component.translatable(key).getString().split("\\$")).map(Component::literal).map(c->(Component)c).toList();}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){
        super.render(graphics,x,y,partial);var m=machine();if(m==null)return;
        for(int i=0;i<2;i++)if(legacyInfoHovered(x,y,152+i*18,44,16,52)){var tank=m.tanks[i];var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+" / "+tank.getMaxFill()+" mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
        if(legacyInfoHovered(x,y,134,44,16,52))graphics.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d / %,d HE",m.power,SoyuzLauncherBlockEntity.MAX_POWER)),x,y);
        if(legacyInfoHovered(x,y,-16,53,16,16))graphics.renderComponentTooltip(font,description("desc.gui.soyuz.desc"),leftPos-8,topPos+69);
        if(legacyInfoHovered(x,y,79,52,18,18))graphics.renderComponentTooltip(font,description("desc.gui.soyuz.cargo"),x,y);
        if(legacyInfoHovered(x,y,97,52,18,18))graphics.renderComponentTooltip(font,description("desc.gui.soyuz.satellite"),x,y);
    }
    @Override public boolean mouseClicked(double x,double y,int button){
        boolean handled=super.mouseClicked(x,y,button);int action=legacyInfoHovered(x,y,97,52,18,18)?0:legacyInfoHovered(x,y,79,52,18,18)?1:legacyInfoHovered(x,y,88,97,18,18)?2:-1;
        if(action>=0){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));minecraft.gameMode.handleInventoryButtonClick(menu.containerId,action);return true;}return handled;
    }
}
