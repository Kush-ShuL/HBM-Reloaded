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
 private int burnRate=1;private boolean usingSlider;
 public OilburnerScreen(OilburnerMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/machine/gui_oilburner.png"),176,203);}
 private OilburnerBlockEntity machine(){return minecraft.level.getBlockEntity(menu.position) instanceof OilburnerBlockEntity heater?heater:null;}
 @Override protected void init(){super.init();var heater=machine();if(heater!=null)burnRate=heater.burnRate;usingSlider=false;}
 @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,(176-font.width(title))/2,6,4210752,false);graphics.drawString(font,playerInventoryTitle,8,109,4210752,false);}
 @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var heater=machine();if(heater==null)return;int height=heater.heatEnergy*52/Math.max(heater.maxHeatEnergy,1);drawLegacyRegion(graphics,116,69-height,194,52-height,16,height);if(burnRate>heater.maxBurnRate)burnRate=heater.burnRate;int slider=(burnRate-1)*32/Math.max(heater.maxBurnRate-1,1);drawLegacyRegion(graphics,71+slider,23,210,14,4,8);if(heater.isOn){drawLegacyRegion(graphics,70,54,210,0,35,14);if(heater.tank.getFill()>0&&heater.tank.getTankType().hasTrait(FluidTraits.Flammable.class))drawLegacyRegion(graphics,79,34,176,0,18,18);}tank(graphics,heater.tank);}
 private void tank(GuiGraphics graphics,FluidTank tank){if(tank.getMaxFill()==0)return;int height=tank.getFill()*52/tank.getMaxFill();if(height<=0)return;int tint=tank.getTankType().getTint();graphics.flush();RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();graphics.setColor((tint>>16&255)/255F,(tint>>8&255)/255F,(tint&255)/255F,1);graphics.blit(tank.getTankType().getTexture(),leftPos+44,topPos+69-height,0,16-height,16,height,16,16);graphics.flush();graphics.setColor(1,1,1,1);RenderSystem.disableBlend();}
 @Override public void render(GuiGraphics graphics,int x,int y,float partial){
  super.render(graphics,x,y,partial);var heater=machine();if(heater==null)return;
  if(legacyInfoHovered(x,y,116,17,16,52))graphics.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d / %,d TU",heater.heatEnergy,heater.maxHeatEnergy)),x,y);
  if(usingSlider||legacyInfoHovered(x,y,70,23,36,8))graphics.renderTooltip(font,Component.literal(burnRate+" mB/t"),Math.clamp(x,leftPos+70,leftPos+106),Math.clamp(y,topPos+23,topPos+31));
  if(heater.tank.getTankType().hasTrait(FluidTraits.Flammable.class)&&legacyInfoHovered(x,y,79,34,18,18)){var trait=heater.tank.getTankType().getTrait(FluidTraits.Flammable.class);int heat=(int)(trait.getHeatEnergy()*burnRate/1000);graphics.renderComponentTooltip(font,List.of(Component.literal(String.format(Locale.US,"%,d TU/t",heat)).withStyle(ChatFormatting.YELLOW),Component.literal(String.format(Locale.US,"%,d TU/s",heat*20)).withStyle(ChatFormatting.YELLOW)),x,y);}
  if(legacyInfoHovered(x,y,70,54,35,15))graphics.renderTooltip(font,Component.translatable("gui.hbm.oilburner.ignition"),x,y);
  if(legacyInfoHovered(x,y,44,17,16,52)){var tank=heater.tank;var lines=new ArrayList<Component>();lines.add(tank.getTankType().displayName());lines.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0){lines.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));lines.add(Component.translatable("gui.hbm.chemical.pressurized").withStyle(System.currentTimeMillis()%1000<500?ChatFormatting.RED:ChatFormatting.DARK_RED));}FluidInfo.append(tank.getTankType(),lines,hasShiftDown());graphics.renderComponentTooltip(font,lines,x,y);}
 }
 private void slide(double x){var heater=machine();if(heater==null)return;int value=Math.clamp((int)((x-leftPos-70)*heater.maxBurnRate/32),1,heater.maxBurnRate);if(value!=burnRate){burnRate=value;PacketDistributor.sendToServer(new OilburnerControl(menu.containerId,burnRate));}}
 @Override public boolean mouseClicked(double x,double y,int button){boolean handled=super.mouseClicked(x,y,button);if(legacyInfoHovered(x,y,80,54,16,14)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));PacketDistributor.sendToServer(new OilburnerControl(menu.containerId));return true;}if(legacyInfoHovered(x,y,70,23,36,8)){minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK,1));usingSlider=true;slide(x);return true;}return handled;}
 @Override public boolean mouseDragged(double x,double y,int button,double dx,double dy){if(usingSlider){slide(x);return true;}return super.mouseDragged(x,y,button,dx,dy);}
 @Override public boolean mouseReleased(double x,double y,int button){if(button==0||button==1)usingSlider=false;return super.mouseReleased(x,y,button);}
}
