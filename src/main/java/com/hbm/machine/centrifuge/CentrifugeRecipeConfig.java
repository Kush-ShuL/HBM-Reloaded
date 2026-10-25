package com.hbm.machine.centrifuge;

import com.google.gson.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.material.MaterialDistributionConfig;
import com.hbm.recipe.generic.GenericRecipeConfigCodec;
import com.hbm.registry.HbmItemTags;
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
                    try { var row=value.getAsJsonObject(); var recipe=nativeRecipe(row,server.registryAccess()); additions.values().removeIf(holder->holder.value().exactInput()==recipe.exactInput()&&sameInput(holder.value().input(),recipe.input())); var id=ResourceLocation.fromNamespaceAndPath("hbm","centrifuge/config/"+index); additions.put(id,new RecipeHolder<>(id,recipe)); }
                    catch(Exception error) { UNRESOLVED.add("config row "+index+": "+error.getMessage()); } index++;
                }
            } else {
                addCertusDefault(merged,additions);
                var defaults=merged.values().stream().filter(holder->holder.value() instanceof CentrifugeRecipe).map(holder->new RecipeHolder<>(holder.id(),(CentrifugeRecipe)holder.value())).toList();
                writeTemplate(directory.resolve("_hbmCentrifuge.json"),defaults,server.registryAccess());
            }
            int index=0;
            for(var data:IMC) {
                try {
                    var outputs=new ArrayList<ItemStack>(); for(int i=1;i<=4;i++) { var stack=readImcStack(data.getCompound("output"+i),server.registryAccess()); if(stack.isEmpty()) throw new IllegalArgumentException("Output"+i+" is unregistered"); outputs.add(stack); }
                    ItemStack input=readImcStack(data.getCompound("input"),server.registryAccess());
                    if(input.isEmpty()&&data.getString("tag").isEmpty()) throw new IllegalArgumentException("Neither native input stack nor item tag is valid");
                    Ingredient ingredient=input.isEmpty()?HbmItemTags.ingredient(data.getString("tag")):exact(input,server.registryAccess());
                    var recipe=new CentrifugeRecipe(ingredient,outputs,List.of(),!input.isEmpty());
                    // Legacy putAll overrides the same AStack key; remove a prior identical native ingredient.
                    merged.values().removeIf(holder->holder.value() instanceof CentrifugeRecipe r&&sameInput(r.input(),ingredient));
                    additions.values().removeIf(holder->sameInput(holder.value().input(),ingredient));
                    var id=ResourceLocation.fromNamespaceAndPath("hbm","centrifuge/imc/"+index); additions.put(id,new RecipeHolder<>(id,recipe));
                } catch(Exception error) { UNRESOLVED.add("IMC row "+index+": "+error.getMessage()); } index++;
            }
            IMC.clear();
        } catch(Exception error) { LogUtils.getLogger().error("Cannot load original centrifuge recipe configuration",error); }
        additions.forEach(merged::put); if(!additions.isEmpty()||!previous.isEmpty()||Files.isRegularFile(active)) manager.replaceRecipes(merged.values()); INSTALLED.put(manager,additions);
        for(String error:UNRESOLVED) LogUtils.getLogger().warn("Unbound centrifuge recipe: {}",error);
    }
    /** Source296: use the first real registered crystal, twice in each of four independent lanes. */
    private static void addCertusDefault(Map<ResourceLocation,RecipeHolder<?>> merged,Map<ResourceLocation,RecipeHolder<CentrifugeRecipe>> additions) {
        ItemStack crystal=HbmItemTags.items("c:crystals/certus_quartz").filter(stack->!stack.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
        if(crystal.isEmpty())return;
        crystal=crystal.copyWithCount(2);
        var ingredient=HbmItemTags.ingredient("c:ores/certus_quartz");
        var id=ResourceLocation.fromNamespaceAndPath("hbm","centrifuge/source_296");
        if(merged.containsKey(id))return; // A supplied native datapack recipe retains its real identity.
        var recipe=new CentrifugeRecipe(ingredient,List.of(crystal.copy(),crystal.copy(),crystal.copy(),crystal.copy()),List.of(),false);
        var holder=new RecipeHolder<>(id,recipe);additions.put(id,holder);merged.put(id,holder);
    }
    private static CentrifugeRecipe nativeRecipe(JsonObject row,HolderLookup.Provider lookup)throws Exception {
        JsonArray in=row.getAsJsonArray("input"); String kind=in.get(0).getAsString(); int metadata=!kind.equals("fluid_container")&&in.size()>3?in.get(3).getAsInt():0;
        Ingredient ingredient;
        if(kind.equals("tag")) ingredient=HbmItemTags.ingredient(in.get(1).getAsString());
        else if(kind.equals("fluid_container")||kind.equals("ingredient")) ingredient=GenericRecipeConfigCodec.readIngredient(in,ALIASES,lookup).ingredient();
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
        return new CentrifugeRecipe(ingredient,output,List.of(),row.has("exact_input")?row.get("exact_input").getAsBoolean():kind.equals("item")&&metadata!=32767&&(in.size()<=2||in.get(2).getAsInt()==1));
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
    private static void writeTemplate(Path path,List<RecipeHolder<CentrifugeRecipe>> recipes,HolderLookup.Provider lookup)throws java.io.IOException {
        JsonObject root=new JsonObject();root.addProperty("comment","输出按槽位排列，机器处理前四项。输入支持tag、item、nbt、fluid_container与ingredient；替换文件名为hbmCentrifuge.json。");JsonArray rows=new JsonArray();
        for(var holder:recipes){var recipe=holder.value();rows.add(row(GenericRecipeConfigCodec.writeIngredient(recipe.input(),1,lookup),recipe));}
        root.add("recipes",rows);Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n");
    }
    private static boolean sameInput(Ingredient first,Ingredient second){var a=HbmItemTags.tag(first);var b=HbmItemTags.tag(second);return a.isPresent()||b.isPresent()?a.isPresent()&&b.isPresent()&&a.get().equals(b.get()):first.equals(second);}
    private static JsonObject row(JsonArray input,CentrifugeRecipe recipe) { JsonObject row=new JsonObject(); row.add("input",input); row.addProperty("exact_input",recipe.exactInput()); JsonArray output=new JsonArray(); recipe.outputs().forEach(stack->output.add(jsonStack(stack))); row.add("output",output); return row; }
}
