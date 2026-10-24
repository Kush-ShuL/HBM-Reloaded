package com.hbm.item.autogen;

import com.google.gson.Gson;
import com.hbm.material.HbmMaterials;
import com.hbm.material.MaterialShape;
import com.hbm.material.NtmMaterial;
import com.hbm.material.resources.GeneratedMaterialResources;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Configured materials are fixed physical identities; enablement controls the original display/tag domain. */
public final class AutogenContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<?>> RECIPES=DeferredRegister.create(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER,"hbm");
    public static final net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>,net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<ScrapsCraftingRecipe>> SCRAPS_SPLIT=RECIPES.register("scraps_split",()->new net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer<>(ScrapsCraftingRecipe::new));
    private static final Map<String,Family> FAMILIES=new LinkedHashMap<>();
    private static final List<DeferredItem<AutogenItem>> ALL=new ArrayList<>();
    private static final List<Identity> IDENTITIES=new ArrayList<>();
    private static boolean registered;
    private AutogenContent() {}
    public static void register(IEventBus bus) {
        if(registered) return; registered=true; HbmMaterials.initialize();
        try(var input=AutogenContent.class.getResourceAsStream("/hbm/autogen-items.json")) {
            if(input==null) throw new IllegalStateException("Missing autogen-items.json");
            var data=new Gson().fromJson(new InputStreamReader(input,StandardCharsets.UTF_8),Definitions.class);
            for(Spec spec:data.items()) add(spec,spec.shape()==null?null:HbmMaterials.shape(spec.shape()),false);
        } catch(java.io.IOException error) { throw new IllegalStateException(error); }
        for(MaterialShape shape:HbmMaterials.customShapes()) {
            String id=shape.name().toLowerCase(Locale.ROOT);
            add(new Spec(id,id,shape.symbol(),"ItemAutogen",shape.name().toLowerCase(Locale.ROOT),"item."+shape.name()+".name",Map.of(),false),shape,true);
        }
        ITEMS.register(bus); RECIPES.register(bus); SourceMaterialCraftingRecipes.register();
        bus.addListener(AutogenContent::setup);
        GeneratedMaterialResources.contribute(PackType.CLIENT_RESOURCES,AutogenContent::models);
        GeneratedMaterialResources.register(bus);
    }
    private static void add(Spec spec,MaterialShape shape,boolean custom) {
        if(FAMILIES.containsKey(spec.id())) throw new IllegalArgumentException("Custom material shape item collides with existing family "+spec.id());
        Family family=new Family(spec,shape); FAMILIES.put(spec.id(),family);
        for(NtmMaterial material:HbmMaterials.ordered()) {
            String path="autogen/"+(custom?"custom/":"")+spec.id()+"/m"+material.id();
            var holder=ITEMS.register(path,()->spec.itemClass().equals("ItemScraps")?new ScrapsItem(spec,material):new AutogenItem(spec,shape,material));
            family.items.put(material.id(),holder); ALL.add(holder);
            boolean creative=spec.itemClass().equals("ItemScraps")
                    ?material.smeltingBehavior()==NtmMaterial.SmeltingBehavior.SMELTABLE || material.smeltingBehavior()==NtmMaterial.SmeltingBehavior.ADDITIVE
                    :material.autogen().contains(shape);
            boolean sourceOreMember=shape!=null && material.autogen().contains(shape)
                    && (!spec.oreRequiresSmeltable() || material.smeltingBehavior()==NtmMaterial.SmeltingBehavior.SMELTABLE);
            IDENTITIES.add(new Identity(holder.getId(),spec.id(),shape,material,sourceOreMember,creative));
        }
    }
    private static void setup(FMLCommonSetupEvent event) { event.enqueueWork(HbmMaterials::initializeDistributions); }
    public static List<DeferredItem<AutogenItem>> all() { return List.copyOf(ALL); }
    public static Family item(String family) { var value=FAMILIES.get(family); if(value==null) throw new IllegalArgumentException("Unknown autogen family "+family); return value; }
    public static List<Identity> identities() { return List.copyOf(IDENTITIES); }
    public static ResourceLocation orderAnchor(AutogenItem item) { return ResourceLocation.fromNamespaceAndPath("hbm","autogen/"+(item.shape()!=null && item.shape().symbol().startsWith("custom:")?"custom/":"")+item.spec().id()+"/m0"); }
    public static List<ItemStack> creativeStacks() { return ALL.stream().flatMap(holder->holder.get().creativeStacks().stream()).toList(); }
    public static List<ItemStack> viewerHiddenStacks() { return ALL.stream().map(DeferredItem::get).filter(item->item.creativeStacks().isEmpty()).map(ItemStack::new).toList(); }
    private static Map<ResourceLocation,byte[]> models() {
        var resources=new LinkedHashMap<ResourceLocation,byte[]>();
        for(var family:FAMILIES.values()) for(var item:family.items.values()) {
            var value=Map.of("parent","minecraft:item/generated","textures",Map.of("layer0","hbm:item/"+family.spec.texture()));
            resources.put(item.getId().withPath(path->"models/item/"+path+".json"),new Gson().toJson(value).getBytes(StandardCharsets.UTF_8));
        }
        return resources;
    }
    /** Typed source registration eligibility, not a second membership registry or an expanded material tag. */
    public record Identity(ResourceLocation itemId,String family,MaterialShape shape,NtmMaterial material,boolean sourceOreMember,boolean creativeVisible) {}
    /** An internal factory handle, never a registered item or a decoder for saved stacks. */
    public static final class Family {
        private final Spec spec;
        private final MaterialShape shape;
        private final Map<Integer,DeferredItem<AutogenItem>> items=new LinkedHashMap<>();
        private Family(Spec spec,MaterialShape shape) { this.spec=spec; this.shape=shape; }
        public Spec spec() { return spec; }
        public MaterialShape shape() { return shape; }
        public ItemStack stack(NtmMaterial material,int count) {
            var holder=items.get(material.id());
            if(holder==null) throw new IllegalArgumentException("Material is not registered: "+material.id());
            return new ItemStack(holder.get(),count);
        }
    }
    private record Definitions(List<Spec> items) {}
    public record Spec(String symbol,String id,String shape,@com.google.gson.annotations.SerializedName("class") String itemClass,String texture,String descriptionId,Map<String,String> textureOverrides,boolean oreRequiresSmeltable) {}
}
