package com.hbm.client.heatingoven;

import com.hbm.client.gui.LegacyContainerScreen;
import com.hbm.machine.heatingoven.*;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** Original GUIFirebox layout with the HeatingOven's white title and original texture. */
public final class HeatingOvenScreen extends LegacyContainerScreen<HeatingOvenMenu> {
 public HeatingOvenScreen(HeatingOvenMenu menu,Inventory inventory,Component title){super(menu,inventory,title,ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/machine/gui_heating_oven.png"),176,168);}
 private HeatingOvenBlockEntity machine(){return minecraft.level.getBlockEntity(menu.position) instanceof HeatingOvenBlockEntity oven?oven:null;}
 @Override protected void renderLabels(GuiGraphics graphics,int x,int y){graphics.drawString(font,title,(imageWidth-font.width(title))/2,6,0xffffff,false);graphics.drawString(font,playerInventoryTitle,8,74,0x404040,false);}
 @Override protected void renderBg(GuiGraphics graphics,float partial,int x,int y){super.renderBg(graphics,partial,x,y);var oven=machine();if(oven==null)return;drawLegacyRegion(graphics,81,28,176,0,oven.heatEnergy*69/HeatingOvenConfig.maxHeat,5);drawLegacyRegion(graphics,81,37,176,5,oven.burnTime*70/Math.max(1,oven.maxBurnTime),5);if(oven.wasOn)drawLegacyRegion(graphics,25,26,176,10,18,18);}
 @Override public void render(GuiGraphics graphics,int x,int y,float partial){super.render(graphics,x,y,partial);var oven=machine();if(oven==null)return;if(legacyInfoHovered(x,y,80,27,71,7))graphics.renderTooltip(font,Component.literal(String.format(Locale.US,"%,d / %,dTU",oven.heatEnergy,HeatingOvenConfig.maxHeat)),x,y);if(legacyInfoHovered(x,y,80,36,71,7))graphics.renderComponentTooltip(font,List.of(Component.literal(oven.burnHeat+"TU/t"),Component.literal(oven.burnTime/20+"s")),x,y);if(menu.getCarried().isEmpty())for(int i=0;i<2;i++){var slot=menu.getSlot(i);if(!slot.hasItem()&&legacyInfoHovered(x,y,slot.x,slot.y,16,16)){var lines=bonuses();if(!lines.isEmpty())graphics.renderComponentTooltip(font,lines,x,y);}}}
 private static List<Component> bonuses(){var lines=new ArrayList<Component>();double[][] mods={HeatingOvenConfig.BURN.getModTime(),HeatingOvenConfig.BURN.getModHeat()};for(int type=0;type<2;type++){var section=new ArrayList<Component>();for(int i=0;i<8;i++)if(mods[type][i]!=1){int percent=(int)((mods[type][i]-1)*100);section.add(Component.literal("- ").append(Component.translatable("gui.hbm.firebox.fuel."+i)).append(": ").withStyle(ChatFormatting.YELLOW).append(Component.literal((percent>=0?"+":"")+percent+"%").withStyle(percent>=0?ChatFormatting.GREEN:ChatFormatting.RED)));}if(!section.isEmpty()){lines.add(Component.translatable(type==0?"gui.hbm.firebox.time_bonus":"gui.hbm.firebox.heat_bonus").withStyle(type==0?ChatFormatting.GOLD:ChatFormatting.RED));lines.addAll(section);}}return lines;}
}
