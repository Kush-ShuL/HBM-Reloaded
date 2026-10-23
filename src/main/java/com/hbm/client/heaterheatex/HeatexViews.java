package com.hbm.client.heaterheatex;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Viewer values come directly from the same active Coolable traits used by the actual machine. */
final class HeatexViews {
 record View(ItemStack hot,ItemStack cold,int heat,double efficiency){}
 static List<View> recipes(){var result=new ArrayList<View>();for(var hot:HbmFluids.getAll()){var trait=hot.getTrait(FluidTraits.Coolable.class);if(trait==null)continue;double efficiency=trait.getEfficiency(FluidTraits.CoolingType.HEATEXCHANGER);if(efficiency>0)result.add(new View(FluidIconItem.make(hot,trait.amountReq()),FluidIconItem.make(trait.coolsTo(),trait.amountProduced()),trait.heatEnergy(),efficiency));}return result;}
 static void draw(GuiGraphics graphics,int x,int y,View view){graphics.blit(ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei_fluid.png"),x,y,5,11,166,65,256,256);String energy=view.heat()+" TU × "+(int)(view.efficiency()*100)+"%";var font=Minecraft.getInstance().font;graphics.drawString(font,energy,x+(166-font.width(energy))/2,y+5,0x404040,false);}
}
