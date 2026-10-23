package com.hbm.railway.cart;
import com.hbm.machine.storage.CrateStackData;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
/** Native shaped matching with the actual source first-match base and optional crate last-tag-copy rules. */
public final class SourceCartShapedRecipe extends ShapedRecipe {
    private final ShapedRecipe original;
    private SourceCartShapedRecipe(ShapedRecipe original){super(original.getGroup(),original.category(),original.pattern,original.getResultItem(null),original.showNotification());this.original=original;}
    @Override public ItemStack assemble(CraftingInput input,HolderLookup.Provider lookup){
        var result=super.assemble(input,lookup);var kind=SourceCartItem.kind(result);
        // Original input matching ignores cartBase NBT. Preserve original insertion-list first hit, independent of modern recipe-map order.
        if(kind==SourceCartKind.POWDER||kind==SourceCartKind.SEMTEX||kind==SourceCartKind.DESTROYER){for(var base:SourceCartKind.Base.values())if(kind.supports(base))return SourceCartItem.stack(base,kind);}
        if(kind==SourceCartKind.CRATE)for(int i=0;i<input.size();i++){var stack=input.getItem(i);if(!stack.isEmpty()&&CrateStackData.hasSourceData(stack))CrateStackData.copySourceData(result,stack);}
        return result;
    }
    @Override public RecipeSerializer<?> getSerializer(){return SourceCartContent.COPY_RECIPE.get();}
    public static final class Serializer implements RecipeSerializer<SourceCartShapedRecipe> {
        private static final MapCodec<SourceCartShapedRecipe> CODEC=ShapedRecipe.Serializer.CODEC.xmap(SourceCartShapedRecipe::new,recipe->recipe.original);
        private static final StreamCodec<RegistryFriendlyByteBuf,SourceCartShapedRecipe> STREAM=ShapedRecipe.Serializer.STREAM_CODEC.map(SourceCartShapedRecipe::new,recipe->recipe.original);
        @Override public MapCodec<SourceCartShapedRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,SourceCartShapedRecipe> streamCodec(){return STREAM;}
    }
}
