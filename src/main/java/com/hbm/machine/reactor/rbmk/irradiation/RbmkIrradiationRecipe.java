package com.hbm.machine.reactor.rbmk.irradiation;
import com.hbm.fluid.FluidStack;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Shared genuine OutgasserRecipes input/optional solid+gas/fusionOnly data, not a fictitious timed HE recipe. */
public record RbmkIrradiationRecipe(GenericIngredient input,ItemStack solid,Optional<FluidStack> gas,boolean fusionOnly,String keyKind,String sourceKey,int order)implements Recipe<RecipeInput> {
    public static final MapCodec<RbmkIrradiationRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericIngredient.CODEC.fieldOf("input").forGetter(RbmkIrradiationRecipe::input),ItemStack.OPTIONAL_CODEC.optionalFieldOf("solid_output",ItemStack.EMPTY).forGetter(RbmkIrradiationRecipe::solid),GenericRecipeData.FLUID_CODEC.optionalFieldOf("fluid_output").forGetter(RbmkIrradiationRecipe::gas),Codec.BOOL.optionalFieldOf("fusion_only",false).forGetter(RbmkIrradiationRecipe::fusionOnly),Codec.STRING.optionalFieldOf("key_kind","item").forGetter(RbmkIrradiationRecipe::keyKind),Codec.STRING.optionalFieldOf("source_key","").forGetter(RbmkIrradiationRecipe::sourceKey),Codec.INT.optionalFieldOf("order",0).forGetter(RbmkIrradiationRecipe::order)).apply(i,RbmkIrradiationRecipe::new));
    @Override public boolean matches(RecipeInput in,Level level){return input.count()==1&&!keyKind.equals("nbt")&&in.size()>0&&input.matches(in.getItem(0),true);}@Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return solid.copy();}@Override public ItemStack getResultItem(HolderLookup.Provider lookup){return solid;}@Override public boolean isSpecial(){return true;}@Override public boolean canCraftInDimensions(int width,int height){return true;}@Override public RecipeType<?> getType(){return RbmkOutgasserContent.RECIPE.get();}@Override public RecipeSerializer<?> getSerializer(){return RbmkOutgasserContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<RbmkIrradiationRecipe>{@Override public MapCodec<RbmkIrradiationRecipe> codec(){return CODEC;}@Override public StreamCodec<RegistryFriendlyByteBuf,RbmkIrradiationRecipe> streamCodec(){return StreamCodec.of((b,r)->{r.input.encode(b);ItemStack.OPTIONAL_STREAM_CODEC.encode(b,r.solid);b.writeBoolean(r.gas.isPresent());if(r.gas.isPresent()){var fluid=r.gas.get();b.writeUtf(fluid.type.getName());b.writeInt(fluid.fill);b.writeInt(fluid.pressure);}b.writeBoolean(r.fusionOnly);b.writeUtf(r.keyKind);b.writeUtf(r.sourceKey);b.writeInt(r.order);},b->{var input=GenericIngredient.decode(b);var solid=ItemStack.OPTIONAL_STREAM_CODEC.decode(b);var gas=b.readBoolean()?Optional.of(new FluidStack(com.hbm.fluid.HbmFluids.fromName(b.readUtf()),b.readInt(),b.readInt())):Optional.<FluidStack>empty();return new RbmkIrradiationRecipe(input,solid,gas,b.readBoolean(),b.readUtf(),b.readUtf(),b.readInt());});}}
}
