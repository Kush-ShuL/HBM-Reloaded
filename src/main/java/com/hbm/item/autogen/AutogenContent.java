package com.hbm.item.autogen;

import com.google.gson.Gson;
import com.hbm.material.HbmMaterials;
import com.hbm.material.MaterialShape;
import com.hbm.material.NtmMaterial;
import com.hbm.registry.HbmOreDictionary;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** 17 source ItemAutogen, true derived scraps, and one real registered item per custom shape. */
public final class AutogenContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<?>> RECIPES=DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER,"hbm");
    public static final net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>,net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<ScrapsCraftingRecipe>> SCRAPS_SPLIT=RECIPES.register("scraps_split",()->new net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<>(ScrapsCraftingRecipe::new));
    private static final Map<String,DeferredItem<AutogenItem>> ALL=new LinkedHashMap<>();
    private static final Map<String,List<Variant>> ORE_MEMBERS=new LinkedHashMap<>();
    private static boolean registered;
    private AutogenContent() {}
    public static void register(IEventBus bus) {
        if(registered) return; registered=true; HbmMaterials.initialize();
        try(var input=AutogenContent.class.getResourceAsStream("/hbm/autogen-items.json")) {
            if(input==null) throw new IllegalStateException("Missing autogen-items.json");
            var data=new Gson().fromJson(new InputStreamReader(input,StandardCharsets.UTF_8),Definitions.class);
            for(Spec spec:data.items()) add(spec,spec.shape()==null?null:HbmMaterials.shape(spec.shape()));
        } catch(java.io.IOException error) { throw new IllegalStateException(error); }
        for(MaterialShape shape:HbmMaterials.customShapes()) {
            String id=shape.name().toLowerCase(Locale.ROOT);
            add(new Spec(id,id,shape.symbol(),"ItemAutogen",shape.name().toLowerCase(Locale.ROOT),"item."+shape.name()+".name",Map.of(),false),shape);
        }
        ITEMS.register(bus); RECIPES.register(bus); SourceMaterialCraftingRecipes.register(); bus.addListener(AutogenContent::setup);
    }
    private static void add(Spec spec,MaterialShape shape) {
        if(ALL.containsKey(spec.id())) throw new IllegalArgumentException("Custom material shape item collides with existing item "+spec.id());
        ALL.put(spec.id(),ITEMS.register(spec.id(),()->spec.itemClass().equals("ItemScraps")?new ScrapsItem(spec):new AutogenItem(spec,shape)));
    }
    private static void setup(FMLCommonSetupEvent event) { event.enqueueWork(()->{ bindOreDictionary(); HbmMaterials.initializeDistributions(); }); }
    private static void bindOreDictionary() {
        for(NtmMaterial material:HbmMaterials.ordered()) for(var holder:ALL.values()) {
            AutogenItem item=holder.get(); MaterialShape shape=item.shape();
            if(shape==null || !material.autogen().contains(shape) || item.spec().oreRequiresSmeltable() && material.smeltingBehavior()!=NtmMaterial.SmeltingBehavior.SMELTABLE) continue;
            for(String name:material.names()) {
                String raw=shape.name()+name, tag=HbmOreDictionary.registerName(raw);
                ORE_MEMBERS.computeIfAbsent(tag,ignored->new ArrayList<>()).add(new Variant(item,material));
            }
        }
        // MatDistribution explicitly registers these original vanilla OreDictionary keys.
        String stone=HbmOreDictionary.registerName("stone"), cobblestone=HbmOreDictionary.registerName("cobblestone");
        HbmOreDictionary.addProvider(stack->stack.is(net.minecraft.world.level.block.Blocks.STONE.asItem())?List.of(stone):stack.is(net.minecraft.world.level.block.Blocks.COBBLESTONE.asItem())?List.of(cobblestone):List.of(),
                tag->tag.equals(stone)?Stream.of(new ItemStack(net.minecraft.world.level.block.Blocks.STONE)):tag.equals(cobblestone)?Stream.of(new ItemStack(net.minecraft.world.level.block.Blocks.COBBLESTONE)):Stream.empty());
        HbmOreDictionary.addProvider(stack->{
            if(!(stack.getItem() instanceof AutogenItem item)) return List.of();
            NtmMaterial material=HbmMaterials.byId(item.materialId(stack));
            if(material==null || item.shape()==null || !material.autogen().contains(item.shape()) || item.spec().oreRequiresSmeltable() && material.smeltingBehavior()!=NtmMaterial.SmeltingBehavior.SMELTABLE) return List.of();
            return material.names().stream().map(name->"hbm:legacy_ore_dict/"+(item.shape().name()+name).toLowerCase(Locale.ROOT)).toList();
        },tag->ORE_MEMBERS.getOrDefault(tag,List.of()).stream().map(variant->variant.material().make(variant.item())));
    }
    public static List<DeferredItem<AutogenItem>> all() { return List.copyOf(ALL.values()); }
    public static AutogenItem item(String id) { var holder=ALL.get(id); if(holder==null) throw new IllegalArgumentException("Unknown autogen item "+id); return holder.get(); }
    public static List<ItemStack> creativeStacks() { return ALL.values().stream().flatMap(holder->holder.get().creativeStacks().stream()).toList(); }
    private record Variant(AutogenItem item,NtmMaterial material) {}
    private record Definitions(List<Spec> items) {}
    public record Spec(String symbol,String id,String shape,@com.google.gson.annotations.SerializedName("class") String itemClass,String texture,String descriptionId,Map<String,String> textureOverrides,boolean oreRequiresSmeltable) {}
}
