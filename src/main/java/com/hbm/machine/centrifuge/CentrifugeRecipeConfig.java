package com.hbm.machine.centrifuge;

import com.google.gson.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.material.MaterialDistributionConfig;
import com.hbm.recipe.LegacyOreIngredient;
import com.hbm.registry.HbmOreDictionary;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** Original recipe-file replacement + centrifuge IMC, installed before vanilla recipe synchronization. */
public final class CentrifugeRecipeConfig {
    private static final List<CompoundTag> IMC=new ArrayList<>();
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<CentrifugeRecipe>>> INSTALLED=new WeakHashMap<>();
    private static final Map<String,String> ALIASES=new LinkedHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    private CentrifugeRecipeConfig() {}
    public static List<String> unresolved() { return List.copyOf(UNRESOLVED); }
    public static void register() {
        HbmOreDictionary.registerName("crystalCertusQuartz");
        HbmOreDictionary.registerName("oreCertusQuartz");
        try(var input=CentrifugeRecipeConfig.class.getResourceAsStream("/hbm/centrifuge-item-aliases.json")) {
            if(input!=null) { var values=JsonParser.parseReader(new java.io.InputStreamReader(input,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject(); values.entrySet().forEach(entry->ALIASES.put(entry.getKey(),entry.getValue().getAsString())); }
        } catch(java.io.IOException error) { throw new IllegalStateException(error); }
        NeoForge.EVENT_BUS.addListener(CentrifugeRecipeConfig::starting);
        NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,CentrifugeRecipeConfig::sync);
    }
    static void imc(InterModProcessEvent event) { event.enqueueWork(()->InterModComms.getMessages("hbm","centrifuge"::equals).forEach(message->{ Object data=message.messageSupplier().get(); if(data instanceof CompoundTag tag) IMC.add(tag.copy()); else LogUtils.getLogger().error("Centrifuge IMC from {} requires original compound payload",message.senderModId()); })); }
    private static void starting(ServerAboutToStartEvent event) { install(event.getServer()); }
    private static void sync(OnDatapackSyncEvent event) { if(event.getPlayer()==null||!INSTALLED.containsKey(event.getPlayerList().getServer().getRecipeManager())) install(event.getPlayerList().getServer()); }
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server) {
        var manager=server.getRecipeManager(); var previous=INSTALLED.getOrDefault(manager,Map.of());
        Map<ResourceLocation,RecipeHolder<?>> merged=new LinkedHashMap<>();
        for(var holder:manager.getOrderedRecipes()) if(previous.get(holder.id())!=holder) merged.put(holder.id(),holder);
        Map<ResourceLocation,RecipeHolder<CentrifugeRecipe>> additions=new LinkedHashMap<>(); UNRESOLVED.clear();
        Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmCentrifuge.json");
        try {
            Files.createDirectories(directory);
            if(Files.isRegularFile(active)) {
                // SerializableRecipe deletes defaults before loading the user's replacement file.
                merged.values().removeIf(holder->holder.value().getType()==CentrifugeContent.TYPE.get());
                JsonObject root; try(var reader=Files.newBufferedReader(active)) { root=JsonParser.parseReader(reader).getAsJsonObject(); }
                int index=0; for(var value:root.getAsJsonArray("recipes")) {
                    try { var row=value.getAsJsonObject(); var recipe=legacyRecipe(row,server.registryAccess()); additions.values().removeIf(holder->holder.value().exactInput()==recipe.exactInput()&&holder.value().input().equals(recipe.input())); var id=ResourceLocation.fromNamespaceAndPath("hbm","centrifuge/config/"+index); additions.put(id,new RecipeHolder<>(id,recipe)); }
                    catch(Exception error) { UNRESOLVED.add("config row "+index+": "+error.getMessage()); } index++;
                }
            } else {
                addCertusDefault(merged,additions);
                var defaults=merged.values().stream().filter(holder->holder.value() instanceof CentrifugeRecipe).map(holder->new RecipeHolder<>(holder.id(),(CentrifugeRecipe)holder.value())).toList();
                writeTemplate(directory.resolve("_hbmCentrifuge.json"),defaults);
            }
            int index=0;
            for(var data:IMC) {
                try {
                    var outputs=new ArrayList<ItemStack>(); for(int i=1;i<=4;i++) { var stack=readImcStack(data.getCompound("output"+i),server.registryAccess()); if(stack.isEmpty()) throw new IllegalArgumentException("Output"+i+" is unregistered"); outputs.add(stack); }
                    ItemStack input=readImcStack(data.getCompound("input"),server.registryAccess());
                    if(input.isEmpty()&&data.getString("oredict").isEmpty()) throw new IllegalArgumentException("Neither original input nor ore dictionary key is valid");
                    Ingredient ingredient=input.isEmpty()?new LegacyOreIngredient(ResourceLocation.parse(HbmOreDictionary.registerName(data.getString("oredict")))).toVanilla():exact(input,server.registryAccess());
                    var recipe=new CentrifugeRecipe(ingredient,outputs,List.of(),!input.isEmpty());
                    // Legacy putAll overrides the same AStack key; remove a prior identical native ingredient.
                    merged.values().removeIf(holder->holder.value() instanceof CentrifugeRecipe r&&r.input().equals(ingredient));
                    additions.values().removeIf(holder->holder.value().input().equals(ingredient));
                    var id=ResourceLocation.fromNamespaceAndPath("hbm","centrifuge/imc/"+index); additions.put(id,new RecipeHolder<>(id,recipe));
                } catch(Exception error) { UNRESOLVED.add("IMC row "+index+": "+error.getMessage()); } index++;
            }
        } catch(Exception error) { LogUtils.getLogger().error("Cannot load original centrifuge recipe configuration",error); }
        additions.forEach(merged::put); if(!additions.isEmpty()||!previous.isEmpty()||Files.isRegularFile(active)) manager.replaceRecipes(merged.values()); INSTALLED.put(manager,additions);
        for(String error:UNRESOLVED) LogUtils.getLogger().warn("Unbound centrifuge recipe: {}",error);
    }
    /** Source296: use the first real registered crystal, twice in each of four independent lanes. */
    private static void addCertusDefault(Map<ResourceLocation,RecipeHolder<?>> merged,Map<ResourceLocation,RecipeHolder<CentrifugeRecipe>> additions) {
        ItemStack crystal=HbmOreDictionary.items("hbm:legacy_ore_dict/crystalcertusquartz").filter(stack->!stack.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
        if(crystal.isEmpty())return;
        crystal=crystal.copyWithCount(2);
        var ingredient=new LegacyOreIngredient(ResourceLocation.parse("hbm:legacy_ore_dict/orecertusquartz")).toVanilla();
        var id=ResourceLocation.fromNamespaceAndPath("hbm","centrifuge/source_296");
        if(merged.containsKey(id))return; // A supplied native datapack recipe retains its real identity.
        var recipe=new CentrifugeRecipe(ingredient,List.of(crystal.copy(),crystal.copy(),crystal.copy(),crystal.copy()),List.of(),false);
        var holder=new RecipeHolder<>(id,recipe);additions.put(id,holder);merged.put(id,holder);
    }
    private static CentrifugeRecipe legacyRecipe(JsonObject row,HolderLookup.Provider lookup)throws Exception {
        JsonArray in=row.getAsJsonArray("input"); String kind=in.get(0).getAsString(); int metadata=in.size()>3?in.get(3).getAsInt():0;
        Ingredient ingredient;
        if(kind.equals("dict")) ingredient=new LegacyOreIngredient(ResourceLocation.parse(HbmOreDictionary.registerName(in.get(1).getAsString()))).toVanilla();
        else if(kind.equals("item")) { ItemStack stack=legacyStack(in.get(1).getAsString(),metadata); if(stack.isEmpty()) throw new IllegalArgumentException("Unregistered input "+in.get(1)); ingredient=metadata==32767?Ingredient.of(stack.getItem()):exact(stack,lookup); }
        else if(kind.equals("nbt")) {
            // The original readAStack(nbt) constructor fixes metadata to0, even if a metadata field exists.
            ItemStack stack=legacyStack(in.get(1).getAsString(),0); if(stack.isEmpty()) throw new IllegalArgumentException("Unregistered NBT input "+in.get(1));
            ingredient=new CentrifugeNbtIngredient(stack,in.size()>4?TagParser.parseTag(in.get(4).getAsString()):new CompoundTag()).toVanilla();
        } else throw new IllegalArgumentException("Unknown source AStack kind "+kind);
        var output=new ArrayList<ItemStack>(); for(var value:row.getAsJsonArray("output")) {
            var spec=value.getAsJsonArray(); ItemStack stack=legacyStack(spec.get(0).getAsString(),spec.size()>2?spec.get(2).getAsInt():0);
            if(stack.isEmpty()) throw new IllegalArgumentException("Unregistered output "+spec.get(0)); stack.setCount(spec.size()>1?spec.get(1).getAsInt():1);
            if(spec.size()>3) stack.set(DataComponents.CUSTOM_DATA,CustomData.of(TagParser.parseTag(spec.get(3).getAsString()))); output.add(stack);
        }
        return new CentrifugeRecipe(ingredient,output,List.of(),kind.equals("item")&&metadata!=32767&&(in.size()<=2||in.get(2).getAsInt()==1));
    }
    private static ItemStack legacyStack(String old,int metadata) {
        ItemStack stack=MaterialDistributionConfig.legacyStack(ALIASES.getOrDefault(old,old),metadata);
        if(stack.isEmpty()) return stack;
        if(com.hbm.item.fluid.FluidItemContent.subtypedItems().contains(stack.getItem())) com.hbm.item.fluid.FluidItemContent.setType(stack,com.hbm.fluid.HbmFluids.fromID(metadata));
        if(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())==0&&!stack.getItem().components().has(HbmHazards.LEGACY_METADATA.get())) stack.remove(HbmHazards.LEGACY_METADATA.get());
        return stack;
    }
    private static Ingredient exact(ItemStack stack,HolderLookup.Provider lookup) {
        return new CentrifugeNbtIngredient(stack,new CompoundTag()).toVanilla();
    }
    private static ItemStack readImcStack(CompoundTag tag,HolderLookup.Provider lookup) {
        if(tag.isEmpty()) return ItemStack.EMPTY;
        ItemStack stack=ItemStack.parseOptional(lookup,tag); if(!stack.isEmpty()) return stack;
        stack=legacyStack(tag.getString("id"),tag.getShort("Damage")); if(!stack.isEmpty()) { stack.setCount(tag.contains("Count")?tag.getByte("Count"):1); if(tag.contains("tag")) stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag.getCompound("tag").copy())); } return stack;
    }
    private static JsonArray jsonStack(ItemStack stack) {
        JsonArray value=new JsonArray(); value.add(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()); value.add(stack.getCount()); value.add(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())); if(stack.has(DataComponents.CUSTOM_DATA)) value.add(stack.get(DataComponents.CUSTOM_DATA).copyTag().toString()); return value;
    }
    private static void writeTemplate(Path path,List<RecipeHolder<CentrifugeRecipe>> recipes)throws java.io.IOException {
        JsonObject root=new JsonObject(); root.addProperty("comment","Outputs are positional; the machine processes the first four. This original array grammar replaces defaults when renamed to hbmCentrifuge.json."); JsonArray rows=new JsonArray();
        for(var holder:recipes) {
            var recipe=holder.value(); var custom=recipe.input().getCustomIngredient();
            if(custom instanceof LegacyOreIngredient ore) {
                String raw=HbmOreDictionary.allNames().stream().filter(name->("hbm:legacy_ore_dict/"+name.toLowerCase(Locale.ROOT)).equals(ore.tag().toString())).findFirst().orElse(ore.tag().getPath().substring("legacy_ore_dict/".length()));
                JsonArray input=new JsonArray(); input.add("dict"); input.add(raw); rows.add(row(input,recipe));
            } else for(var stack:recipe.input().getItems()) { JsonArray input=new JsonArray(); input.add("item"); input.add(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()); input.add(1); input.add(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())); rows.add(row(input,recipe)); }
        }
        root.add("recipes",rows); Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");
    }
    private static JsonObject row(JsonArray input,CentrifugeRecipe recipe) { JsonObject row=new JsonObject(); row.add("input",input); JsonArray output=new JsonArray(); recipe.outputs().forEach(stack->output.add(jsonStack(stack))); row.add("output",output); return row; }
}
