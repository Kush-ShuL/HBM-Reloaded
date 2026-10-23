package com.hbm.client.oilburner;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.*;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.oilburner.*;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
/** Source176x203 GUI, exact active switch/artwork and its narrower original clickable rectangle. */
public final class OilburnerScreen extends LegacyContainerScreen<OilburnerMenu> {
 public OilburnerScreen(OilburnerMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/machine/gui_oilburner.png"),176,203);}
 private OilburnerBlockEntity machine(){return minecraft.level.getBlockEntity(menu.position) instanceof OilburnerBlockEntity heater?heater:null;}
 @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,(176-font.width(title))/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,109,4210752,false);}
 @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var heater=machine();if(heater==null)return;int height=heater.heatEnergy*52/OilburnerBlockEntity.MAX_HEAT;drawLegacyRegion(graphics,116,69-height,194,52-height,16,height);if(heater.isOn){drawLegacyRegion(graphics,70,54,210,0,35,14);if(heater.tank.getFill()>0&&heater.tank.getTankType().hasTrait(FluidTraits.Flammable.class))drawLegacyRegion(graphics,79,34,176,0,18,18);}tank(graphics,heater.tank);}
 private void tank(GuiGraphics graphics,FluidTank tank){if(tank.getMaxFill()==0)return;int height=tank.getFill()*52/tank.getMaxFill();if(height<=0)return;int tint=tank.getTankType().getTint();graphics.flush();RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();graphics.setColor((tint>>16&255)/255F,(tint>>8&255)/255F,(tint&255)/255F,1);graphics.blit(tank.getTankType().getTexture(),leftPos+44,topPos+69-height,0,16-height,16,height,16,16);graphics.flush();graphics.setColor(1,1,1,1);RenderSystem.disableBlend();}
 @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var heater=machine();if(heater==null)return;if(legacyInfoHovered(x,y,116,17,16,52))graphics.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d / %,d TU",Math.min(heater.heatEnergy,OilburnerBlockEntity.MAX_HEAT),OilburnerBlockEntity.MAX_HEAT)),x,y);if(heater.tank.getTankType().hasTrait(FluidTraits.Flammable.class)&&legacyInfoHovered(x,y,79,34,18,18))graphics.renderComponentTooltip(font,List.of(Component.literal(heater.setting+" mB/t"),Component.literal(String.format(Locale.US,"%,d TU/t",heater.heatPerMb()*heater.setting))),x,y);if(legacyInfoHovered(x,y,44,17,16,52)){var tank=heater.tank;var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0){lines.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));lines.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_RED));}FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}}
 @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,80,54,16,14)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));PacketDistributor.sendToServer(new OilburnerControl(menu.containerId));return true;}return handled;}
}
