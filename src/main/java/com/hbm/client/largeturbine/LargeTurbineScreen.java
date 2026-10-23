package com.hbm.client.largeturbine;
import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.fluid.*;
import com.hbm.item.battery.BatteryItem;
import com.hbm.item.fluid.client.FluidInfo;
import com.hbm.machine.largeturbine.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
/** Exact original176x168 GUI,steamcompression icon,verticalgauges and source invalidfluid warning. */
public final class LargeTurbineScreen extends LegacyContainerScreen<LargeTurbineMenu> {
 public LargeTurbineScreen(LargeTurbineMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.parse("hbm:textures/gui/generators/gui_turbine_large.png"),176,168);}
 private LargeTurbineEntity machine(){return minecraft!=null&&minecraft.level!=null&&minecraft.level.getBlockEntity(menu.position) instanceof LargeTurbineEntity t?t:null;}
 @Override protected void renderLabels(GuiGraphics g,int x,int y){g.drawString(font,title,imageWidth/2-font.width(title)/2,6,4210752,false);g.drawString(font,playerInventoryTitle,8,imageHeight-96+2,4210752,false);}
 @Override protected void renderBg(GuiGraphics g,float partial,int x,int y){super.renderBg(g,partial,x,y);var t=machine();if(t==null)return;var in=t.tanks[0].getTankType();if(in==HbmFluids.STEAM||in==HbmFluids.HOTSTEAM||in==HbmFluids.SUPERHOTSTEAM||in==HbmFluids.ULTRAHOTSTEAM)drawLegacyRegion(g,99,18,183,t.steamType()*14,14,14);int power=LargeTurbineEntity.maxPower==0?0:(int)(t.power*34/LargeTurbineEntity.maxPower);drawLegacyRegion(g,123,69-power,176,34-power,7,power);if(t.tanks[1].getTankType()==HbmFluids.NONE)g.blit(ResourceLocation.parse("hbm:textures/gui/gui_utility.png"),leftPos-16,topPos+68,8,16,16,16,256,256);tank(g,t.tanks[0],62);tank(g,t.tanks[1],134);}
 private void tank(GuiGraphics g,FluidTank tank,int x){int fill=(int)((long)tank.getFill()*52/tank.getMaxFill());if(fill<=0)return;int tint=tank.getTankType().getTint();g.setColor((tint>>16&255)/255F,(tint>>8&255)/255F,(tint&255)/255F,1);for(int done=0;done<fill;done+=16){int h=Math.min(16,fill-done);g.blit(tank.getTankType().getTexture(),leftPos+x,topPos+69-done-h,0,16-h,16,h,16,16);}g.setColor(1,1,1,1);}
 @Override public void render(GuiGraphics g,int x,int y,float partial){super.render(g,x,y,partial);var t=machine();if(t==null)return;for(int i=0;i<2;i++)if(legacyInfoHovered(x,y,i==0?62:134,17,16,52)){var tank=t.tanks[i];var info=new ArrayList<Component>();info.add(tank.getTankType().displayName());info.add(Component.literal(tank.getFill()+"/"+tank.getMaxFill()+"mB"));if(tank.getPressure()!=0)info.add(Component.translatable("gui.hbm.chemical.pressure",tank.getPressure()).withStyle(ChatFormatting.RED));FluidInfo.append(tank.getTankType(),info,hasShiftDown());g.renderComponentTooltip(font,info,x,y);}if(t.tanks[1].getTankType()==HbmFluids.NONE&&legacyInfoHovered(x,y,-16,68,16,16))g.renderComponentTooltip(font,List.of(Component.translatable("gui.hbm.large_turbine.invalid_fluid")),leftPos-8,topPos+84);if(legacyInfoHovered(x,y,123,35,7,34))g.renderComponentTooltip(font,List.of(Component.translatable("tooltip.hbm.battery.energy_stored",BatteryItem.shortNumber(t.power),BatteryItem.shortNumber(LargeTurbineEntity.maxPower))),x,y);}
}
