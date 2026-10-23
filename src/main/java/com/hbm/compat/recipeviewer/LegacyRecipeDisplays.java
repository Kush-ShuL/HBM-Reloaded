package com.hbm.compat.recipeviewer;

import com.google.gson.*;
import com.hbm.fluid.FluidContainerRegistry;
import com.hbm.fluid.HbmFluids;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.fluid.FluidIconItem;
import com.hbm.item.fluid.FluidItemContent;
import com.hbm.registry.HbmOreDictionary;
import com.mojang.logging.LogUtils;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** Common source-backed display graph shared by both viewers; does not install executable recipes. */
public final class LegacyRecipeDisplays {
    public record Slot(List<ItemStack> alternatives,Map<String,List<Component>> annotations) {
        public Slot { alternatives=List.copyOf(alternatives); annotations=Map.copyOf(annotations); }
        public Slot(List<ItemStack> alternatives) { this(alternatives,Map.of()); }
        public List<Component> tooltip(ItemStack stack) { return annotations.getOrDefault(variant(stack),List.of()); }
    }
    public record Display(ResourceLocation id,String category,List<Slot> inputs,List<Slot> outputs,Integer duration,Long power,List<String> notes) {}
    public record Category(String id,String title,String layout,ResourceLocation texture,int width,int height,List<ItemStack> machines) {}
    private static final List<Category> CATEGORIES=new ArrayList<>();
    private static final List<Display> DISPLAYS=new ArrayList<>();
    private static final List<String> UNRESOLVED=new ArrayList<>();
    private static boolean loaded;
    private LegacyRecipeDisplays() {}
    public static synchronized List<Category> categories() {
        load();
        // Actual machine ports own their native categories; do not display their processing twice.
        return CATEGORIES.stream().filter(category->!(Set.of("shredder","centrifuge","alloy","assembly","chemical","crystallizer","soldering","precision_assembly","solidification","rockmill","combination").contains(category.id)&&BuiltInRegistries.RECIPE_TYPE.containsKey(ResourceLocation.fromNamespaceAndPath("hbm",category.id))))
                .filter(category->!category.id.equals("liquefaction")||!BuiltInRegistries.RECIPE_TYPE.containsKey(ResourceLocation.fromNamespaceAndPath("hbm","oil_liquefaction")))
                .filter(category->!category.id.equals("space_assembly")||!BuiltInRegistries.RECIPE_TYPE.containsKey(ResourceLocation.fromNamespaceAndPath("hbm","space_assembler")))
                .filter(category->!category.id.equals("super_computer")||!BuiltInRegistries.RECIPE_TYPE.containsKey(ResourceLocation.fromNamespaceAndPath("hbm","supercomputer")))
                .filter(category->DISPLAYS.stream().anyMatch(display->display.category.equals(category.id))).toList();
    }
    public static List<Display> recipes(String category) { load(); return DISPLAYS.stream().filter(display->display.category.equals(category)).toList(); }
    public static List<String> unresolved() { load(); return List.copyOf(UNRESOLVED); }
    private static synchronized void load() {
        if(loaded) return; loaded=true;
        try(var input=LegacyRecipeDisplays.class.getResourceAsStream("/hbm/nei-recipes.json")) {
            if(input==null) throw new IllegalStateException("Missing original NEI display data");
            JsonObject root=JsonParser.parseReader(new InputStreamReader(input,StandardCharsets.UTF_8)).getAsJsonObject();
            for(var value:root.getAsJsonArray("categories")) {
                var row=value.getAsJsonObject(); var machines=new ArrayList<ItemStack>();
                for(var item:row.getAsJsonArray("machines")) { ItemStack stack=registered(item.getAsString(),1); if(!stack.isEmpty()) machines.add(stack); }
                CATEGORIES.add(new Category(row.get("id").getAsString(),row.get("title").getAsString(),row.get("layout").getAsString(),ResourceLocation.parse(row.get("texture").getAsString()),row.get("width").getAsInt(),row.get("height").getAsInt(),List.copyOf(machines)));
            }
            for(var value:root.getAsJsonArray("recipes")) {
                var row=value.getAsJsonObject();
                try {
                    var inputs=slots(row.getAsJsonArray("inputs"),true); var outputs=slots(row.getAsJsonArray("outputs"),false);
                    if(Set.of("blast_furnace","rockmill","purex","chemical","centrifuge","liquefaction","shredder","alloy","crystallizer").contains(row.get("category").getAsString())) inputs=inputs.stream().map(slot->new Slot(com.hbm.ore.OreRecipeInputs.examples(slot.alternatives()),slot.annotations())).toList();
                    if(inputs.stream().anyMatch(slot->slot.alternatives.isEmpty()) || outputs.stream().allMatch(slot->slot.alternatives.isEmpty())) throw new IllegalArgumentException("No bound source ingredient/output");
                    var notes=new ArrayList<String>(); if(row.has("notes")) row.getAsJsonArray("notes").forEach(note->notes.add(note.getAsString()));
                    DISPLAYS.add(new Display(ResourceLocation.parse(row.get("id").getAsString()),row.get("category").getAsString(),inputs,outputs,row.has("duration")?row.get("duration").getAsInt():null,row.has("power")?row.get("power").getAsLong():null,List.copyOf(notes)));
                } catch(RuntimeException error) { UNRESOLVED.add(row.get("id").getAsString()+": "+error.getMessage()); }
            }
            fluidContainers(); shredderPost();
            LogUtils.getLogger().info("Bound {} source NEI processing displays; {} source candidates await registered ingredients",DISPLAYS.size(),UNRESOLVED.size());
        } catch(java.io.IOException error) { throw new IllegalStateException("Cannot load original NEI processing displays",error); }
    }
    private static List<Slot> slots(JsonArray values,boolean input) {
        var result=new ArrayList<Slot>();
        for(var value:values) { var row=value.getAsJsonObject(); var annotations=new HashMap<String,List<Component>>(); if(!input) annotations(row,annotations); result.add(new Slot(decode(row,input),annotations)); }
        return List.copyOf(result);
    }
    private static String variant(ItemStack stack) { return BuiltInRegistries.ITEM.getKey(stack.getItem())+"@"+stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())+"#"+stack.getOrDefault(FluidItemContent.FLUID_TYPE.get(),-1); }
    private static void annotations(JsonObject row,Map<String,List<Component>> result) {
        if(row.get("kind").getAsString().equals("alternatives")) for(var value:row.getAsJsonArray("alternatives")) annotations(value.getAsJsonObject(),result);
        var labels=new ArrayList<Component>();
        if(row.has("chance")&&row.get("chance").getAsDouble()<1) labels.add(Component.literal(percent(row.get("chance").getAsDouble())+" per item").withStyle(net.minecraft.ChatFormatting.RED));
        if(row.has("selectionProbability")&&row.get("selectionProbability").getAsDouble()<1) labels.add(Component.literal(percent(row.get("selectionProbability").getAsDouble())+" selection").withStyle(net.minecraft.ChatFormatting.RED));
        if(!labels.isEmpty()) for(var stack:decode(row,false)) result.put(variant(stack),List.copyOf(labels));
    }
    private static List<ItemStack> decode(JsonObject row,boolean input) {
        String kind=row.get("kind").getAsString(); var stacks=new ArrayList<ItemStack>();
        switch(kind) {
            case "empty" -> { return List.of(); }
            case "item" -> {
                var spec=row.getAsJsonObject("stack"); var stack=registered(spec.get("id").getAsString(),spec.get("count").getAsInt());
                if(stack.isEmpty()) throw new IllegalArgumentException("Item not registered: "+spec.get("id"));
                if(spec.has("components")) for(var component:spec.getAsJsonObject("components").entrySet()) switch(component.getKey()) {
                    case "hbm:legacy_metadata" -> stack.set(HbmHazards.LEGACY_METADATA.get(),component.getValue().getAsInt());
                    case "hbm:fluid_type" -> stack.set(FluidItemContent.FLUID_TYPE.get(),component.getValue().getAsInt());
                    case "minecraft:damage" -> stack.set(DataComponents.DAMAGE,component.getValue().getAsInt());
                    case "minecraft:custom_data" -> stack.set(DataComponents.CUSTOM_DATA,
                            net.minecraft.world.item.component.CustomData.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE,component.getValue()).getOrThrow());
                    default -> throw new IllegalArgumentException("Display component not decoded: "+component.getKey());
                }
                stacks.add(stack);
            }
            case "ore" -> {
                int count=row.has("count")?row.get("count").getAsInt():1;
                HbmOreDictionary.items(row.get("tag").getAsString()).forEach(stack->stacks.add(stack.copyWithCount(count)));
            }
            case "fluid" -> stacks.add(FluidIconItem.make(HbmFluids.fromName(row.get("type").getAsString()),row.get("amount").getAsInt(),row.has("pressure")?row.get("pressure").getAsInt():0));
            case "alternatives" -> { for(var value:row.getAsJsonArray("alternatives")) stacks.addAll(decode(value.getAsJsonObject(),input)); }
            default -> throw new IllegalArgumentException("Unknown source ingredient: "+kind);
        }
        return stacks;
    }
    private static String percent(double value) { return ((int)(value*1000))/10F+"%"; }
    private static ItemStack registered(String id,int count) { var location=ResourceLocation.tryParse(id); return location!=null&&BuiltInRegistries.ITEM.containsKey(location)?new ItemStack(BuiltInRegistries.ITEM.get(location),count):ItemStack.EMPTY; }
    private static void fluidContainers() {
        int index=0;
        for(var container:FluidContainerRegistry.allContainers) {
            var inputs=new ArrayList<Slot>(); inputs.add(new Slot(List.of(FluidIconItem.make(container.type(),container.content()))));
            if(!container.emptyContainer().isEmpty()) inputs.add(new Slot(List.of(container.emptyContainer().copy())));
            DISPLAYS.add(new Display(ResourceLocation.fromNamespaceAndPath("hbm","nei/fluid_containers/runtime/"+index++),"fluid_containers",List.copyOf(inputs),List.of(new Slot(List.of(container.fullContainer().copy()))),null,null,List.of()));
        }
    }
    private record Key(String item,int metadata) {}
    private static Key key(ItemStack stack) { return new Key(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(),stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),stack.getDamageValue())); }
    private static List<ItemStack> ore(String name) { return HbmOreDictionary.items("hbm:legacy_ore_dict/"+name.toLowerCase(Locale.ROOT)).toList(); }
    private static ItemStack preferredDust(String name) {
        var matches=ore("dust"+name); if(matches.isEmpty()) return ItemStack.EMPTY;
        return matches.stream().filter(stack->BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals("hbm")).findFirst().orElse(matches.getFirst()).copy();
    }
    private static void shredderPost() {
        var used=new HashSet<Key>();
        for(var recipe:DISPLAYS) if(recipe.category.equals("shredder")) for(var stack:recipe.inputs.getFirst().alternatives) used.add(key(stack));
        int[] index={0};
        // These source registerDefaults ore loops run before general registerPost inference.
        for(var rule:List.of(new String[]{"logWood","hbm:powder_sawdust","4"},new String[]{"plankWood","hbm:powder_sawdust","1"},new String[]{"treeSapling","minecraft:stick","1"},new String[]{"dustLapis","hbm:powder_cobalt_tiny","1"})) {
            ItemStack result=registered(rule[1],Integer.parseInt(rule[2])); if(!result.isEmpty()) for(var input:ore(rule[0])) shred(used,index,input,result);
        }
        for(String name:HbmOreDictionary.allNames()) {
            if(name.isEmpty()||name.contains("Any")) continue;
            var matches=ore(name); if(matches.isEmpty()) continue;
            for(String prefix:List.of("ingot","plate","gem","crystal","ore")) if(name.startsWith(prefix)&&name.length()>prefix.length()) {
                ItemStack dust=preferredDust(name.substring(prefix.length()));
                if(!dust.isEmpty()&&!BuiltInRegistries.ITEM.getKey(dust.getItem()).toString().equals("hbm:scrap")) { dust.setCount(prefix.equals("ore")?2:1); for(var input:matches) shred(used,index,input,dust); }
            }
            if(name.startsWith("block")&&name.length()>5) {
                String suffix=name.substring(5); ItemStack dust=preferredDust(suffix);
                if(!dust.isEmpty()&&!BuiltInRegistries.ITEM.getKey(dust.getItem()).toString().equals("hbm:scrap")) { dust.setCount(ore("ingot"+suffix).isEmpty()&&ore("gem"+suffix).isEmpty()?4:9); for(var input:matches) shred(used,index,input,dust); }
            }
            ItemStack fallback=name.startsWith("dustTiny")&&name.length()>8?registered("hbm:dust_tiny",1):name.startsWith("dust")&&name.length()>4?registered("hbm:dust",1):ItemStack.EMPTY;
            if(!fallback.isEmpty()) for(var input:matches) shred(used,index,input,fallback);
        }
    }
    private static void shred(Set<Key> used,int[] index,ItemStack input,ItemStack result) {
        if(input.isEmpty()||!used.add(key(input))) return;
        DISPLAYS.add(new Display(ResourceLocation.fromNamespaceAndPath("hbm","nei/shredder/ore_post/"+index[0]++),"shredder",List.of(new Slot(List.of(input.copyWithCount(1)))),List.of(new Slot(List.of(result.copy()))),null,null,List.of()));
    }
    public static ItemStack icon(Category category) {
        if(!category.machines.isEmpty()) return category.machines.getFirst();
        return recipes(category.id).stream().flatMap(recipe->recipe.outputs.stream()).flatMap(slot->slot.alternatives.stream()).findFirst().orElse(ItemStack.EMPTY);
    }
    /** Original NEIUniversalHandler and NEIGenericRecipeHandler coordinates; not modern crafting grids. */
    public static int[][] inputCoordinates(Category category,int count) {
        String layout=category.layout;
        if(layout.equals("shredder")) return new int[][]{{39,24}};
        if(layout.equals("alloy")) return new int[][]{{75,7},{75,43}};
        if(layout.equals("fluidcontainers")) return count==1?new int[][]{{30,24}}:new int[][]{{30,24},{48,24}};
        int[][] result;
        if(layout.equals("generic")) {
            result=switch(count) {
                case 1->new int[][]{{48,24}}; case 2->new int[][]{{30,24},{48,24}}; case 3->new int[][]{{12,24},{30,24},{48,24}};
                case 4->new int[][]{{30,15},{48,15},{30,33},{48,33}}; case 5->new int[][]{{12,15},{30,15},{48,15},{12,33},{30,33}}; case 6->new int[][]{{12,15},{30,15},{48,15},{12,33},{30,33},{48,33}};
                default->{ int columns=(count+2)/3; var points=new int[count][2]; for(int i=0;i<count;i++) points[i]=new int[]{12+(i%columns)*18-(columns==4?18:0),6+(i/columns)*18}; yield points; }
            };
            if(category.id.equals("assembly")) { int shift=count>12?-9:count>9?18:0; for(var point:result) point[0]+=shift; }
        } else {
            result=switch(count) {
                case 1->new int[][]{{48,24}}; case 2->new int[][]{{48,24},{30,24}}; case 3->new int[][]{{48,24},{30,24},{12,24}};
                case 4->new int[][]{{48,15},{30,15},{48,33},{30,33}}; case 5->new int[][]{{48,15},{30,15},{12,24},{48,33},{30,33}}; case 6->new int[][]{{48,15},{30,15},{12,15},{48,33},{30,33},{12,33}};
                case 7->new int[][]{{48,6},{30,15},{12,15},{48,24},{30,33},{12,33},{48,42}};
                case 8->new int[][]{{48,6},{30,6},{12,15},{48,24},{30,24},{12,33},{48,42},{30,42}};
                case 9->new int[][]{{48,6},{30,6},{12,6},{48,24},{30,24},{12,24},{48,42},{30,42},{12,42}};
                default->{ var points=new int[count][2]; for(int i=0;i<count;i++) points[i]=new int[]{i%4*18,i/4*18}; yield points; }
            };
        }
        return result;
    }
    public static int assemblyOffset(Category category,Display display) { return category.id.equals("assembly")?(display.inputs.size()>12?27:display.inputs.size()>9?18:0):0; }
    public static int[][] outputCoordinates(Category category,Display display) {
        if(category.layout.equals("shredder")) return new int[][]{{129,24}};
        if(category.layout.equals("alloy")) return new int[][]{{129,25}};
        if(category.layout.equals("fluidcontainers")) return new int[][]{{120,24}};
        int[][] result=switch(display.outputs.size()) {
            case 1->new int[][]{{102,24}}; case 2->new int[][]{{102,24},{120,24}}; case 3->new int[][]{{102,24},{120,24},{138,24}};
            case 4->new int[][]{{102,15},{120,15},{102,33},{120,33}}; case 5->new int[][]{{102,15},{120,15},{102,33},{120,33},{138,24}};
            case 6->new int[][]{{102,6},{120,6},{102,24},{120,24},{102,42},{120,42}};
            case 7->new int[][]{{102,6},{120,6},{102,24},{120,24},{102,42},{120,42},{138,24}};
            case 8->new int[][]{{102,6},{120,6},{102,24},{120,24},{102,42},{120,42},{138,24},{138,42}};
            default->new int[display.outputs.size()][2];
        };
        int shift=assemblyOffset(category,display); for(var point:result) point[0]+=shift; return result;
    }
}
