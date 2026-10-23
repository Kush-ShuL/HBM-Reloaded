package com.hbm.client.industrialboiler;
import com.hbm.fluid.*;
import com.hbm.item.fluid.FluidIconItem;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Source BoilingHandler: NiceOrder,positive BOILER efficiency and the actual first Heatable step. */
final class BoilingViews {
 record View(ItemStack input,ItemStack output){}
 static List<View> recipes(){var result=new ArrayList<View>();for(var type:HbmFluids.getInNiceOrder()){var trait=type.getTrait(FluidTraits.Heatable.class);if(trait!=null&&trait.getEfficiency(FluidTraits.HeatingType.BOILER)>0){var step=trait.getFirstStep();result.add(new View(FluidIconItem.make(type,step.amountReq()),FluidIconItem.make(step.typeProduced(),step.amountProduced())));}}return result;}
 static List<ItemStack> machines(){var result=new ArrayList<ItemStack>();for(String name:List.of("machine_boiler","machine_industrial_boiler")){var id=ResourceLocation.fromNamespaceAndPath("hbm",name);if(BuiltInRegistries.ITEM.containsKey(id))result.add(new ItemStack(BuiltInRegistries.ITEM.get(id)));}return result;}
 static void draw(GuiGraphics graphics,int x,int y){var texture=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(graphics,texture,x,y);graphics.blit(texture,x+47,y+23,5,87,18,18,256,256);graphics.blit(texture,x+101,y+23,5,87,18,18,256,256);graphics.blit(texture,x+74,y+14,59,87,18,36,256,256);}
}
