package com.hbm.world.structure.templates.microwave.client;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
/** Viewer entries are actual first-match smelting recipes satisfying the same source food gate as the machine. */
final class MicrowaveRecipes {
    record View(ResourceLocation recipe,List<ItemStack> inputs,ItemStack output){}
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/gui/processing/gui_microwave.png");
    static List<View> recipes(){var level=Minecraft.getInstance().level;if(level==null)return List.of();var result=new ArrayList<View>();for(var holder:level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)){var groups=new ArrayList<View>();for(var input:holder.value().getIngredients().getFirst().getItems()){if(input.isEmpty())continue;var single=new SingleRecipeInput(input);var actual=level.getRecipeManager().getRecipeFor(RecipeType.SMELTING,single,level);if(actual.isEmpty()||!actual.get().id().equals(holder.id()))continue;var output=actual.get().value().assemble(single,level.registryAccess());if(output.isEmpty()||!input.has(DataComponents.FOOD)&&!output.has(DataComponents.FOOD))continue;View group=null;for(var candidate:groups)if(candidate.output().getCount()==output.getCount()&&ItemStack.isSameItemSameComponents(candidate.output(),output)){group=candidate;break;}if(group==null){group=new View(holder.id(),new ArrayList<>(),output.copy());groups.add(group);}group.inputs().add(input.copy());}result.addAll(groups);}return List.copyOf(result);}
    static void draw(GuiGraphics graphics,int x,int y){graphics.blit(TEXTURE,x,y,72F,26F,88,34,256,256);graphics.blit(TEXTURE,x+32,y+8,192F,0F,22,16,256,256);}
    private MicrowaveRecipes(){}
}
