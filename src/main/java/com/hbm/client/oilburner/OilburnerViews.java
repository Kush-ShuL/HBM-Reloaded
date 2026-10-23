package com.hbm.client.oilburner;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** One actual mB of native fluid -> original truncated heat per mB. TU is a resource, not a fake output item. */
final class OilburnerViews {
 record View(ItemStack fuel,int heat){}
 static List<View> recipes(){var result=new ArrayList<View>();for(var type:HbmFluids.getAll()){var trait=type.getTrait(FluidTraits.Flammable.class);if(trait!=null)result.add(new View(FluidIconItem.make(type,1),(int)(trait.getHeatEnergy()/1000)));}return result;}
 static void draw(GuiGraphics graphics,int x,int y,View view){graphics.blit(ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei_fluid.png"),x,y,5,11,166,65,256,256);String heat=String.format(Locale.US,"%,d TU",view.heat());graphics.drawString(Minecraft.getInstance().font,heat,x+158-Minecraft.getInstance().font.width(heat),y+28,0x404040,false);}
}
