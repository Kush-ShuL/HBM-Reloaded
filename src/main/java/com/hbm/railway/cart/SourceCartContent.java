package com.hbm.railway.cart;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.DispenserBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class SourceCartContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredItem<SourceCartItem> CART=ITEMS.register("cart",SourceCartItem::new);
    public static final DeferredHolder<EntityType<?>,EntityType<SourceEmptyCart>> EMPTY=ENTITIES.register("entity_ntm_cart_ore",()->cart(SourceEmptyCart::new,"entity_ntm_cart_ore"));
    public static final DeferredHolder<EntityType<?>,EntityType<SourceCrateCart>> CRATE=ENTITIES.register("entity_ntm_cart_crate",()->cart(SourceCrateCart::new,"entity_ntm_cart_crate"));
    public static final DeferredHolder<EntityType<?>,EntityType<SourceDestroyerCart>> DESTROYER=ENTITIES.register("entity_ntm_cart_destroyer",()->cart(SourceDestroyerCart::new,"entity_ntm_cart_destroyer"));
    public static final DeferredHolder<EntityType<?>,EntityType<SourcePowderCart>> POWDER=ENTITIES.register("entity_ntm_cart_powder",()->cart(SourcePowderCart::new,"entity_ntm_cart_powder"));
    public static final DeferredHolder<EntityType<?>,EntityType<SourceSemtexCart>> SEMTEX=ENTITIES.register("entity_ntm_cart_semtex",()->cart(SourceSemtexCart::new,"entity_ntm_cart_semtex"));
    private static <T extends SourceCartEntity> EntityType<T> cart(EntityType.EntityFactory<T> factory,String id){return EntityType.Builder.of(factory,MobCategory.MISC).sized(.98F,.7F).clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:"+id);}
    public static final DeferredHolder<MenuType<?>,MenuType<CrateCartMenu>> CRATE_MENU=MENUS.register("source_crate_cart",()->new MenuType<>((IContainerFactory<CrateCartMenu>)CrateCartMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>,MenuType<DestroyerCartMenu>> DESTROYER_MENU=MENUS.register("source_destroyer_cart",()->new MenuType<>((IContainerFactory<DestroyerCartMenu>)DestroyerCartMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeSerializer<?>,SourceCartShapedRecipe.Serializer> COPY_RECIPE=RECIPES.register("source_cart_shaped",SourceCartShapedRecipe.Serializer::new);
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static List<ItemStack> indexedItems(){var list=new ArrayList<ItemStack>();for(var kind:SourceCartKind.values())for(var base:SourceCartKind.Base.values())if(kind.supports(base))list.add(SourceCartItem.stack(base,kind));return list;}
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))indexedItems().forEach(event::accept);}
    private static void common(FMLCommonSetupEvent event){event.enqueueWork(()->DispenserBlock.registerBehavior(CART.get(),new SourceCartItem.Dispense()));}
    public static void register(IEventBus bus){ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);RECIPES.register(bus);bus.addListener(SourceCartContent::common);bus.addListener(SourceCartContent::creative);}
    private SourceCartContent(){}
}
