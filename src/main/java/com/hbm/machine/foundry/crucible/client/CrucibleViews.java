package com.hbm.machine.foundry.crucible.client;
import com.hbm.item.autogen.*;
import com.hbm.machine.foundry.crucible.*;
import com.hbm.material.*;
import com.hbm.registry.HbmOreDictionary;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Material results are queried from the same native registry as the actual crucible, including custom distributions. */
final class CrucibleViews {
 record View(boolean alloy,List<List<ItemStack>> inputs,List<ItemStack> outputs){}
 static List<View> smelting(Collection<ItemStack> visible){var candidates=new ArrayList<ItemStack>(visible);BuiltInRegistries.ITEM.forEach(item->candidates.add(new ItemStack(item)));candidates.addAll(AutogenContent.creativeStacks());HbmOreDictionary.allNames().forEach(name->HbmOreDictionary.items(HbmOreDictionary.registerName(name)).forEach(candidates::add));var seen=new HashSet<String>();var recipes=new ArrayList<View>();for(var input:candidates){if(input.isEmpty()||input.getItem() instanceof ScrapsItem)continue;String key=BuiltInRegistries.ITEM.getKey(input.getItem())+":"+HbmMaterials.metadata(input);if(!seen.add(key))continue;var materials=HbmMaterials.getSmeltingMaterialsFromItem(input);if(materials.isEmpty())continue;var stack=input.copyWithCount(1);recipes.add(new View(false,List.of(List.of(stack)),materials.stream().map(m->ScrapsItem.create(m,true)).toList()));}return recipes;}
 static List<View> alloying(){var level=Minecraft.getInstance().level;if(level==null)return List.of();return CrucibleRecipeConfig.recipes(level.getRecipeManager()).stream().map(recipe->new View(true,recipe.input().stream().map(m->List.of(ScrapsItem.create(m,true))).toList(),recipe.output().stream().map(m->ScrapsItem.create(m,true)).toList())).toList();}
 static ResourceLocation texture(boolean alloy){return ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/nei/gui_nei_crucible"+(alloy?"":"_smelting")+".png");}
 static void draw(GuiGraphics g,int x,int y,boolean alloy){g.blit(texture(alloy),x,y,5,11,166,65,256,256);}
 static int inputX(View view,int i){return view.alloy()?12+i%3*18:48;}static int inputY(View view,int i){return view.alloy()?6+i/3*18:24;}
}
