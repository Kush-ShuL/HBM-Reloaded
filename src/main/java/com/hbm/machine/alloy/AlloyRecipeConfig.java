package com.hbm.machine.alloy;
import com.google.gson.*;
import com.hbm.hazard.HbmHazards;
import com.hbm.material.MaterialDistributionConfig;
import com.hbm.recipe.LegacyOreIngredient;
import com.hbm.registry.HbmOreDictionary;
import com.mojang.logging.LogUtils;
import java.nio.file.*;
import java.util.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.*;
import net.neoforged.bus.api.*;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

/** Source IMC is appended to defaults first; the user's legacy recipe file replaces that entire list. */
public final class AlloyRecipeConfig {
    private static final List<CompoundTag> IMC=new ArrayList<>();
    private static final Map<RecipeManager,Map<ResourceLocation,RecipeHolder<AlloyRecipe>>> INSTALLED=new WeakHashMap<>();
    private static final Map<String,String> ALIASES=new LinkedHashMap<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    private AlloyRecipeConfig() {}
    public static List<String> unresolved() { return List.copyOf(UNRESOLVED); }
    public static void register(IEventBus bus) {
        try(var stream=AlloyRecipeConfig.class.getResourceAsStream("/hbm/alloy-item-aliases.json")) {
            if(stream!=null) JsonParser.parseReader(new java.io.InputStreamReader(stream,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry->ALIASES.put(entry.getKey(),entry.getValue().getAsString()));
        } catch(java.io.IOException error) { throw new IllegalStateException(error); }
        bus.addListener(AlloyRecipeConfig::imc);
        NeoForge.EVENT_BUS.addListener(AlloyRecipeConfig::starting); NeoForge.EVENT_BUS.addListener(EventPriority.LOWEST,AlloyRecipeConfig::sync);
    }
    private static void imc(InterModProcessEvent event) { event.enqueueWork(()->InterModComms.getMessages("hbm","blastfurnace"::equals).forEach(message->{ if(message.messageSupplier().get() instanceof CompoundTag tag) IMC.add(tag.copy()); else LogUtils.getLogger().error("Original blastfurnace IMC requires compound payload from {}",message.senderModId()); })); }
    private static void starting(ServerAboutToStartEvent event) { install(event.getServer()); }
    private static void sync(OnDatapackSyncEvent event) { if(event.getPlayer()==null||!INSTALLED.containsKey(event.getPlayerList().getServer().getRecipeManager())) install(event.getPlayerList().getServer()); }
    public static void reload(MinecraftServer server) { install(server); }
    private static void install(MinecraftServer server) {
        RecipeManager manager=server.getRecipeManager(); var previous=INSTALLED.getOrDefault(manager,Map.of()); var merged=new LinkedHashMap<ResourceLocation,RecipeHolder<?>>();
        for(var holder:manager.getOrderedRecipes()) if(previous.get(holder.id())!=holder) merged.put(holder.id(),holder);
        var additions=new LinkedHashMap<ResourceLocation,RecipeHolder<AlloyRecipe>>(); UNRESOLVED.clear();
        Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"),active=directory.resolve("hbmBlastFurnaceLegacy.json");
        try {
            Files.createDirectories(directory);
            if(Files.isRegularFile(active)) {
                merged.values().removeIf(holder->holder.value().getType()==AlloyContent.TYPE.get());
                JsonObject config; try(var reader=Files.newBufferedReader(active)) { config=JsonParser.parseReader(reader).getAsJsonObject(); }
                int index=0; for(var value:config.getAsJsonArray("recipes")) { try { JsonObject row=value.getAsJsonObject(); var recipe=new AlloyRecipe(input(row.getAsJsonArray("input1")),input(row.getAsJsonArray("input2")),output(row.getAsJsonArray("output")),row.has("hidden")&&row.get("hidden").getAsBoolean(),index); put(additions,"config",index,recipe); } catch(Exception error) { UNRESOLVED.add("config row "+index+": "+error.getMessage()); } index++; }
            } else {
                try(var template=AlloyRecipeConfig.class.getResourceAsStream("/hbm/alloy-recipe-template.json")) { if(template!=null) Files.copy(template,directory.resolve("_hbmBlastFurnaceLegacy.json"),StandardCopyOption.REPLACE_EXISTING); }
                if(com.hbm.config.GeneralModes.lbsm("LBSM_recipeSimpleChemistry")){
                    try(var resource=AlloyRecipeConfig.class.getResourceAsStream("/hbm/alloy-lbsm-recipe.json")){if(resource!=null){var row=JsonParser.parseReader(new java.io.InputStreamReader(resource,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();put(additions,"lbsm",0,new AlloyRecipe(input(row.getAsJsonArray("input1")),input(row.getAsJsonArray("input2")),output(row.getAsJsonArray("output")),false,14));}}
                }
                int index=0; for(var tag:IMC) { try { ItemStack output=imcStack(tag.getCompound("output"),server.registryAccess()); if(output.isEmpty()) throw new IllegalArgumentException("Unregistered output"); put(additions,"imc",index,new AlloyRecipe(imcInput(tag,1,server.registryAccess()),imcInput(tag,2,server.registryAccess()),output,false,100000+index)); } catch(Exception error) { UNRESOLVED.add("IMC row "+index+": "+error.getMessage()); } index++; }
            }
        } catch(Exception error) { LogUtils.getLogger().error("Cannot load original blast-furnace recipe configuration",error); }
        additions.forEach(merged::put); if(!additions.isEmpty()||!previous.isEmpty()||Files.isRegularFile(active)) manager.replaceRecipes(merged.values()); INSTALLED.put(manager,additions);
        for(String error:UNRESOLVED) LogUtils.getLogger().warn("Unbound alloy recipe: {}",error);
    }
    private static void put(Map<ResourceLocation,RecipeHolder<AlloyRecipe>> target,String prefix,int index,AlloyRecipe recipe) { var id=ResourceLocation.fromNamespaceAndPath("hbm","alloy/"+prefix+"/"+index); target.put(id,new RecipeHolder<>(id,recipe)); }
    private static Ingredient ore(String key) { return new LegacyOreIngredient(ResourceLocation.parse(HbmOreDictionary.registerName(key))).toVanilla(); }
    private static Ingredient input(JsonArray value) {
        String kind=value.get(0).getAsString(),name=value.get(1).getAsString();
        if(kind.equals("dict")) return ore(name);
        if(kind.equals("dictframe")) return net.neoforged.neoforge.common.crafting.CompoundIngredient.of(ore("ingot"+name),ore("plate"+name),ore("gem"+name),ore("dust"+name));
        if(!kind.equals("item")) throw new IllegalArgumentException("Source alloy input accepts item,dict,dictframe; got "+kind);
        ItemStack stack=legacyStack(name,value.size()>3?value.get(3).getAsInt():0); if(stack.isEmpty()) throw new IllegalArgumentException("Unregistered source input "+name); return new AlloyStackIngredient(stack).toVanilla();
    }
    private static ItemStack output(JsonArray value)throws Exception {
        ItemStack stack=legacyStack(value.get(0).getAsString(),value.size()>2?value.get(2).getAsInt():0); if(stack.isEmpty()) throw new IllegalArgumentException("Unregistered source output "+value.get(0)); stack.setCount(value.size()>1?value.get(1).getAsInt():1);
        if(value.size()>3) stack.set(DataComponents.CUSTOM_DATA,CustomData.of(TagParser.parseTag(value.get(3).getAsString()))); return stack;
    }
    private static ItemStack legacyStack(String id,int metadata) {
        ItemStack stack=MaterialDistributionConfig.legacyStack(ALIASES.getOrDefault(id,id),metadata); if(stack.isEmpty()) return stack;
        if(com.hbm.item.fluid.FluidItemContent.subtypedItems().contains(stack.getItem())) com.hbm.item.fluid.FluidItemContent.setType(stack,com.hbm.fluid.HbmFluids.fromID(metadata));
        if(stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())==0&&!stack.getItem().components().has(HbmHazards.LEGACY_METADATA.get())) stack.remove(HbmHazards.LEGACY_METADATA.get()); return stack;
    }
    private static Ingredient imcInput(CompoundTag tag,int index,HolderLookup.Provider lookup) {
        return switch(tag.getString("inputType"+index)) {
            case "ore"->ore(tag.getString("input"+index));
            case "itemstack"->{ ItemStack stack=imcStack(tag.getCompound("input"+index),lookup); if(stack.isEmpty()) throw new IllegalArgumentException("Unregistered input"+index); yield new AlloyStackIngredient(stack).toVanilla(); }
            case "orelist"->throw new IllegalArgumentException("X5846 getRecipeStacks(List) branch was commented out; original IMC orelist does not match any input");
            default->throw new IllegalArgumentException("Unknown original IMC input type"+index);
        };
    }
    private static ItemStack imcStack(CompoundTag tag,HolderLookup.Provider lookup) { if(tag.isEmpty()) return ItemStack.EMPTY; ItemStack stack=ItemStack.parseOptional(lookup,tag); if(!stack.isEmpty()) return stack; stack=legacyStack(tag.getString("id"),tag.getShort("Damage")); if(!stack.isEmpty()) { stack.setCount(tag.contains("Count")?tag.getByte("Count"):1); if(tag.contains("tag")) stack.set(DataComponents.CUSTOM_DATA,CustomData.of(tag.getCompound("tag").copy())); } return stack; }
}
