package com.hbm.machine.ashpit.client;
import com.hbm.fluid.HbmFluids;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.ashpit.AshpitBlockEntity;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
/** Exact AshpitHandler examples; unavailable future machines do not become invented catalysts. */
final class AshpitViews {
 record View(List<ItemStack> machines,List<ItemStack> input,ItemStack ash){}
 private static List<ItemStack> existing(String... names){var result=new ArrayList<ItemStack>();for(String name:names){var id=ResourceLocation.parse(name);if(BuiltInRegistries.ITEM.containsKey(id)){var stack=new ItemStack(BuiltInRegistries.ITEM.get(id));if(!stack.isEmpty())result.add(stack);}}return result;}
 static List<View> recipes(){var list=new ArrayList<View>();var ovens=existing("hbm:heater_firebox","hbm:heater_oven");if(!ovens.isEmpty()){list.add(new View(ovens,existing("minecraft:coal","hbm:lignite","hbm:coke_coal"),AshpitBlockEntity.ashStack(1)));list.add(new View(ovens,existing("minecraft:oak_log","minecraft:acacia_log","minecraft:oak_planks","minecraft:oak_sapling","minecraft:charcoal"),AshpitBlockEntity.ashStack(0)));list.add(new View(ovens,existing("hbm:solid_fuel","hbm:scrap","hbm:dust","hbm:rocket_fuel"),AshpitBlockEntity.ashStack(2)));}var chimneys=existing("hbm:chimney_brick","hbm:chimney_industrial");var industrial=existing("hbm:chimney_industrial");for(var smoke:List.of(HbmFluids.SMOKE,HbmFluids.SMOKE_LEADED,HbmFluids.SMOKE_POISON)){if(!chimneys.isEmpty())list.add(new View(chimneys,List.of(FluidIconItem.make(smoke,2000)),AshpitBlockEntity.ashStack(3)));if(!industrial.isEmpty())list.add(new View(industrial,List.of(FluidIconItem.make(smoke,8000)),AshpitBlockEntity.ashStack(4)));}return list;}
 static void draw(GuiGraphics g,int x,int y){var texture=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);for(int dx:new int[]{47,29,101})g.blit(texture,x+dx,y+23,5,87,18,18,256,256);g.blit(texture,x+74,y+14,59,87,18,36,256,256);}
}
