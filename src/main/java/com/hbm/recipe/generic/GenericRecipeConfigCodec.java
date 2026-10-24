package com.hbm.recipe.generic;
import com.google.gson.*;
import com.hbm.fluid.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.material.MaterialDistributionConfig;
import com.hbm.machine.alloy.AlloyStackIngredient;
import com.hbm.machine.centrifuge.CentrifugeNbtIngredient;
import com.hbm.recipe.SourceFluidContainerIngredient;
import com.hbm.registry.HbmItemTags;
import com.mojang.serialization.JsonOps;
import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
/** Native tags and component-aware inputs; invalid/unbound stacks are reported, never substituted. */
public final class GenericRecipeConfigCodec {
    private GenericRecipeConfigCodec(){}
    public static GenericRecipeData read(JsonObject row,HolderLookup.Provider lookup,Map<String,String> aliases){
        var inputs=new ArrayList<GenericIngredient>();if(row.has("inputItem"))for(var element:row.getAsJsonArray("inputItem"))inputs.add(readIngredient(element.getAsJsonArray(),aliases,lookup));
        var outputs=new ArrayList<GenericOutput>();if(row.has("outputItem"))for(var element:row.getAsJsonArray("outputItem"))outputs.add(readOutput(element.getAsJsonArray(),aliases));
        List<String> pools=row.has("blueprintpool")?Arrays.asList(row.get("blueprintpool").getAsString().split(":")):List.of();
        return new GenericRecipeData(row.get("name").getAsString(),row.has("duration")?row.get("duration").getAsInt():0,row.has("power")?row.get("power").getAsLong():0,inputs,readFluids(row,"inputFluid"),outputs,readFluids(row,"outputFluid"),pools,row.has("autoSwitchGroup")?row.get("autoSwitchGroup").getAsString():"",row.has("icon")?readStack(row.getAsJsonArray("icon"),aliases):ItemStack.EMPTY,row.has("named")&&row.get("named").getAsBoolean(),row.has("nameWrapper")?row.get("nameWrapper").getAsString():"",row.has("order")?row.get("order").getAsInt():0);
    }
    public static GenericIngredient readIngredient(JsonArray array,Map<String,String> aliases){return readIngredient(array,aliases,null);}
    public static GenericIngredient readIngredient(JsonArray array,Map<String,String> aliases,HolderLookup.Provider lookup){
        String kind=array.get(0).getAsString();
        if(kind.equals("fluid_container")){
            String fluid=array.get(1).getAsString();int amount=array.get(2).getAsInt(),count=array.size()>3?array.get(3).getAsInt():1;
            if(amount<=0)throw new IllegalArgumentException("Fluid container amount must be positive mB");
            if(HbmFluids.fromName(fluid)==HbmFluids.NONE&&!fluid.equals("NONE"))throw new IllegalArgumentException("Unregistered fluid "+fluid);
            return new GenericIngredient(new SourceFluidContainerIngredient(fluid,amount).toVanilla(),count);
        }
        int count=array.size()>2?array.get(2).getAsInt():1;
        if(kind.equals("tag"))return new GenericIngredient(HbmItemTags.ingredient(array.get(1).getAsString()),count);
        if(kind.equals("ingredient"))return new GenericIngredient(Ingredient.CODEC_NONEMPTY.parse(lookup==null?JsonOps.INSTANCE:lookup.createSerializationContext(JsonOps.INSTANCE),array.get(1)).getOrThrow(),count);
        if(!kind.equals("item")&&!kind.equals("nbt"))throw new IllegalArgumentException("Unknown input kind "+kind+"; use tag, item, nbt, fluid_container or ingredient");
        int metadata=kind.equals("nbt")?0:array.size()>3?array.get(3).getAsInt():0;var stack=legacyStack(array.get(1).getAsString(),metadata,aliases);
        Ingredient ingredient;
        if(kind.equals("nbt")){CompoundTag tag=new CompoundTag();try{if(array.size()>4)tag=TagParser.parseTag(array.get(4).getAsString());}catch(Exception error){throw new IllegalArgumentException("Invalid NBT input "+array,error);}ingredient=new CentrifugeNbtIngredient(stack,tag).toVanilla();}
        else ingredient=new AlloyStackIngredient(stack).toVanilla();
        return new GenericIngredient(ingredient,count);
    }
    public static ItemStack readStack(JsonArray array,Map<String,String> aliases){ItemStack stack=legacyStack(array.get(0).getAsString(),array.size()>2?array.get(2).getAsInt():0,aliases);stack.setCount(array.size()>1?array.get(1).getAsInt():1);if(array.size()>3)try{stack.set(DataComponents.CUSTOM_DATA,CustomData.of(TagParser.parseTag(array.get(3).getAsString())));}catch(Exception error){throw new IllegalArgumentException("Invalid output source NBT "+array,error);}return stack;}
    public static ItemStack legacyStack(String name,int metadata,Map<String,String> aliases){var stack=MaterialDistributionConfig.legacyStack(aliases.getOrDefault(name,name),metadata);if(stack.isEmpty())throw new IllegalArgumentException("Unregistered source item "+name+"@"+metadata);if(com.hbm.item.fluid.FluidItemContent.subtypedItems().contains(stack.getItem()))com.hbm.item.fluid.FluidItemContent.setType(stack,HbmFluids.fromID(metadata));if(metadata==0&&!stack.getItem().components().has(HbmHazards.LEGACY_METADATA.get()))stack.remove(HbmHazards.LEGACY_METADATA.get());return stack;}
    private static List<FluidStack> readFluids(JsonObject row,String key){var fluids=new ArrayList<FluidStack>();if(row.has(key))for(var value:row.getAsJsonArray(key)){var array=value.getAsJsonArray();var type=HbmFluids.fromName(array.get(0).getAsString());if(type==HbmFluids.NONE&&!array.get(0).getAsString().equals("NONE"))throw new IllegalArgumentException("Unregistered source fluid "+array.get(0));fluids.add(new FluidStack(type,array.get(1).getAsInt(),array.size()>2?array.get(2).getAsInt():0));}return fluids;}
    private static GenericOutput readOutput(JsonArray array,Map<String,String> aliases){String kind=array.get(0).getAsString();if(kind.equals("single"))return new GenericOutput(List.of(new GenericOutput.Chance(readStack(array.get(1).getAsJsonArray(),aliases),array.size()>2?array.get(2).getAsFloat():1F,0)),false);if(!kind.equals("multi"))throw new IllegalArgumentException("Unknown original IOutput "+kind);var pool=new ArrayList<GenericOutput.Chance>();for(int i=1;i<array.size();i++){var entry=array.get(i).getAsJsonArray();pool.add(new GenericOutput.Chance(readStack(entry.get(0).getAsJsonArray(),aliases),entry.size()>1?entry.get(1).getAsFloat():1F,entry.size()>2?entry.get(2).getAsInt():0));}return new GenericOutput(pool,true);}
    public static JsonObject write(GenericRecipeData recipe){return write(recipe,null);}
    public static JsonObject write(GenericRecipeData recipe,HolderLookup.Provider lookup){var row=new JsonObject();row.addProperty("name",recipe.name());row.addProperty("duration",recipe.duration());row.addProperty("power",recipe.power());row.addProperty("order",recipe.order());var inputs=new JsonArray();for(var input:recipe.inputItems())inputs.add(writeIngredient(input.ingredient(),input.count(),lookup));if(!inputs.isEmpty())row.add("inputItem",inputs);var outputs=new JsonArray();for(var output:recipe.outputItems()){var array=new JsonArray();array.add(output.weighted()?"multi":"single");for(var c:output.pool()){if(output.weighted()){var choice=new JsonArray();choice.add(writeStack(c.stack()));choice.add(c.chance());choice.add(c.weight());array.add(choice);}else{array.add(writeStack(c.stack()));if(c.chance()<1)array.add(c.chance());}}outputs.add(array);}if(!outputs.isEmpty())row.add("outputItem",outputs);writeFluids(row,"inputFluid",recipe.inputFluids());writeFluids(row,"outputFluid",recipe.outputFluids());if(recipe.named())row.addProperty("named",true);if(!recipe.icon().isEmpty())row.add("icon",writeStack(recipe.icon()));if(!recipe.pools().isEmpty())row.addProperty("blueprintpool",String.join(":",recipe.pools()));if(!recipe.group().isEmpty())row.addProperty("autoSwitchGroup",recipe.group());if(!recipe.nameWrapper().isEmpty())row.addProperty("nameWrapper",recipe.nameWrapper());return row;}
    public static JsonArray writeIngredient(GenericIngredient input){return writeIngredient(input.ingredient(),input.count(),null);}
    public static JsonArray writeIngredient(Ingredient ingredient,int count){return writeIngredient(ingredient,count,null);}
    public static JsonArray writeIngredient(Ingredient ingredient,int count,HolderLookup.Provider lookup){
        var array=new JsonArray();var tag=HbmItemTags.tag(ingredient);
        if(tag.isPresent()){array.add("tag");array.add(tag.get().location().toString());array.add(count);return array;}
        var custom=ingredient.getCustomIngredient();
        if(custom instanceof SourceFluidContainerIngredient fluid){array.add("fluid_container");array.add(fluid.fluid());array.add(fluid.amount());array.add(count);return array;}
        ItemStack stack=null;CompoundTag required=null;
        if(custom instanceof CentrifugeNbtIngredient nbt&&metadata(nbt.example())==0){stack=nbt.example();required=nbt.required();}
        else if(custom instanceof AlloyStackIngredient exact)stack=exact.stack();
        if(stack!=null){array.add(required==null?"item":"nbt");array.add(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());array.add(count);array.add(metadata(stack));if(required!=null)array.add(required.toString());return array;}
        // Keep native OR branches and component predicates intact, including unbound tags.
        array.add("ingredient");array.add(Ingredient.CODEC_NONEMPTY.encodeStart(lookup==null?JsonOps.INSTANCE:lookup.createSerializationContext(JsonOps.INSTANCE),ingredient).getOrThrow());array.add(count);return array;
    }
    private static int metadata(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue());}
    public static JsonArray writeStack(ItemStack stack){var array=new JsonArray();array.add(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString());array.add(stack.getCount());array.add(metadata(stack));if(stack.has(DataComponents.CUSTOM_DATA))array.add(stack.get(DataComponents.CUSTOM_DATA).copyTag().toString());return array;}
    private static void writeFluids(JsonObject row,String key,List<FluidStack> values){if(values.isEmpty())return;var array=new JsonArray();for(var fluid:values){var spec=new JsonArray();spec.add(fluid.type.getName());spec.add(fluid.fill);if(fluid.pressure!=0)spec.add(fluid.pressure);array.add(spec);}row.add(key,array);}
}
