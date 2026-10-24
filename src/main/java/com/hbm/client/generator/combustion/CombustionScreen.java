package com.hbm.client.generator.combustion;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.FluidTraits;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.generator.combustion.*;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Exact176×203 original panel, ignition x89..104 and continuous source30-position drag slider. */
public final class CombustionScreen extends LegacyContainerScreen<CombustionMenu> {
    private int setting;private boolean locked;
    public CombustionScreen(CombustionMenu menu,Inventory player,Component title){super(menu,player,title,ResourceLocation.parse("hbm:textures/gui/generators/gui_combustion.png"),176,203);if(player.player.level().getBlockEntity(menu.position) instanceof CombustionBlockEntity machine)setting=machine.setting;}
    private CombustionBlockEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof CombustionBlockEntity machine?machine:null;}
    @Override protected void renderLabels(GuiGraphics graphics,int mouseX,int mouseY){graphics.drawString(font,playerInventoryTitle,8,109,4210752,false);}
    @Override protected void renderBg(GuiGraphics graphics,float partial,int mouseX,int mouseY){super.renderBg(graphics,partial,mouseX,mouseY);var engine=machine();if(engine==null)return;if(engine.getItem(2).getItem() instanceof PistonSetItem piston)drawLegacyRegion(graphics,80,51,176,52+piston.type()*12,25,12);drawLegacyRegion(graphics,79+setting*32/30,38,192,15,4,8);if(engine.isOn)drawLegacyRegion(graphics,79,13,192,0,35,15);int height=engine.getPowerExact().multiply(java.math.BigInteger.valueOf(53)).divide(java.math.BigInteger.valueOf(CombustionBlockEntity.MAX_POWER)).intValue();if(height>0)drawLegacyRegion(graphics,143,69-height,176,52-height,16,height);drawTank(graphics,engine);}
    private void drawTank(GuiGraphics graphics,CombustionBlockEntity engine){var tank=engine.tank;int height=tank.getFill()*52/tank.getMaxFill();if(height<=0)return;int color=tank.getTankType().getTint();graphics.flush();RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();graphics.setColor((color>>16&255)/255F,(color>>8&255)/255F,(color&255)/255F,1);graphics.blit(tank.getTankType().getTexture(),leftPos+35,topPos+69-height,0,16-height,16,height,16,16);graphics.flush();graphics.setColor(1,1,1,1);RenderSystem.disableBlend();}
    @Override public void render(GuiGraphics graphics,int mouseX,int mouseY,float partial){super.render(graphics,mouseX,mouseY,partial);var engine=machine();if(engine==null)return;if(!locked){if(legacyInfoHovered(mouseX,mouseY,143,17,16,52))graphics.renderTooltip(font,Component.literal(engine.getPowerExact()+" / "+CombustionBlockEntity.MAX_POWER+" HE"),mouseX,mouseY);if(legacyInfoHovered(mouseX,mouseY,35,17,16,52)){var lines=new ArrayList<Component>();lines.add(engine.tank.getTankType().displayName());lines.add(Component.literal(engine.tank.getFill()+" / "+engine.tank.getMaxFill()+" mB"));FluidInfo.append(engine.tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,mouseX,mouseY);}}
        if(locked||legacyInfoHovered(mouseX,mouseY,80,38,34,8))graphics.renderTooltip(font,Component.literal(setting*.2+"mB/t"),Mth.clamp(mouseX,leftPos+80,leftPos+114),Mth.clamp(mouseY,topPos+38,topPos+46));
        if(engine.getItem(2).getItem() instanceof PistonSetItem&&legacyInfoHovered(mouseX,mouseY,79,50,35,14)){var trait=engine.tank.getTankType().getTrait(FluidTraits.Combustible.class);double power=trait==null?0:setting*.2*trait.getCombustionEnergy()/1000D*engine.efficiency();graphics.renderComponentTooltip(font,List.of(Component.literal(String.format(Locale.US,"%,d",(int)power)+" HE/t").withStyle(ChatFormatting.YELLOW),Component.literal(String.format(Locale.US,"%,d",(int)(power*20))+" HE/s").withStyle(ChatFormatting.YELLOW)),mouseX,mouseY);}
        if(legacyInfoHovered(mouseX,mouseY,79,13,35,15))graphics.renderTooltip(font,Component.translatable("gui.hbm.combustion.ignition"),mouseX,mouseY);
        // Source drawScreen updates this while locked, preserving mouse-based quantization and packet-on-change.
        if(locked){int next=Mth.clamp((mouseX-leftPos-81)*30/32,0,30);if(next!=setting){setting=next;PacketDistributor.sendToServer(new CombustionControl(menu.position,next));}}
    }
    private void click(){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));}
    @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,89,13,16,14)){click();PacketDistributor.sendToServer(new CombustionControl(menu.position,-1));handled=true;}if(legacyInfoHovered(x,y,79,38,36,8)){click();locked=true;handled=true;}return handled;}
    @Override public boolean mouseReleased(double x,double y,int button){if(button==0||button==1)locked=false;return super.mouseReleased(x,y,button);}
}
