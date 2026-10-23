package com.hbm.client.pneumaticstorage;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.pneumaticstorage.*;
import com.hbm.pneumatic.PneumaticNetwork;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** Four actual source GUIs share rendering code, retaining each original PNG/size/layout and real controls. */
public final class PneumaticStorageScreen extends LegacyContainerScreen<PneumaticStorageMenu> {
 public PneumaticStorageScreen(PneumaticStorageMenu menu,Inventory inv,Component title){super(menu,inv,title,PneumaticStorageContent.id("textures/gui/storage/gui_pneumatic_"+menu.machine.kind.suffix+".png"),menu.machine.kind.width,menu.machine.kind.height);}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,88-font.width(title)/2,5,4210752,false);g.drawString(font,playerInventoryTitle,8,imageHeight-94,4210752,false);if(menu.machine instanceof PneumaticMono mono)for(int i=0;i<3;i++)if(!mono.getItem(i).isEmpty()){int amount=mono.amounts[i];String percent=" ("+((int)(amount*1000D/PneumaticMono.CAPACITY)/10D)+"%)";g.drawString(font,String.format(Locale.US,"%,d",amount)+percent,50,22+i*18,0x000000,false);}}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);if(menu.machine instanceof PneumaticMono mono)for(int i=0;i<3;i++)if(!mono.getItem(i).isEmpty())drawLegacyRegion(g,44,17+i*18,0,181,(int)((long)mono.amounts[i]*124/PneumaticMono.CAPACITY),16);
  if(menu.machine instanceof PneumaticStorageBase storage){
   // Explicit user adaptation: use linear gauges in the original meter footprint.
   linear(g,leftPos+175,topPos+18,18,14,storage.compair.getFill(),storage.compair.getMaxFill(),0xFFCA6C43);
   linear(g,leftPos+174,topPos+36,20,8,storage.compair.getPressure(),5,0xFFCA6C43);
  }
  if(menu.machine instanceof PneumaticExporter exporter){if(exporter.rorConfiguredMode){drawLegacyRegion(g,142,52,176,18,18,18);drawLegacyRegion(g,14,14,77,14,58,58);}if(!exporter.continuousRequest)drawLegacyRegion(g,142,16,176,0,18,18);if(exporter.requestMode==1)drawLegacyRegion(g,142,34,194,0,18,18);if(exporter.requestMode==2)drawLegacyRegion(g,142,34,194,18,18,18);}
 }
 static void linear(GuiGraphics g,int x,int y,int width,int height,long value,long maximum,int color){g.fill(x-1,y-2,x+width+1,y+height+2,0xFFC6C6C6);g.fill(x,y,x+width,y+height,0xFF373737);g.fill(x+1,y+1,x+width-1,y+height-1,0xFF8B8B8B);int fill=maximum<=0?0:(int)(Math.clamp((double)value/maximum,0,1)*(width-2));if(fill>0)g.fill(x+1,y+1,x+1+fill,y+height-1,color);}
 private void control(String key){PacketDistributor.sendToServer(new PneumaticStoragePackets.Control(menu.containerId,menu.position(),key));}
 @Override public boolean mouseClicked(double x,double y,int button){boolean result=super.mouseClicked(x,y,button);if(menu.machine instanceof PneumaticStorageBase&&legacyInfoHovered(x,y,174,36,20,8)){control("pressure");return true;}if(menu.machine instanceof PneumaticExporter){for(int i=0;i<3;i++)if(legacyInfoHovered(x,y,142,16+i*18,18,18)){control(new String[]{"continuous","request","ror"}[i]);return true;}}return result;}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);if(menu.machine instanceof PneumaticStorageBase storage){if(legacyInfoHovered(x,y,174,36,20,8))g.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.pneumatic.pressure",storage.compair.getPressure()),Component.translatable("gui.hbm.pneumatic.range",PneumaticNetwork.getRangeFromPressure(storage.compair.getPressure()))),x,y);if(legacyInfoHovered(x,y,175,16,18,18))g.renderComponentTooltip(font,List.of(storage.compair.getTankType().displayName(),Component.literal(storage.compair.getFill()+"/"+storage.compair.getMaxFill()+"mB")),x,y);}
  if(menu.machine instanceof PneumaticExporter exporter){if(legacyInfoHovered(x,y,142,16,18,18))g.renderTooltip(font,Component.translatable("gui.hbm.pneumatic_storage.request_mode",Component.translatable(exporter.continuousRequest?"gui.hbm.pneumatic_storage.continuous":"gui.hbm.pneumatic_storage.by_request").withStyle(ChatFormatting.YELLOW)),x,y);if(legacyInfoHovered(x,y,142,34,18,18))g.renderTooltip(font,Component.translatable("gui.hbm.pneumatic_storage.request_type",Component.translatable(switch(exporter.requestMode){case 1->"gui.hbm.pneumatic_storage.full_stack";case 2->"gui.hbm.pneumatic_storage.full_request";default->"gui.hbm.pneumatic_storage.as_much";}).withStyle(ChatFormatting.YELLOW)),x,y);if(legacyInfoHovered(x,y,142,52,18,18)){var lines=new ArrayList<Component>();lines.add(Component.translatable("gui.hbm.pneumatic_storage.filter_type",Component.translatable(exporter.rorConfiguredMode?"gui.hbm.pneumatic_storage.ror":"gui.hbm.pneumatic_storage.manual").withStyle(ChatFormatting.YELLOW)));if(exporter.rorConfiguredMode)for(int i=0;i<9;i++){var filter=exporter.rorFilters[i];lines.add(filter[0]==0||filter[2]<=0?Component.translatable("gui.hbm.pneumatic_storage.ror_slot_empty",i+1):Component.translatable("gui.hbm.pneumatic_storage.ror_slot",i+1,filter[0],filter[1],filter[2]));}g.renderComponentTooltip(font,lines,x,y);}}
 }
}
