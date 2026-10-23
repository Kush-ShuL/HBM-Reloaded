package com.hbm.machine.anvil;
import com.hbm.hazard.HbmHazards;
import com.hbm.material.HbmMaterials;
import com.hbm.registry.HbmOreDictionary;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
public record AnvilSmithingRecipe(AnvilIngredient left,AnvilIngredient right,ItemStack output,int tier,String kind,boolean shapeless,String prefix,List<ItemStack> exactLeft,int order) implements Recipe<AnvilInput> {
    public static final MapCodec<AnvilSmithingRecipe> CODEC=RecordCodecBuilder.mapCodec(instance->instance.group(AnvilIngredient.CODEC.fieldOf("left").forGetter(AnvilSmithingRecipe::left),AnvilIngredient.CODEC.fieldOf("right").forGetter(AnvilSmithingRecipe::right),ItemStack.CODEC.fieldOf("result").forGetter(AnvilSmithingRecipe::output),Codec.INT.fieldOf("tier").forGetter(AnvilSmithingRecipe::tier),Codec.STRING.optionalFieldOf("kind","normal").forGetter(AnvilSmithingRecipe::kind),Codec.BOOL.optionalFieldOf("shapeless",false).forGetter(AnvilSmithingRecipe::shapeless),Codec.STRING.optionalFieldOf("prefix","").forGetter(AnvilSmithingRecipe::prefix),ItemStack.CODEC.listOf().optionalFieldOf("exact_left",List.of()).forGetter(AnvilSmithingRecipe::exactLeft),Codec.INT.optionalFieldOf("order",0).forGetter(AnvilSmithingRecipe::order)).apply(instance,AnvilSmithingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf,AnvilSmithingRecipe> STREAM_CODEC=StreamCodec.of((buf,r)->{r.left.encode(buf);r.right.encode(buf);ItemStack.STREAM_CODEC.encode(buf,r.output);buf.writeInt(r.tier);buf.writeUtf(r.kind);buf.writeBoolean(r.shapeless);buf.writeUtf(r.prefix);buf.writeVarInt(r.exactLeft.size());r.exactLeft.forEach(stack->ItemStack.STREAM_CODEC.encode(buf,stack));buf.writeInt(r.order);},buf->{var left=AnvilIngredient.decode(buf);var right=AnvilIngredient.decode(buf);ItemStack out=ItemStack.STREAM_CODEC.decode(buf);int tier=buf.readInt();String kind=buf.readUtf();boolean shape=buf.readBoolean();String prefix=buf.readUtf();var exact=new ArrayList<ItemStack>();int count=buf.readVarInt();for(int i=0;i<count;i++)exact.add(ItemStack.STREAM_CODEC.decode(buf));return new AnvilSmithingRecipe(left,right,out,tier,kind,shape,prefix,exact,buf.readInt());});
    public int orientation(AnvilInput input) {
        ItemStack a=input.left(),b=input.right(); if(a.isEmpty()||b.isEmpty()) return -1;
        if(kind.equals("rename")) return right.matches(b,false)&&b.has(DataComponents.CUSTOM_NAME)?0:-1;
        if(kind.equals("cyanide")) return a.has(DataComponents.FOOD)&&(right.matches(b,false)||BuiltInRegistries.ITEM.getKey(b.getItem()).toString().equals("hbm:pill_red"))?0:-1;
        if(kind.equals("mold")) {
            if(!right.matches(b,false)) return -1;
            if(!prefix.isEmpty()&&a.getCount()==left.count()) for(String name:HbmOreDictionary.names(a)) {
                for(var shape:HbmMaterials.shapes()) for(String candidate:shape.prefixes()) if(candidate.length()>prefix.length()&&name.startsWith(candidate)) return -1;
                if(name.startsWith(prefix)) return 0;
            }
            for(ItemStack stack:exactLeft) if(a.getItem()==stack.getItem()&&metadata(a)==metadata(stack)&&a.getCount()==stack.getCount()) return 0; return -1;
        }
        if(test(left,a)&&test(right,b)) return 0; return shapeless&&test(left,b)&&test(right,a)?1:-1;
    }
    private boolean test(AnvilIngredient ingredient,ItemStack stack) { if(kind.equals("hot")&&stack.getItem() instanceof HotSmithingItem&&HotSmithingItem.heat(stack)<.5) return false; return ingredient.matches(stack,false); }
    public int consumed(int slot,boolean mirrored) { if(kind.equals("mold")) return slot; return slot==0?(mirrored?right.count():left.count()):(mirrored?left.count():right.count()); }
    private static int metadata(ItemStack stack) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue()); }
    @Override public boolean matches(AnvilInput input,Level level) { return orientation(input)>=0; }
    @Override public ItemStack assemble(AnvilInput input,HolderLookup.Provider lookup) {
        if(kind.equals("rename")) { ItemStack stack=input.left().copyWithCount(1);String name=input.right().get(DataComponents.CUSTOM_NAME).getString().replace("\\&","§");stack.set(DataComponents.CUSTOM_NAME,Component.literal("§r"+name));return stack; }
        if(kind.equals("cyanide")) { ItemStack stack=input.left().copyWithCount(1);CustomData.update(DataComponents.CUSTOM_DATA,stack,tag->tag.putBoolean(BuiltInRegistries.ITEM.getKey(input.right().getItem()).toString().equals("hbm:pill_red")?"ntmRedPill":"ntmCyanide",true));return stack; }
        ItemStack stack=output.copy(); if(kind.equals("hot")&&input.left().getItem() instanceof HotSmithingItem&&input.right().getItem() instanceof HotSmithingItem&&stack.getItem() instanceof HotSmithingItem) HotSmithingItem.heatUp(stack,(HotSmithingItem.heat(input.left())+HotSmithingItem.heat(input.right()))/2); return stack;
    }
    @Override public ItemStack getResultItem(HolderLookup.Provider lookup) { return output; }
    @Override public NonNullList<Ingredient> getIngredients() { return NonNullList.of(Ingredient.EMPTY,left.ingredient(),right.ingredient()); }
    @Override public boolean isSpecial() { return true; } @Override public boolean canCraftInDimensions(int x,int y) { return true; }
    @Override public RecipeType<?> getType() { return AnvilContent.SMITHING_TYPE.get(); } @Override public RecipeSerializer<?> getSerializer() { return AnvilContent.SMITHING_SERIALIZER.get(); }
    public static final class Serializer implements RecipeSerializer<AnvilSmithingRecipe> { @Override public MapCodec<AnvilSmithingRecipe> codec(){return CODEC;} @Override public StreamCodec<RegistryFriendlyByteBuf,AnvilSmithingRecipe> streamCodec(){return STREAM_CODEC;} }
}
