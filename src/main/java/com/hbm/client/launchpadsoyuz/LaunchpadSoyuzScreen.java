package com.hbm.client.launchpadsoyuz;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.launchpadsoyuz.*;
import com.hbm.machine.launchpadsoyuz.LaunchpadSoyuzEntity.SoyuzStatus;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Original194x244 GUI; actual5 phase statuses, cargo/satellite and launch packets and unmodified source coordinates. */
public final class LaunchpadSoyuzScreen extends LegacyContainerScreen<LaunchpadSoyuzMenu> {
    public LaunchpadSoyuzScreen(LaunchpadSoyuzMenu menu,Inventory inventory,Component title){super(menu,inventory,title,LaunchpadSoyuzContent.id("textures/gui/machine/gui_launchpad_soyuz.png"),194,244);}
    private LaunchpadSoyuzEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position()) instanceof LaunchpadSoyuzEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int x,int y){
        graphics.drawString(font,title,imageWidth/2-font.width(title)/2,4,0xFFFFFF,false);graphics.drawString(font,playerInventoryTitle,17,imageHeight-96+2,4210752,false);var machine=machine();if(machine==null)return;
        if(machine.soyuzStatus==SoyuzStatus.LAUNCHING){String seconds=Integer.toString(machine.countdown/20),cents=Integer.toString((machine.countdown%20)*5);if(seconds.length()==1)seconds="0"+seconds;if(cents.length()==1)cents+="0";graphics.drawString(font,seconds+":"+cents,85,121,0xFF0000,false);}
        else{String phase=switch(machine.soyuzStatus){case ABSENT->"idle";case LOADING->"loading";case FUELING->"fueling";default->"ready";};int color=switch(machine.soyuzStatus){case ABSENT->0xFF0000;case LOADING->0xFF8000;case FUELING->0xFFFF00;default->0x00FF00;};String label=Component.translatable("desc.gui.soyuz."+phase).getString();float scale=Math.min(1F,22F/Math.max(1,font.width(label)));var pose=graphics.pose();pose.pushPose();pose.scale(scale,scale,1);graphics.drawString(font,label,(int)(97/scale-font.width(label)/2F),(int)(125/scale-font.lineHeight/2F),color,false);pose.popPose();}
    }
    @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){
        super.renderBg(graphics,partial,x,y);var machine=machine();if(machine==null)return;int power=(int)(machine.power*52/LaunchpadSoyuzEntity.MAX_POWER);if(power>0)drawLegacyRegion(graphics,134,96-power,194,52-power,16,power);
        drawLegacyRegion(graphics,97-(machine.cargoMode?18:0),52,228-(machine.cargoMode?18:0),26,18,18);
        drawLegacyRegion(graphics,157,31,machine.hasJetFuel()?210:216,0,6,8);drawLegacyRegion(graphics,175,31,machine.hasOxidizer()?210:216,0,6,8);drawLegacyRegion(graphics,139,31,machine.power>=LaunchpadSoyuzEntity.CONSUMPTION?210:216,0,6,8);
        int orbital=machine.orbital();if(orbital>0)drawLegacyRegion(graphics,79,25,210+(orbital-1)*18,8,18,18);drawLegacyRegion(graphics,97,79,machine.loadedType==-1?210:228,8,18,18);
        if(!machine.cargoMode)drawLegacyRegion(graphics,97,25,machine.getItem(2).isEmpty()?210:228,8,18,18);else drawLegacyRegion(graphics,79,79,machine.getItem(1).isEmpty()?210:228,8,18,18);
        if(machine.soyuzStatus==SoyuzStatus.LAUNCHING)drawLegacyRegion(graphics,88,97,210,44,18,18);tank(graphics,machine.tanks[0],152);tank(graphics,machine.tanks[1],170);
    }
    private void tank(GuiGraphics graphics,FluidTank tank,int offset){
        int height=Mth.clamp(tank.getFill()*52/Math.max(tank.getMaxFill(),1),0,52);if(height==0)return;graphics.flush();var matrix=graphics.pose().last().pose();int color=tank.getTankType().getTint(),x=leftPos+offset,y=topPos+96;
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);RenderSystem.setShaderTexture(0,tank.getTankType().getTexture());RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();var out=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX_COLOR);
        out.addVertex(matrix,x,y,0).setUv(0,1).setColor(color>>16&255,color>>8&255,color&255,255);out.addVertex(matrix,x+16,y,0).setUv(1,1).setColor(color>>16&255,color>>8&255,color&255,255);out.addVertex(matrix,x+16,y-height,0).setUv(1,1-height/16F).setColor(color>>16&255,color>>8&255,color&255,255);out.addVertex(matrix,x,y-height,0).setUv(0,1-height/16F).setColor(color>>16&255,color>>8&255,color&255,255);BufferUploader.drawWithShader(out.buildOrThrow());RenderSystem.disableBlend();
    }
    private List<Component> description(String key){return Arrays.stream(Component.translatable(key).getString().split("\\$")).map(Component::literal).map(value->(Component)value).toList();}
    @Override public void render(GuiGraphics graphics,int x,int y,float partial){
        super.render(graphics,x,y,partial);var machine=machine();if(machine==null)return;
        for(int i=0;i<2;i++)if(legacyInfoHovered(x,y,152+i*18,44,16,52)){var tank=machine.tanks[i];var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+" / "+tank.getMaxFill()+" mB"));FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
        if(legacyInfoHovered(x,y,134,44,16,52))graphics.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d / %,d HE",machine.power,LaunchpadSoyuzEntity.MAX_POWER)),x,y);
        if(legacyInfoHovered(x,y,79,52,18,18))graphics.renderComponentTooltip(font,description("desc.gui.soyuz.cargo"),x,y);if(legacyInfoHovered(x,y,97,52,18,18))graphics.renderComponentTooltip(font,description("desc.gui.soyuz.satellite"),x,y);
    }
    @Override public boolean mouseClicked(double x,double y,int button){
        boolean handled=super.mouseClicked(x,y,button);var machine=machine();if(machine==null)return handled;CompoundTag data=null;
        if(legacyInfoHovered(x,y,79,52,18,18)){data=new CompoundTag();data.putBoolean("cargo",true);}if(legacyInfoHovered(x,y,97,52,18,18)){data=new CompoundTag();data.putBoolean("cargo",false);}
        if(legacyInfoHovered(x,y,88,97,18,18)&&machine.soyuzStatus==SoyuzStatus.READY){data=new CompoundTag();data.putBoolean("launch",true);}
        if(data!=null){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));PacketDistributor.sendToServer(new LaunchpadSoyuzControl(menu.position(),data));return true;}return handled;
    }
}
