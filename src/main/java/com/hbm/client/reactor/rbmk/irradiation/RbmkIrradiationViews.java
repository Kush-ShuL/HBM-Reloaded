package com.hbm.client.reactor.rbmk.irradiation;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.machine.reactor.rbmk.irradiation.*;
import java.util.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
/** Source NEIUniversalHandler one-input/one-or-two-output grid, including actual fusionOnly catalyst override. */
final class RbmkIrradiationViews {
    static List<ItemStack> outputs(RbmkIrradiationRecipe recipe){var result=new ArrayList<ItemStack>();if(!recipe.solid().isEmpty())result.add(recipe.solid());recipe.gas().ifPresent(fluid->result.add(FluidIconItem.make(fluid)));return result;}
    static ItemStack fusion(){return BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse("hbm:fusion_breeder")).map(ItemStack::new).orElse(ItemStack.EMPTY);}
    static List<ItemStack> machines(RbmkIrradiationRecipe recipe){var result=new ArrayList<ItemStack>();if(!recipe.fusionOnly())result.add(new ItemStack(RbmkOutgasserContent.ITEM.get()));var fusion=fusion();if(!fusion.isEmpty())result.add(fusion);return result;}
    static void draw(RbmkIrradiationRecipe recipe,GuiGraphics g,int x,int y){var texture=ResourceLocation.parse("hbm:textures/gui/nei/gui_nei.png");com.hbm.compat.recipeviewer.client.LegacyDisplayDrawing.recipeBackground(g,texture,x,y);g.blit(texture,x+47,y+23,5,87,18,18,256,256);for(int i=0;i<outputs(recipe).size();i++)g.blit(texture,x+101+i*18,y+23,5,87,18,18,256,256);g.blit(texture,x+74,y+14,59,87,18,36,256,256);}
    private RbmkIrradiationViews(){}
}
