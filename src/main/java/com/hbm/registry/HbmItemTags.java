package com.hbm.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hbm.item.autogen.AutogenContent;
import com.hbm.material.HbmMaterials;
import com.hbm.material.resources.GeneratedMaterialResources;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;

/** Native tags own membership, reloads and synchronization. This class stores only selection priority. */
public final class HbmItemTags {
    private static final Map<String,Integer> ORDER = new LinkedHashMap<>();
    private static final Map<String,String> SHAPES = Map.ofEntries(
        Map.entry("NUGGET","c:nuggets"), Map.entry("TINY","c:nuggets"),
        Map.entry("INGOT","c:ingots"), Map.entry("DUST","c:dusts"),
        Map.entry("GEM","c:gems"), Map.entry("CRYSTAL","c:crystals"),
        Map.entry("PLATE","c:plates"), Map.entry("BLOCK","c:storage_blocks"),
        Map.entry("ORE","c:ores"), Map.entry("ONLY_ORE","c:ores"),
        Map.entry("ORENETHER","hbm:ores_in_ground/nether"),
        Map.entry("DUSTTINY","hbm:dusts/tiny"), Map.entry("WIRE","hbm:wires/fine"),
        Map.entry("DENSEWIRE","hbm:wires/dense"), Map.entry("BOLT","hbm:bolts"),
        Map.entry("BILLET","hbm:billets"), Map.entry("FRAGMENT","hbm:bedrock_ore_fragments"),
        Map.entry("CASTPLATE","hbm:plates/cast"), Map.entry("WELDEDPLATE","hbm:plates/welded"),
        Map.entry("SHELL","hbm:gun_parts/shells"), Map.entry("PIPE","hbm:pipes"),
        Map.entry("LIGHTBARREL","hbm:gun_parts/barrels/light"), Map.entry("HEAVYBARREL","hbm:gun_parts/barrels/heavy"),
        Map.entry("LIGHTRECEIVER","hbm:gun_parts/receivers/light"), Map.entry("HEAVYRECEIVER","hbm:gun_parts/receivers/heavy"),
        Map.entry("MECHANISM","hbm:gun_parts/mechanisms"), Map.entry("STOCK","hbm:gun_parts/stocks"),
        Map.entry("GRIP","hbm:gun_parts/grips"));
    static {
        try (var stream=HbmItemTags.class.getResourceAsStream("/hbm/item-tag-priority.json")) {
            if(stream==null) throw new IllegalStateException("Missing item tag selection priorities");
            var tags=JsonParser.parseReader(new InputStreamReader(stream,StandardCharsets.UTF_8)).getAsJsonArray();
            for(var tag:tags) ORDER.putIfAbsent(ResourceLocation.parse(tag.getAsString()).toString(),ORDER.size());
        } catch(java.io.IOException error) { throw new ExceptionInInitializerError(error); }
    }
    private HbmItemTags() {}
    public static void register(IEventBus bus) {
        GeneratedMaterialResources.contribute(PackType.SERVER_DATA,HbmItemTags::generatedTags);
        GeneratedMaterialResources.register(bus);
        bus.addListener((net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event)->event.enqueueWork(()->{
            for(var identity:AutogenContent.identities()) if(identity.sourceOreMember()) {
                String tag=material(identity.shape().symbol(),identity.material().names().getFirst());
                ORDER.putIfAbsent(tag,ORDER.size());
            }
        }));
    }
    public static TagKey<Item> key(String id) { return TagKey.create(Registries.ITEM,ResourceLocation.parse(id)); }
    public static Ingredient ingredient(String id) { return Ingredient.of(key(id)); }
    /** Detect a real vanilla tag value without expanding its current members. */
    public static Optional<TagKey<Item>> tag(Ingredient ingredient) {
        if(ingredient.isCustom()) return Optional.empty();
        var values=ingredient.getValues();
        return values.length==1 && values[0] instanceof Ingredient.TagValue value ? Optional.of(value.tag()) : Optional.empty();
    }
    public static List<String> tags(ItemStack stack) {
        return stack.getTags().map(tag->tag.location().toString())
            .sorted(Comparator.comparingInt((String id)->ORDER.getOrDefault(id,Integer.MAX_VALUE)).thenComparing(Comparator.naturalOrder())).toList();
    }
    /** Defined empty tags count as defined; membership is never cached here. */
    public static List<String> allTags() {
        return BuiltInRegistries.ITEM.getTagNames().map(tag->tag.location().toString())
            .sorted(Comparator.comparingInt((String id)->ORDER.getOrDefault(id,Integer.MAX_VALUE)).thenComparing(Comparator.naturalOrder())).toList();
    }
    public static Stream<ItemStack> items(String id) {
        return BuiltInRegistries.ITEM.getTag(key(id)).stream().flatMap(set->set.stream()).map(holder->new ItemStack(holder.value()));
    }
    /** Material and shape descriptors construct tag keys; they never assign item membership. */
    public static String material(String shapeSymbol,String materialName) {
        var configured=HbmMaterials.byName(materialName);
        String name=token(configured==null?materialName:configured.names().getFirst());
        String category=SHAPES.get(shapeSymbol);
        if(category==null && shapeSymbol.startsWith("custom:")) category="hbm:custom_shapes/"+token(shapeSymbol.substring(7));
        if(category==null) throw new IllegalArgumentException("Material shape has no item tag category: "+shapeSymbol);
        return category+"/"+name;
    }
    public static String category(String tagId) {
        String path=ResourceLocation.parse(tagId).getPath();
        int last=path.lastIndexOf('/');
        return last<0?path:path.substring(0,last);
    }
    public static String materialName(String tagId) {
        String path=ResourceLocation.parse(tagId).getPath();
        return path.substring(path.lastIndexOf('/')+1);
    }
    public static String token(String value) {
        return value.replaceAll("([A-Z]+)([A-Z][a-z])","$1_$2")
            .replaceAll("([a-z0-9])([A-Z])","$1_$2").replaceAll("([a-zA-Z])([0-9])","$1_$2")
            .toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9_/]+","_").replaceAll("^_+|_+$","");
    }
    private static Map<ResourceLocation,byte[]> generatedTags() {
        var definitions=new LinkedHashMap<String,LinkedHashSet<String>>();
        for(var identity:AutogenContent.identities()) if(identity.sourceOreMember()) {
            String id=material(identity.shape().symbol(),identity.material().names().getFirst());
            definitions.computeIfAbsent(id,ignored->new LinkedHashSet<>()).add(identity.itemId().toString());
        }
        var result=new LinkedHashMap<ResourceLocation,byte[]>();
        definitions.forEach((id,members)->{
            var json=new JsonObject(); json.addProperty("replace",false); var values=new JsonArray(); members.forEach(values::add); json.add("values",values);
            var tag=ResourceLocation.parse(id);
            result.put(ResourceLocation.fromNamespaceAndPath(tag.getNamespace(),"tags/item/"+tag.getPath()+".json"),json.toString().getBytes(StandardCharsets.UTF_8));
        });
        return result;
    }
}
