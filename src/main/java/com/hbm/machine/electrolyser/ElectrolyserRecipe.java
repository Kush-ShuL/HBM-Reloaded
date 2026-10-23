package com.hbm.machine.electrolyser;
import com.hbm.material.*;
import com.hbm.recipe.generic.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Both actual source electrolysis tables; molten quanta never become standard fluids. */
public record ElectrolyserRecipe(GenericRecipeData data,MaterialStack left,MaterialStack right,boolean metal) implements Recipe<RecipeInput>,GenericMachineRecipe {
    private static final Codec<MaterialStack> MAT=RecordCodecBuilder.create(i->i.group(Codec.INT.fieldOf("material").forGetter((MaterialStack m)->m.material.id()),Codec.INT.fieldOf("amount").forGetter(m->m.amount)).apply(i,(id,n)->new MaterialStack(Objects.requireNonNull(HbmMaterials.byId(id)),n)));
    public static final MapCodec<ElectrolyserRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(GenericRecipeData.MAP_CODEC.codec().fieldOf("data").forGetter(ElectrolyserRecipe::data),MAT.optionalFieldOf("left").forGetter(r->Optional.ofNullable(r.left)),MAT.optionalFieldOf("right").forGetter(r->Optional.ofNullable(r.right)),Codec.BOOL.fieldOf("metal").forGetter(ElectrolyserRecipe::metal)).apply(i,(d,l,r,m)->new ElectrolyserRecipe(d,l.orElse(null),r.orElse(null),m)));
    public ElectrolyserRecipe{left=left==null?null:left.copy();right=right==null?null:right.copy();}
    @Override public boolean matches(RecipeInput input,Level level){return metal&&input.size()>0&&com.hbm.ore.OreRecipeInputs.test(inputItems().getFirst().ingredient(),input.getItem(0));}
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup){return getResultItem(lookup).copy();}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return outputItems().isEmpty()?ItemStack.EMPTY:outputItems().getFirst().single();}
    @Override public NonNullList<Ingredient> getIngredients(){var out=NonNullList.<Ingredient>create();inputItems().forEach(v->out.add(v.ingredient()));return out;}
    @Override public boolean canCraftInDimensions(int width,int height){return false;}@Override public boolean isSpecial(){return true;}
    @Override public RecipeType<?> getType(){return ElectrolyserContent.TYPE.get();}@Override public RecipeSerializer<?> getSerializer(){return ElectrolyserContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<ElectrolyserRecipe>{
        @Override public MapCodec<ElectrolyserRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,ElectrolyserRecipe> streamCodec(){return StreamCodec.of((b,r)->{GenericRecipeData.STREAM_CODEC.encode(b,r.data);write(b,r.left);write(b,r.right);b.writeBoolean(r.metal);},b->new ElectrolyserRecipe(GenericRecipeData.STREAM_CODEC.decode(b),read(b),read(b),b.readBoolean()));}
        private static void write(RegistryFriendlyByteBuf b,MaterialStack m){b.writeBoolean(m!=null);if(m!=null){b.writeInt(m.material.id());b.writeInt(m.amount);}}
        private static MaterialStack read(RegistryFriendlyByteBuf b){return b.readBoolean()?new MaterialStack(Objects.requireNonNull(HbmMaterials.byId(b.readInt())),b.readInt()):null;}
    }
}
