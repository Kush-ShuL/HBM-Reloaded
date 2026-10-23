package com.hbm.material;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.hazard.HbmHazards;
import com.mojang.logging.LogUtils;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Original hbmRecipes/hbmCrucibleSmelting.json array grammar and replacement semantics. */
public final class MaterialDistributionConfig {
    private static final List<String> UNBOUND=new ArrayList<>();
    private MaterialDistributionConfig() {}
    public static List<String> unresolvedEntries() { return List.copyOf(UNBOUND); }
    public static void load(Path directory) {
        UNBOUND.clear();
        try {
            Files.createDirectories(directory);
            Path active=directory.resolve("hbmCrucibleSmelting.json");
            if(!Files.isRegularFile(active)) { writeTemplate(directory.resolve("_hbmCrucibleSmelting.json")); return; }
            JsonObject root; try(var reader=Files.newBufferedReader(active,StandardCharsets.UTF_8)) { root=JsonParser.parseReader(reader).getAsJsonObject(); }
            HbmMaterials.clearDistributions();
            for(var value:root.getAsJsonArray("recipes")) {
                JsonObject row=value.getAsJsonObject(); JsonArray input=row.getAsJsonArray("input");
                List<MaterialStack> outputs=new ArrayList<>();
                for(var output:row.getAsJsonArray("output")) { JsonArray pair=output.getAsJsonArray(); NtmMaterial material=HbmMaterials.byName(pair.get(0).getAsString()); if(material==null) throw new IllegalArgumentException("Unknown case-sensitive material "+pair.get(0)); outputs.add(new MaterialStack(material,pair.get(1).getAsInt())); }
                String type=input.get(0).getAsString(),key=input.get(1).getAsString();
                if(type.equals("dict")) HbmMaterials.registerOre(key,outputs);
                else if(type.equals("item")) { int meta=input.size()>3?input.get(3).getAsInt():0; ItemStack stack=legacyStack(key,meta); if(stack.isEmpty()) { UNBOUND.add(key+"@"+meta); continue; } HbmMaterials.registerEntry(stack,outputs); }
                else { UNBOUND.add("Unsupported original MatDistribution comparable subclass: "+input); }
            }
        } catch(Exception error) { LogUtils.getLogger().error("Cannot load original crucible material distribution config",error); }
    }
    public static ItemStack legacyStack(String name,int metadata) {
        ResourceLocation id=ResourceLocation.tryParse(name); if(id==null) return ItemStack.EMPTY;
        String path=id.getPath(); if(path.startsWith("item.")) path=path.substring(5); else if(path.startsWith("tile.")) path=path.substring(5);
        if(id.getNamespace().equals("hbm")) {
            String vanilla=Map.of("ingot_copper","copper_ingot","ore_copper","copper_ore","block_copper","copper_block","raw_copper","raw_copper").get(path);
            id=vanilla==null?ResourceLocation.fromNamespaceAndPath("hbm",path):ResourceLocation.withDefaultNamespace(vanilla);
        } else {
            if(path.equals("golden_rail")) path="powered_rail";
            if(path.equals("coal")&&metadata==1) { path="charcoal"; metadata=0; }
            if(path.equals("log")&&metadata>=0&&metadata<4) { path=List.of("oak_log","spruce_log","birch_log","jungle_log").get(metadata); metadata=0; }
            id=ResourceLocation.fromNamespaceAndPath(id.getNamespace(),path);
        }
        if(!BuiltInRegistries.ITEM.containsKey(id)) return ItemStack.EMPTY;
        ItemStack stack=new ItemStack(BuiltInRegistries.ITEM.get(id));
        if(stack.getItem() instanceof com.hbm.item.FlatSourceItem) return metadata==0?stack:ItemStack.EMPTY;
        if(stack.isDamageableItem()) stack.setDamageValue(metadata); else stack.set(HbmHazards.LEGACY_METADATA.get(),metadata);
        return stack;
    }
    private static void writeTemplate(Path path)throws java.io.IOException {
        JsonObject root=new JsonObject(); root.addProperty("comment","One item at a time; amounts in quanta (72 per ingot). Material names are original case-sensitive OreDictionary suffixes. Prefix inference remains automatic; this file replaces fixed and ore overrides."); JsonArray recipes=new JsonArray();
        HbmMaterials.stackEntries().forEach((key,outputs)->{ JsonArray input=new JsonArray(); input.add("item"); input.add(key.item().toString()); input.add(1); input.add(key.metadata()); recipes.add(recipe(input,outputs)); });
        HbmMaterials.oreEntries().forEach((key,outputs)->{ JsonArray input=new JsonArray(); input.add("dict"); input.add(key); recipes.add(recipe(input,outputs)); });
        root.add("recipes",recipes); Files.writeString(path,new GsonBuilder().setPrettyPrinting().create().toJson(root)+"\n",StandardCharsets.UTF_8);
    }
    private static JsonObject recipe(JsonArray input,List<MaterialStack> outputs) { JsonObject row=new JsonObject(); row.add("input",input); JsonArray result=new JsonArray(); for(MaterialStack value:outputs) { JsonArray pair=new JsonArray(); pair.add(value.material.names().getFirst()); pair.add(value.amount); result.add(pair); } row.add("output",result); return row; }
}
