package com.hbm.client.alloy;
import com.hbm.machine.alloy.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
final class AlloyViews {
    static final ResourceLocation TEXTURE=ResourceLocation.fromNamespaceAndPath("hbm","textures/gui/guidifurnace.png");
    static List<AlloyRecipe> recipes() {
        var level=Minecraft.getInstance().level; if(level==null) return List.of();
        var all=level.getRecipeManager().getAllRecipesFor(AlloyContent.TYPE.get()).stream().map(holder->holder.value()).toList();
        // The source hides by ComparableStack output, so every same-item/metadata recipe is hidden together.
        var hidden=all.stream().filter(AlloyRecipe::hidden).map(AlloyRecipe::result).toList();
        return all.stream().filter(recipe->hidden.stream().noneMatch(stack->AlloyBlockEntity.sameLegacy(stack,recipe.result()))).toList();
    }
    static boolean visible(AlloyRecipe recipe) { return recipes().contains(recipe); }
    static List<ItemStack> fuels() { return AlloyBlockEntity.fuels().stream().filter(stack->stack.getItem()!=Items.CHARCOAL&&!net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().equals("block_coke")).toList(); }
    static void draw(GuiGraphics graphics,int x,int y) {
        graphics.blit(TEXTURE,x,y,5,11,166,65,256,256);
        // X5846 NEI cycle duration48 and480 ticks, at the original pixel coordinates.
        long ticks=System.currentTimeMillis()/50L; int progress=(int)(ticks%48)*24/48,fire=14-(int)(ticks%48)*14/48,gauge=52-(int)(ticks%480)*52/480;
        if(fire>0) graphics.blit(TEXTURE,x+57,y+26+14-fire,176,14-fire,14,fire,256,256);
        if(progress>0) graphics.blit(TEXTURE,x+96,y+24,176,14,progress,16,256,256);
        if(gauge>0) graphics.blit(TEXTURE,x+39,y+7+52-gauge,201,52-gauge,16,gauge,256,256);
    }
    private AlloyViews() {}
}
