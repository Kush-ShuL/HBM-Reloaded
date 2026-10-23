package com.hbm.machine.pedestal;

import com.hbm.recipe.generic.GenericIngredient;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** Exact source positional/null/stack-count matching; no translation or rotation of the ritual is permitted. */
public record PedestalRecipe(ItemStack result,List<GenericIngredient> inputs,Extra extra,int recipeSet,int order) implements Recipe<PedestalInput> {
    public enum Extra{NONE,FULL_MOON,NEW_MOON,SUN,GOOD_KARMA,BAD_KARMA}
    public PedestalRecipe{inputs=List.copyOf(inputs);if(inputs.size()!=9)throw new IllegalArgumentException("Pedestal requires exactly nine source positions");}
    public static final Codec<Extra> EXTRA=Codec.STRING.xmap(s->Extra.valueOf(s.toUpperCase(Locale.ROOT)),Extra::name);
    public static final MapCodec<PedestalRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(ItemStack.STRICT_CODEC.fieldOf("result").forGetter(PedestalRecipe::result),GenericIngredient.CODEC.listOf().fieldOf("inputs").forGetter(PedestalRecipe::inputs),EXTRA.optionalFieldOf("extra",Extra.NONE).forGetter(PedestalRecipe::extra),Codec.INT.optionalFieldOf("set",0).forGetter(PedestalRecipe::recipeSet),Codec.INT.optionalFieldOf("order",0).forGetter(PedestalRecipe::order)).apply(i,PedestalRecipe::new));
    @Override public boolean matches(PedestalInput input,Level level){
        if(input.size()!=9)return false;
        float angle=level.getTimeOfDay(0);
        if((extra==Extra.FULL_MOON||extra==Extra.NEW_MOON)&&(angle<.35F||angle>.65F||level.getMoonPhase()!=(extra==Extra.FULL_MOON?0:4)))return false;
        if(extra==Extra.SUN&&angle>.15F&&angle<.85F)return false;
        if(extra==Extra.GOOD_KARMA||extra==Extra.BAD_KARMA){boolean pass=false;for(var player:level.getEntitiesOfClass(Player.class,new AABB(input.center()).inflate(20))){int reputation=PedestalReputation.get(player);if(extra==Extra.GOOD_KARMA?reputation>=10:reputation<=-10){pass=true;break;}}if(!pass)return false;}
        for(int i=0;i<9;i++){var actual=input.getItem(i);var required=inputs.get(i);if(required.count()==0){if(!actual.isEmpty())return false;}else if(actual.getCount()!=required.count()||!required.matches(actual,true))return false;}
        return true;
    }
    @Override public ItemStack assemble(PedestalInput input,HolderLookup.Provider lookup){return result.copy();}
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup){return result;}
    @Override public NonNullList<Ingredient> getIngredients(){var values=NonNullList.<Ingredient>create();inputs.forEach(in->values.add(in.ingredient()));return values;}
    @Override public boolean canCraftInDimensions(int width,int height){return true;}
    @Override public boolean isSpecial(){return true;}
    @Override public ItemStack getToastSymbol(){return new ItemStack(PedestalContent.ITEM.get());}
    @Override public RecipeType<?> getType(){return PedestalContent.TYPE.get();}
    @Override public RecipeSerializer<?> getSerializer(){return PedestalContent.SERIALIZER.get();}
    public static final class Serializer implements RecipeSerializer<PedestalRecipe>{
        @Override public MapCodec<PedestalRecipe> codec(){return CODEC;}
        @Override public StreamCodec<RegistryFriendlyByteBuf,PedestalRecipe> streamCodec(){return StreamCodec.of((buffer,recipe)->{ItemStack.STREAM_CODEC.encode(buffer,recipe.result);for(var slot:recipe.inputs)slot.encode(buffer);buffer.writeEnum(recipe.extra);buffer.writeInt(recipe.recipeSet);buffer.writeInt(recipe.order);},buffer->{var output=ItemStack.STREAM_CODEC.decode(buffer);var slots=new ArrayList<GenericIngredient>();for(int i=0;i<9;i++)slots.add(GenericIngredient.decode(buffer));return new PedestalRecipe(output,slots,buffer.readEnum(Extra.class),buffer.readInt(),buffer.readInt());});}
    }
}
