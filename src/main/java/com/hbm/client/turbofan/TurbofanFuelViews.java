package com.hbm.client.turbofan;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Real registered AERO Combustible traits, nominal level0 output, not another recipe/combustion backend. */
final class TurbofanFuelViews {
 record View(ItemStack fuel,String energy){}static List<View> all(){var result=new ArrayList<View>();for(var fluid:HbmFluids.getInNiceOrder()){var trait=fluid.getTrait(FluidTraits.Combustible.class);if(trait!=null&&trait.getGrade()==FluidTraits.FuelGrade.AERO){long value=trait.getCombustionEnergy()/1000;int output=(int)(value*1D);if(output>0)result.add(new View(FluidIconItem.make(fluid,1),Integer.toString(output)));}}return result;}
 static void draw(GuiGraphics g,int x,int y,String energy){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);g.blit(texture,x+47,y+23,5,87,18,18,256,256);g.blit(texture,x+101,y+23,5,87,18,18,256,256);g.drawString(Minecraft.getInstance().font,Component.translatable("gui.hbm.turbofan.base_energy",energy),x+4,y+53,0x404040,false);}
}
