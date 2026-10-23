package com.hbm.machine.purex;
import com.hbm.recipe.generic.Blueprints;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.*;
import net.neoforged.neoforge.items.wrapper.*;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;
public final class PurexContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<PurexBlock> BLOCK=BLOCKS.register("machine_purex",PurexBlock::new);public static final DeferredItem<PurexItem> ITEM=ITEMS.register("machine_purex",PurexItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PurexBlockEntity>> ENTITY=ENTITIES.register("purex",()->BlockEntityType.Builder.of(PurexBlockEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PurexPort>> PORT=ENTITIES.register("purex_port",()->BlockEntityType.Builder.of(PurexPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<PurexMenu>> MENU=MENUS.register("purex",()->new MenuType<>((IContainerFactory<PurexMenu>)PurexMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<PurexRecipe>> TYPE=TYPES.register("purex",()->new RecipeType<PurexRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,PurexRecipe.Serializer> SERIALIZER=SERIALIZERS.register("purex",PurexRecipe.Serializer::new);
    public static final DeferredItem<com.hbm.bomb.nuclear.programmable.SourceNuclearWasteItem> WASTE_TINY=ITEMS.register("nuclear_waste_tiny",com.hbm.bomb.nuclear.programmable.SourceNuclearWasteItem::new);
    public static final DeferredItem<com.hbm.bomb.nuclear.programmable.SourceNuclearWasteItem> WASTE_VITRIFIED=ITEMS.register("nuclear_waste_vitrified",com.hbm.bomb.nuclear.programmable.SourceNuclearWasteItem::new);
    public static final DeferredItem<com.hbm.bomb.nuclear.programmable.SourceNuclearWasteItem> WASTE_VITRIFIED_TINY=ITEMS.register("nuclear_waste_vitrified_tiny",com.hbm.bomb.nuclear.programmable.SourceNuclearWasteItem::new);
    public static final DeferredItem<SchraraniumItem> SCHRARANIUM=ITEMS.register("ingot_schraranium",SchraraniumItem::new);
    private PurexContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);Blueprints.register(bus);Blueprints.declareRecipeSet(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("hbm","purex"),"PUREXRecipes");bus.addListener(PurexSelection::register);bus.addListener(PurexContent::capabilities);PurexRecipeConfig.register();}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));}
    public static List<ItemStack> creativeItems(){return List.of(new ItemStack(ITEM.get()));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?creativeItems():tab.equals("parts")?List.of(new ItemStack(WASTE_TINY.get()),new ItemStack(WASTE_VITRIFIED.get()),new ItemStack(WASTE_VITRIFIED_TINY.get()),new ItemStack(SCHRARANIUM.get())):List.of();}
}
