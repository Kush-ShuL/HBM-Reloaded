package com.hbm.client.gascentrifuge;
import com.hbm.machine.gascentrifuge.*;import java.util.*;import net.minecraft.client.Minecraft;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.network.chat.Component;import net.minecraft.world.item.ItemStack;
final class GasCentrifugeViews {
 static List<ItemStack> batteries(){var result=new ArrayList<ItemStack>();result.addAll(com.hbm.item.battery.BatteryContent.packStacks());result.addAll(com.hbm.item.battery.BatteryContent.selfChargingStacks());result.add(new ItemStack(com.hbm.item.battery.BatteryContent.CREATIVE.get()));return result;}
 static int[] output(int index){return new int[]{index%2==0?129:147,index<2?15:33};}
 static void draw(GasCentrifugeRecipe recipe,GuiGraphics g,int x,int y){var texture=GasCentrifugeContent.id("textures/gui/nei/gui_nei_centrifuge_gas.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);int power=(int)(System.currentTimeMillis()/50%480)*34/480;g.blit(texture,x+3,y+6+34-power,176,34-power,16,power,256,256);int period=recipe.highSpeed()?80:150;int progress=(int)(System.currentTimeMillis()/50%period)*44/period;g.blit(texture,x+74,y+17,208,0,progress,37,256,256);if(recipe.highSpeed())g.blit(texture,x+25,y+24,192,0,16,16,256,256);var font=com.hbm.client.gui.font.LegacyFonts.get();var text=Component.translatable("jei.hbm.gascentrifuge.machines",recipe.machines());g.drawString(font,text,x+50-font.width(text)/2,y+10,0x00FF00,false);}
 private GasCentrifugeViews(){}
}
