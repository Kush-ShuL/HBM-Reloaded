package com.hbm.machine.storage;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

/** Original ContainerUpgradeCraftingHandler: the first row-major storage container supplies its complete item payload. */
public final class StorageUpgradeRecipe extends ShapedRecipe {
    private final ShapedRecipe original;
    private StorageUpgradeRecipe(ShapedRecipe original){super(original.getGroup(),original.category(),original.pattern,original.getResultItem(null),original.showNotification());this.original=original;}
    @Override public ItemStack assemble(CraftingInput input,HolderLookup.Provider lookup){
        var result=super.assemble(input,lookup);
        for(int i=0;i<input.size();i++){
            var stack=input.getItem(i);boolean massStorage=stack.getItem() instanceof BlockItem&&BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals("hbm:mass_storage");
            if(stack.getItem() instanceof StorageCrateItem||massStorage){
                CrateStackData.copySourceData(result,stack);
                return result;
            }
        }
        throw new IllegalStateException("Source container-upgrade recipe matched without a real storage container");
    }
    @Override public RecipeSerializer<?> getSerializer(){return StorageCrateContent.UPGRADE.get();}
    public static final class Serializer implements RecipeSerializer<StorageUpgradeRecipe> {
        private static final MapCodec<StorageUpgradeRecipe> CODEC=ShapedRecipe.Serializer.CODEC.xmap(StorageUpgradeRecipe::new,recipe->recipe.original);
        private static final StreamCodec<RegistryFriendlyByteBuf,StorageUpgradeRecipe> STREAM=ShapedRecipe.Serializer.STREAM_CODEC.map(StorageUpgradeRecipe::new,recipe->recipe.original);
        @Override public MapCodec<StorageUpgradeRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,StorageUpgradeRecipe> streamCodec(){return STREAM;}
    }
}
