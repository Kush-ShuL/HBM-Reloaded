package com.hbm.client.reactor.rbmk.boiler;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTank;
import com.hbm.machine.reactor.rbmk.boiler.*;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Original176x186 boiler screen; native water/steam gauges and exact source UVs. */
public final class RbmkBoilerScreen extends LegacyContainerScreen<RbmkBoilerMenu> {
    public RbmkBoilerScreen(RbmkBoilerMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/reactors/gui_rbmk_boiler.png"),176,186);}
    private RbmkBoilerEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof RbmkBoilerEntity boiler?boiler:null;}
    @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);}
    @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var boiler=machine();if(boiler==null)return;int i=boiler.feed.getFill()*58/boiler.feed.getMaxFill();drawLegacyRegion(g,126,82-i,176,58-i,14,i);int j=boiler.steam.getFill()*22/boiler.steam.getMaxFill();if(j>0)j++;if(j>22)j++;drawLegacyRegion(g,91,65-j,190,24-j,4,j);drawLegacyRegion(g,36,24,194+14*RbmkBoilerEntity.steamIndex(boiler.steam.getTankType()),0,14,58);}
    private void tank(GuiGraphics g,FluidTank tank,int x,int y,int rx,int ry,int w,int h){if(legacyInfoHovered(x,y,rx,ry,w,h))g.renderComponentTooltip(font,List.of(tank.getTankType().displayName(),Component.literal(tank.getFill()+" / "+tank.getMaxFill()+" mB")),x,y);}
    @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var boiler=machine();if(boiler==null)return;tank(g,boiler.feed,x,y,126,24,16,56);tank(g,boiler.steam,x,y,89,39,8,28);}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(machine()!=null&&legacyInfoHovered(x,y,33,21,20,64)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));PacketDistributor.sendToServer(new RbmkBoilerControl(menu.containerId));return true;}return handled;}
}
