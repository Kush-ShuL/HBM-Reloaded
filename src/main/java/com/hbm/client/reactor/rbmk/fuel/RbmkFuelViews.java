package com.hbm.client.reactor.rbmk.fuel;
import com.hbm.machine.reactor.rbmk.fuel.*;import java.util.*;import com.hbm.registry.HbmContent;import net.minecraft.client.gui.GuiGraphics;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.*;
/** Source ComparableStackHeat's exact sample values and NEIUniversalHandler layout. Physical cooling stays in the actual FuelPoolRod implementation. */
final class RbmkFuelViews {
 record View(ItemStack input,ItemStack output,boolean cooling){}
 static boolean hot(ItemStack stack){return RbmkFuelRodItem.getCoreHeat(stack)>=50||RbmkFuelRodItem.getHullHeat(stack)>=50;}
 static List<View> disassembly(){var result=new ArrayList<View>();for(var rod:RbmkFuelRodItem.craftableRods)for(int enrichment=0;enrichment<=4;enrichment++){var input=new ItemStack(rod);RbmkFuelRodItem.setYield(input,Math.min(1-enrichment/5D,.99)*rod.yield);result.add(new View(input,RbmkFuelDisassembly.result(input),false));if(rod.pellet.isXenonEnabled()){input=input.copy();RbmkFuelRodItem.setPoison(input,50);result.add(new View(input,RbmkFuelDisassembly.result(input),false));}}return List.copyOf(result);}
 static List<View> cooling(){var result=new ArrayList<View>();for(var rod:RbmkFuelRodItem.craftableRods){var output=new ItemStack(rod);RbmkFuelRodItem.setYield(output,.2*rod.yield);var input=output.copy();RbmkFuelRodItem.setCoreHeat(input,100);RbmkFuelRodItem.setHullHeat(input,50);result.add(new View(input,output,true));}return List.copyOf(result);}
 static ItemStack catalyst(boolean cooling){return cooling?new ItemStack(HbmContent.item("machine_waste_drum")):new ItemStack(Items.CRAFTING_TABLE);}
 static void draw(GuiGraphics g,int x,int y){var tex=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");g.blit(tex,x+47,y+23,5,87,18,18);g.blit(tex,x+101,y+23,5,87,18,18);g.blit(tex,x+74,y+14,59,87,18,36);}private RbmkFuelViews(){}
}
