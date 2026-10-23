package com.hbm.machine.combinationfurnace;
import com.hbm.fluid.FluidStack;
import com.hbm.ore.RawOreContent;
import com.hbm.recipe.LegacyOreIngredient;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Source single-item key -> optional solid and/or private fluid output, using the sole native generic payload. */
public record CombinationFurnaceRecipe(GenericRecipeData data) implements Recipe<RecipeInput>,GenericMachineRecipe {
    public GenericIngredient input(){return inputItems().getFirst();}
    public ItemStack solid(){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().pool().getFirst().stack().copy();}
    public FluidStack fluid(){return outputFluids().isEmpty()?null:outputFluids().getFirst();}
    public boolean dictionary(){return input().ingredient().getCustomIngredient() instanceof LegacyOreIngredient;}
    public boolean matchesItem(ItemStack stack){ItemStack canonical=RawOreContent.processingStack(stack);if(dictionary())return input().matches(canonical,true);for(ItemStack key:input().ingredient().getItems())if(com.hbm.machine.electricfurnace.ElectricFurnaceBlockEntity.sameLegacyItem(key,canonical))return true;return false;}
    public static final MapCodec<CombinationFurnaceRecipe> CODEC=GenericRecipeData.MAP_CODEC.xmap(CombinationFurnaceRecipe::new,CombinationFurnaceRecipe::data);
    @Override public boolean matches(RecipeInput input,Level level){return input.size()>0&&matchesItem(input.getItem(0));}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return solid();}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return solid();}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}
    @Override public boolean isSpecial(){return true;}
    @Override public NonNullList<Ingredient> getIngredients(){return NonNullList.of(Ingredient.EMPTY,input().ingredient());}
    @Override public RecipeType<?> getType(){return CombinationFurnaceContent.TYPE.get();}
    @Override public RecipeSerializer<?> getSerializer(){return CombinationFurnaceContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<CombinationFurnaceRecipe>{
        @Override public MapCodec<CombinationFurnaceRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,CombinationFurnaceRecipe> streamCodec(){return GenericRecipeData.STREAM_CODEC.map(CombinationFurnaceRecipe::new,CombinationFurnaceRecipe::data);}
    }
}
