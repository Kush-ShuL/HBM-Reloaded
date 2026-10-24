package com.hbm.material;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.autogen.ScrapsItem;
import com.hbm.registry.HbmItemTags;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

/** Native X5846 Mats/MaterialShapes registry and one-item quantum accounting. */
public final class HbmMaterials {
    private static final Logger LOGGER=LogUtils.getLogger();
    private static final Gson GSON=new GsonBuilder().setPrettyPrinting().create();
    private static final List<NtmMaterial> ORDERED=new ArrayList<>();
    private static final Map<Integer,NtmMaterial> BY_ID=new HashMap<>();
    private static final Map<String,NtmMaterial> BY_NAME=new HashMap<>(), BY_SYMBOL=new HashMap<>();
    private static final Map<String,MaterialShape> SHAPES=new LinkedHashMap<>();
    // Deliberately HashMap, matching original prefix iteration and later-prefix overwrites.
    private static final Map<String,MaterialShape> PREFIXES=new HashMap<>();
    private static final List<MaterialShape> CUSTOM_SHAPES=new ArrayList<>();
    private static final List<MoldDefinition> MOLDS=new ArrayList<>();
    private static final Map<StackKey,List<MaterialStack>> STACK_ENTRIES=new HashMap<>();
    private static final Map<String,List<MaterialStack>> TAG_ENTRIES=new HashMap<>();
    private static final Map<String,MaterialStack> INFERRED_TAG_MATERIALS=new HashMap<>();
    private static JsonObject source;
    private static boolean initialized;
    private HbmMaterials() {}
    public static synchronized void initialize() {
        if(initialized) return;
        source=readResource("/hbm/materials.json");
        for(JsonElement value:source.getAsJsonArray("shapes")) {
            JsonObject row=value.getAsJsonObject();
            addShape(new MaterialShape(row.get("symbol").getAsString(),row.get("quantity").getAsInt(),strings(row.getAsJsonArray("prefixes")),row.get("noAutogen").getAsBoolean()),false);
        }
        for(JsonElement value:source.getAsJsonArray("materials")) {
            JsonObject row=value.getAsJsonObject();
            NtmMaterial material=new NtmMaterial(row.get("symbol").getAsString(),row.get("id").getAsInt(),strings(row.getAsJsonArray("names")),
                    NtmMaterial.SmeltingBehavior.valueOf(row.get("smeltingBehavior").getAsString()),row.get("solidColorLight").getAsInt(),row.get("solidColorDark").getAsInt(),row.get("moltenColor").getAsInt());
            for(JsonElement shape:row.getAsJsonArray("shapes")) material.autogen(SHAPES.get(shape.getAsString()));
            for(JsonElement trait:row.getAsJsonArray("traits")) material.trait(NtmMaterial.Trait.valueOf(trait.getAsString()));
            addMaterial(material);
        }
        for(JsonElement value:source.getAsJsonArray("materials")) { JsonObject row=value.getAsJsonObject(); BY_SYMBOL.get(row.get("symbol").getAsString()).setConversion(BY_SYMBOL.get(row.get("smeltsInto").getAsString()),row.get("convIn").getAsInt(),row.get("convOut").getAsInt()); }
        Path directory=FMLPaths.CONFIGDIR.get().resolve("hbmConfig");
        try { Files.createDirectories(directory); readCustomMaterials(defaultFile(directory.resolve("hbmMats.json"),source.getAsJsonObject("defaultCustomMaterials"))); }
        catch(Exception error) { LOGGER.error("Could not read original HBM custom materials",error); }
        try { readCustomShapes(defaultFile(directory.resolve("hbmShapes.json"),source.getAsJsonObject("defaultCustomShapes"))); }
        catch(Exception error) { LOGGER.error("Could not read original HBM custom shapes",error); }
        initialized=true;
        for(var shape:SHAPES.values()) {
            if(shape.prefixes().isEmpty() || shape.symbol().equals("ANY")) continue;
            for(var material:ORDERED) {
                var smelts=material.smeltsInto().smeltingBehavior();
                if(smelts==NtmMaterial.SmeltingBehavior.SMELTABLE || smelts==NtmMaterial.SmeltingBehavior.ADDITIVE)
                    INFERRED_TAG_MATERIALS.putIfAbsent(HbmItemTags.material(shape.symbol(),material.names().getFirst()),new MaterialStack(material,shape.q(1)));
            }
        }
    }
    private static void addMaterial(NtmMaterial material) { ORDERED.add(material); BY_ID.put(material.id(),material); BY_SYMBOL.put(material.symbol(),material); material.names().forEach(name->BY_NAME.put(name,material)); }
    private static void addShape(MaterialShape shape,boolean custom) { SHAPES.put(shape.symbol(),shape); shape.prefixes().forEach(prefix->PREFIXES.put(prefix,shape)); if(custom) CUSTOM_SHAPES.add(shape); }
    private static JsonObject readResource(String resource) {
        try(var stream=HbmMaterials.class.getResourceAsStream(resource)) {
            if(stream==null) throw new IllegalStateException("Missing source material definitions "+resource);
            return JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonObject();
        } catch(IOException error) { throw new IllegalStateException(error); }
    }
    private static JsonObject defaultFile(Path path,JsonObject defaults)throws IOException {
        if(!Files.exists(path)) Files.writeString(path,GSON.toJson(defaults)+"\n",StandardCharsets.UTF_8);
        try(var reader=Files.newBufferedReader(path,StandardCharsets.UTF_8)) { return JsonParser.parseReader(reader).getAsJsonObject(); }
    }
    private static List<String> strings(JsonArray array) { var values=new ArrayList<String>(); array.forEach(value->values.add(value.getAsString())); return values; }
    private static void readCustomMaterials(JsonObject root) {
        for(var entry:root.entrySet()) {
            JsonObject row=entry.getValue().getAsJsonObject(); int id=row.get("id").getAsInt();
            if(id>Short.MAX_VALUE || BY_ID.containsKey(id)) { LOGGER.error("Cannot register custom material {} with ID {}; original loader stops at an occupied ID or an ID above32767",entry.getKey(),id); break; }
            var requested=NtmMaterial.SmeltingBehavior.valueOf(row.get("smeltingBehavior").getAsString());
            // Source switch's default make(id,dict) intentionally retains default colors/NOT_SMELTABLE.
            boolean colored=requested==NtmMaterial.SmeltingBehavior.SMELTABLE || requested==NtmMaterial.SmeltingBehavior.NOT_SMELTABLE || requested==NtmMaterial.SmeltingBehavior.ADDITIVE;
            NtmMaterial material=new NtmMaterial(entry.getKey(),id,List.of(entry.getKey()),colored?requested:NtmMaterial.SmeltingBehavior.NOT_SMELTABLE,
                    colored?row.get("solidColorLight").getAsInt():0xFF4A00,colored?row.get("solidColorDark").getAsInt():0x802000,colored?row.get("moltenColor").getAsInt():0xFF4A00);
            addMaterial(material);
            material.trait(NtmMaterial.Trait.valueOf(row.get("matTraits").getAsString()));
            row.getAsJsonArray("shapes").forEach(shape->material.autogen(PREFIXES.get(shape.getAsString())));
        }
    }
    private static void readCustomShapes(JsonObject root) {
        for(var entry:root.entrySet()) {
            JsonObject row=entry.getValue().getAsJsonObject();
            MaterialShape shape=new MaterialShape("custom:"+entry.getKey(),row.get("quantity").getAsInt(),List.of(entry.getKey()),false);
            addShape(shape,false);
            row.getAsJsonArray("materials").forEach(value->{ NtmMaterial material=BY_NAME.get(value.getAsString()); if(material!=null) material.autogen(shape); });
            row.getAsJsonArray("molds").forEach(value->{ JsonObject mold=value.getAsJsonObject(); int amount=mold.get("castAmount").getAsInt(); boolean large=mold.get("size").getAsString().equals("L"); MOLDS.add(new MoldDefinition(shape,large,amount,entry.getKey()+"."+amount)); });
            CUSTOM_SHAPES.add(shape);
        }
    }
    public static List<NtmMaterial> ordered() { initialize(); return List.copyOf(ORDERED); }
    public static NtmMaterial byId(int id) { initialize(); return BY_ID.get(id); }
    public static NtmMaterial byName(String name) { initialize(); return BY_NAME.get(name); }
    public static NtmMaterial bySymbol(String symbol) { initialize(); return BY_SYMBOL.get(symbol); }
    public static MaterialShape shape(String symbol) { initialize(); return SHAPES.get(symbol); }
    public static MaterialShape prefix(String name) { initialize(); return PREFIXES.get(name); }
    public static List<MaterialShape> shapes() { initialize(); return List.copyOf(SHAPES.values()); }
    public static List<MaterialShape> customShapes() { initialize(); return List.copyOf(CUSTOM_SHAPES); }
    public static List<MoldDefinition> molds() { initialize(); return List.copyOf(MOLDS); }
    public record MoldDefinition(MaterialShape shape,boolean large,int castAmount,String legacyName) {}
    public static int metadata(ItemStack stack) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue()); }
    public static void registerEntry(ItemStack stack,List<MaterialStack> materials) { if(!stack.isEmpty()&&!materials.isEmpty()) STACK_ENTRIES.put(StackKey.of(stack),List.copyOf(materials)); }
    public static void registerTag(String tag,List<MaterialStack> materials) {
        if(!materials.isEmpty()) TAG_ENTRIES.put(HbmItemTags.key(tag).location().toString(),List.copyOf(materials));
    }
    private static MaterialStack infer(String tag) {
        var value=INFERRED_TAG_MATERIALS.get(tag);
        return value==null?null:value.copy();
    }
    /** Source order: first native tag match, then exact stack additions, then scraps. Count and NBT do not alter comparable-stack identity. */
    public static List<MaterialStack> getMaterialsFromItem(ItemStack stack) {
        initialize(); var result=new ArrayList<MaterialStack>(); if(stack.isEmpty()) return result;
        stack=com.hbm.ore.RawOreContent.processingStack(stack);
        for(String tag:HbmItemTags.tags(stack)) {
            List<MaterialStack> explicit=TAG_ENTRIES.get(tag);
            if(explicit!=null) { explicit.forEach(value->result.add(value.copy())); break; }
            var inferred=infer(tag);
            if(inferred!=null) { result.add(inferred); break; }
        }
        var specific=STACK_ENTRIES.get(StackKey.of(stack)); if(specific!=null) specific.forEach(value->result.add(value.copy()));
        if(stack.getItem() instanceof ScrapsItem) { MaterialStack scraps=ScrapsItem.getMaterials(stack); if(scraps!=null) result.add(scraps); }
        return result;
    }
    public static List<MaterialStack> getSmeltingMaterialsFromItem(ItemStack stack) { return getMaterialsFromItem(stack).stream().map(value->new MaterialStack(value.material.smeltsInto(),value.amount*value.material.convOut()/value.material.convIn())).toList(); }
    public static Component formatAmount(int amount,boolean showInMb) {
        initialize(); if(showInMb) return Component.literal((amount*2)+"mB");
        var parts=new ArrayList<Component>();
        for(String symbol:List.of("BLOCK","INGOT","NUGGET","QUANTUM")) {
            MaterialShape shape=SHAPES.get(symbol); int count=amount/shape.quantity(); amount-=shape.q(count);
            if(count>0) { String key=symbol.toLowerCase(Locale.ROOT); if(count!=1) key=key.equals("quantum")?"quanta":key+"s"; parts.add(Component.translatable("matshape."+key,count)); }
        }
        var text=Component.empty(); for(int index=0;index<parts.size();index++) { if(index>0) text.append(" "); text.append(parts.get(index)); } return text;
    }
    /** Registry-dependent distributions run in common setup, after actual item registration. */
    public static void initializeDistributions() {
        initialize(); STACK_ENTRIES.clear(); TAG_ENTRIES.clear();
        for(JsonElement value:source.getAsJsonArray("distributions")) {
            JsonObject row=value.getAsJsonObject(); var outputs=readOutputs(row.getAsJsonArray("outputs"));
            if(row.get("kind").getAsString().equals("tag")) row.getAsJsonArray("keys").forEach(key->registerTag(key.getAsString(),outputs));
            else { JsonObject input=row.getAsJsonObject("input"); ResourceLocation id=ResourceLocation.parse(input.get("id").getAsString()); if(!BuiltInRegistries.ITEM.containsKey(id)) continue;
                ItemStack stack=new ItemStack(BuiltInRegistries.ITEM.get(id));
                if(input.has("components")) { JsonObject components=input.getAsJsonObject("components"); int meta=components.get(components.has("hbm:legacy_metadata")?"hbm:legacy_metadata":"minecraft:damage").getAsInt(); if(stack.isDamageableItem()) stack.setDamageValue(meta); else stack.set(HbmHazards.LEGACY_METADATA.get(),meta); }
                registerEntry(stack,outputs);
            }
        }
        MaterialDistributionConfig.load(FMLPaths.CONFIGDIR.get().resolve("hbmRecipes"));
    }
    private static List<MaterialStack> readOutputs(JsonArray array) { var result=new ArrayList<MaterialStack>(); for(JsonElement value:array) { JsonObject row=value.getAsJsonObject(); NtmMaterial material=BY_ID.get(row.get("materialId").getAsInt()); if(material!=null) result.add(new MaterialStack(material,row.get("amount").getAsInt())); } return result; }
    /** Read-only source distribution enumeration for arc-furnace autogeneration; mutable quanta are detached. */
    public record StackDistribution(ResourceLocation item,int metadata,List<MaterialStack> materials) {}
    public record TagDistribution(String tag,List<MaterialStack> materials) {}
    public static List<StackDistribution> stackDistributions() {
        initialize(); return STACK_ENTRIES.entrySet().stream().map(entry->new StackDistribution(
                ResourceLocation.fromNamespaceAndPath(entry.getKey().item().getNamespace(),entry.getKey().item().getPath()),entry.getKey().metadata(),
                entry.getValue().stream().map(MaterialStack::copy).toList())).toList();
    }
    public static List<TagDistribution> tagDistributions() {
        initialize(); return TAG_ENTRIES.entrySet().stream().map(entry->new TagDistribution(entry.getKey(),entry.getValue().stream().map(MaterialStack::copy).toList())).toList();
    }
    static void clearDistributions() { STACK_ENTRIES.clear(); TAG_ENTRIES.clear(); }
    static Map<StackKey,List<MaterialStack>> stackEntries() { return STACK_ENTRIES; }
    static Map<String,List<MaterialStack>> tagEntries() { return TAG_ENTRIES; }
    static record StackKey(ResourceLocation item,int metadata) { static StackKey of(ItemStack stack) { return new StackKey(BuiltInRegistries.ITEM.getKey(stack.getItem()),HbmMaterials.metadata(stack)); } }
}
