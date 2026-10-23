package com.hbm.world.structure.loot;
import com.mojang.serialization.*;
import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** MKUCraftingHandler's seed-shuffled six exact ingredients and three absolute empty slots. */
public final class MkuRecipe extends CustomRecipe {
    public final long seed;private final List<ItemStack> grid;
    public MkuRecipe(long seed){super(CraftingBookCategory.MISC);this.seed=seed;grid=MkuStructureLoot.recipe(seed);}
    @Override public boolean matches(CraftingInput input,Level level){List<ItemStack> actual;if(input instanceof LegacyCraftingOrigin origin&&origin.hbm$originalWidth()>0){if(origin.hbm$originalWidth()!=3||origin.hbm$originalHeight()!=3)return false;actual=origin.hbm$originalItems();}else{if(input.width()!=3||input.height()!=3)return false;actual=input.items();}for(int i=0;i<9;i++){var a=actual.get(i);var expected=grid.get(i);if(a.isEmpty()&&expected.isEmpty())continue;if(a.isEmpty()||expected.isEmpty()||!a.is(expected.getItem())||a.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),a.getDamageValue()).intValue()!=expected.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),expected.getDamageValue()).intValue())return false;}return true;}
    @Override public ItemStack assemble(CraftingInput input,HolderLookup.Provider lookup){return getResultItem(lookup);}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return new ItemStack(com.hbm.item.medicine.MedicineContent.ALL.get("syringe_mkunicorn").get());}
    @Override public boolean canCraftInDimensions(int width,int height){return width==3&&height==3;}
    @Override public RecipeSerializer<?> getSerializer(){return StructureLootContent.MKU_RECIPE.get();}
    public static final class Serializer implements RecipeSerializer<MkuRecipe>{
        private static final MapCodec<MkuRecipe> CODEC=Codec.LONG.fieldOf("seed").xmap(MkuRecipe::new,value->value.seed);
        private static final StreamCodec<RegistryFriendlyByteBuf,MkuRecipe> STREAM=StreamCodec.composite(ByteBufCodecs.VAR_LONG,value->value.seed,MkuRecipe::new);
        @Override public MapCodec<MkuRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,MkuRecipe> streamCodec(){return STREAM;}
    }
}
