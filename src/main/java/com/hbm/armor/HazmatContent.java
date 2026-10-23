package com.hbm.armor;

import com.hbm.exposure.HbmExposure;
import com.hbm.registry.HbmContent;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The original basic yellow hazmat set, filters, cladding and modification table. */
public final class HazmatContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister<ArmorMaterial> MATERIALS=DeferredRegister.create(Registries.ARMOR_MATERIAL,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredHolder<ArmorMaterial,ArmorMaterial> MATERIAL=MATERIALS.register("hazmat",()->new ArmorMaterial(
        Map.of(ArmorItem.Type.HELMET,2,ArmorItem.Type.CHESTPLATE,5,ArmorItem.Type.LEGGINGS,4,ArmorItem.Type.BOOTS,1),5,
        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.EMPTY),()->Ingredient.of(HbmContent.item("hazmat_cloth")),List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath("hbm","hazmat"))),0,0));
    public static final DeferredItem<HazmatMaskItem> HELMET=ITEMS.register("hazmat_helmet",()->new HazmatMaskItem(MATERIAL));
    public static final DeferredItem<HazmatArmorItem> PLATE=ITEMS.register("hazmat_plate",()->new HazmatArmorItem(MATERIAL,ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<HazmatArmorItem> LEGS=ITEMS.register("hazmat_legs",()->new HazmatArmorItem(MATERIAL,ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<HazmatArmorItem> BOOTS=ITEMS.register("hazmat_boots",()->new HazmatArmorItem(MATERIAL,ArmorItem.Type.BOOTS));
    public static final DeferredItem<GasMaskFilterItem> FILTER=ITEMS.register("gas_mask_filter",GasMaskFilterItem::new);
    public static final DeferredItem<GasMaskFilterItem> FILTER_MONO=ITEMS.register("gas_mask_filter_mono",GasMaskFilterItem::new);
    public static final DeferredItem<GasMaskFilterItem> FILTER_COMBO=ITEMS.register("gas_mask_filter_combo",GasMaskFilterItem::new);
    public static final DeferredItem<GasMaskFilterItem> FILTER_RAG=ITEMS.register("gas_mask_filter_rag",GasMaskFilterItem::new);
    public static final DeferredItem<GasMaskFilterItem> FILTER_PISS=ITEMS.register("gas_mask_filter_piss",GasMaskFilterItem::new);
    public static final DeferredItem<ArmorCladdingItem> PAINT=ITEMS.register("cladding_paint",()->new ArmorCladdingItem(.025));
    public static final DeferredItem<ArmorCladdingItem> RUBBER=ITEMS.register("cladding_rubber",()->new ArmorCladdingItem(.005));
    public static final DeferredItem<ArmorCladdingItem> LEAD=ITEMS.register("cladding_lead",()->new ArmorCladdingItem(.1));
    public static final DeferredItem<ArmorCladdingItem> DESH=ITEMS.register("cladding_desh",()->new ArmorCladdingItem(.2));
    public static final DeferredItem<ArmorCladdingItem> GHIORSIUM=ITEMS.register("cladding_ghiorsium",()->new ArmorCladdingItem(.5));
    public static final DeferredBlock<ArmorTableBlock> TABLE=BLOCKS.register("machine_armor_table",ArmorTableBlock::new);
    public static final DeferredItem<BlockItem> TABLE_ITEM=ITEMS.registerSimpleBlockItem(TABLE);
    public static final DeferredHolder<MenuType<?>,MenuType<ArmorTableMenu>> MENU=MENUS.register("machine_armor_table",()->new MenuType<>(ArmorTableMenu::new,FeatureFlags.DEFAULT_FLAGS));
    private HazmatContent(){}
    public static void register(IEventBus bus){
        MATERIALS.register(bus);ITEMS.register(bus);BLOCKS.register(bus);MENUS.register(bus);
        bus.addListener(HazmatContent::setup);bus.addListener(HazmatContent::vanillaCreative);HazmatVariants.register(bus);
    }
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{HazmatRegistry.initialize();HbmExposure.addProtection(HazmatRegistry.INSTANCE);});}
    private static void vanillaCreative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey()==CreativeModeTabs.COMBAT){event.accept(HELMET.get());event.accept(PLATE.get());event.accept(LEGS.get());event.accept(BOOTS.get());}}
    public static List<ItemStack> creativeItems(String tab){return tab.equals("consumable")?List.of(new ItemStack(FILTER.get()),new ItemStack(FILTER_MONO.get()),new ItemStack(FILTER_COMBO.get()),new ItemStack(FILTER_RAG.get()),new ItemStack(FILTER_PISS.get()),new ItemStack(PAINT.get()),new ItemStack(RUBBER.get()),new ItemStack(LEAD.get()),new ItemStack(DESH.get()),new ItemStack(GHIORSIUM.get()),new ItemStack(TABLE_ITEM.get())):List.of();}
}
