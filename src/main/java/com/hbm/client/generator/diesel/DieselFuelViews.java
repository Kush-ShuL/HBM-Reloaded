package com.hbm.client.generator.diesel;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.generator.diesel.DieselBlockEntity;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
final class DieselFuelViews {
    record View(ItemStack fuel,String energy){}
    static List<View> all(){var result=new ArrayList<View>();for(var fluid:HbmFluids.getInNiceOrder()){var energy=DieselBlockEntity.fuelEnergy(fluid);if(energy.signum()>0)result.add(new View(FluidIconItem.make(fluid,1),energy.toString()));}return result;}
    static void draw(GuiGraphics graphics,int x,int y,String energy){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(graphics,texture,x,y);graphics.blit(texture,x+47,y+23,5,87,18,18,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.drawString(Minecraft.getInstance().font,Component.translatable("gui.hbm.diesel.fuel_energy",energy),x+4,y+53,0x404040,false);}
}
