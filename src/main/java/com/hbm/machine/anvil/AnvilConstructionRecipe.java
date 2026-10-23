package com.hbm.machine.anvil;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
/** Source construction/recycling recipes consume the player's main inventory atomically, not GUI smithing slots. */
public record AnvilConstructionRecipe(List<AnvilIngredient> inputs,List<Output> outputs,int lower,int upper,String overlay,int order,String sourceMode,List<AnvilIngredient> expensiveInputs) implements Recipe<RecipeInput> {
    public record Output(ItemStack stack,float chance) {
        public static final Codec<Output> CODEC=RecordCodecBuilder.create(instance->instance.group(ItemStack.CODEC.fieldOf("stack").forGetter(Output::stack),Codec.FLOAT.optionalFieldOf("chance",1F).forGetter(Output::chance)).apply(instance,Output::new));
    }
    public AnvilConstructionRecipe(List<AnvilIngredient> inputs,List<Output> outputs,int lower,int upper,String overlay,int order) { this(inputs,outputs,lower,upper,overlay,order,"always",List.of()); }
    public static final MapCodec<AnvilConstructionRecipe> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(AnvilIngredient.CODEC.listOf().fieldOf("inputs").forGetter(AnvilConstructionRecipe::inputs),Output.CODEC.listOf().fieldOf("outputs").forGetter(AnvilConstructionRecipe::outputs),Codec.INT.optionalFieldOf("tier_lower",0).forGetter(AnvilConstructionRecipe::lower),Codec.INT.optionalFieldOf("tier_upper",-1).forGetter(AnvilConstructionRecipe::upper),Codec.STRING.optionalFieldOf("overlay","NONE").forGetter(AnvilConstructionRecipe::overlay),Codec.INT.optionalFieldOf("order",0).forGetter(AnvilConstructionRecipe::order),Codec.STRING.optionalFieldOf("source_mode","always").forGetter(AnvilConstructionRecipe::sourceMode),AnvilIngredient.CODEC.listOf().optionalFieldOf("expensive_inputs",List.of()).forGetter(AnvilConstructionRecipe::expensiveInputs)).apply(instance,AnvilConstructionRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,AnvilConstructionRecipe> STREAM_CODEC=StreamCodec.of((buffer,recipe)->{ buffer.writeVarInt(recipe.inputs.size()); recipe.inputs.forEach(value->value.encode(buffer)); buffer.writeVarInt(recipe.outputs.size()); recipe.outputs.forEach(value->{ItemStack.STREAM_CODEC.encode(buffer,value.stack);buffer.writeFloat(value.chance);});buffer.writeInt(recipe.lower);buffer.writeInt(recipe.upper);buffer.writeUtf(recipe.overlay);buffer.writeInt(recipe.order); },buffer->{var inputs=new ArrayList<AnvilIngredient>();int count=buffer.readVarInt();for(int i=0;i<count;i++)inputs.add(AnvilIngredient.decode(buffer));var outputs=new ArrayList<Output>();count=buffer.readVarInt();for(int i=0;i<count;i++)outputs.add(new Output(ItemStack.STREAM_CODEC.decode(buffer),buffer.readFloat()));return new AnvilConstructionRecipe(inputs,outputs,buffer.readInt(),buffer.readInt(),buffer.readUtf(),buffer.readInt());});
    public boolean tierValid(int tier) { return tier>=lower&&(upper==-1||tier<=upper); }
    public ItemStack display() { if(overlay.equals("RECYCLING")) for(var input:inputs) if((input.ingredient().getCustomIngredient() instanceof com.hbm.machine.alloy.AlloyStackIngredient||input.ingredient().getCustomIngredient() instanceof com.hbm.machine.centrifuge.CentrifugeNbtIngredient)&&input.ingredient().getItems().length>0) return input.ingredient().getItems()[0].copyWithCount(Math.max(1,input.count())); return outputs.isEmpty()?ItemStack.EMPTY:outputs.getFirst().stack().copy(); }
    @Override public boolean matches(RecipeInput input,Level level) { return false; }
    @Override public ItemStack assemble(RecipeInput input,HolderLookup.Provider lookup) { return outputs.isEmpty()?ItemStack.EMPTY:outputs.getFirst().stack.copy(); }
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup) { return outputs.isEmpty()?ItemStack.EMPTY:outputs.getFirst().stack; }
    @Override public NonNullList<Ingredient> getIngredients() { var list=NonNullList.<Ingredient>create();inputs.forEach(value->list.add(value.ingredient()));return list; }
    @Override public boolean isSpecial() { return true; } @Override public boolean canCraftInDimensions(int x,int y) { return true; }
    @Override public RecipeType<?> getType() { return AnvilContent.CONSTRUCTION_TYPE.get(); } @Override public RecipeSerializer<?> getSerializer() { return AnvilContent.CONSTRUCTION_SERIALIZER.get(); }
    public static final class Serializer implements RecipeSerializer<AnvilConstructionRecipe> { @Override public MapCodec<AnvilConstructionRecipe> codec(){return CODEC;} @Override public StreamCodec<RegistryFriendlyByteBuf,AnvilConstructionRecipe> streamCodec(){return STREAM_CODEC;} }
}
