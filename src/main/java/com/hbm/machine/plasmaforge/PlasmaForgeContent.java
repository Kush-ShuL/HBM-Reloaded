package com.hbm.machine.plasmaforge;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
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
public final class PlasmaForgeContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<PlasmaForgeBlock> BLOCK=BLOCKS.register("fusion_plasma_forge",PlasmaForgeBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PlasmaForgeBlockEntity>> ENTITY=ENTITIES.register("fusion_plasma_forge",()->BlockEntityType.Builder.of(PlasmaForgeBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PlasmaForgePort>> PORT=ENTITIES.register("fusion_plasma_forge_port",()->BlockEntityType.Builder.of(PlasmaForgePort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<PlasmaForgeMenu>> MENU=MENUS.register("plasma_forge",()->new MenuType<>((IContainerFactory<PlasmaForgeMenu>)PlasmaForgeMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<PlasmaForgeRecipe>> TYPE=TYPES.register("plasma_forge",()->new RecipeType<PlasmaForgeRecipe>(){});
    public static final DeferredHolder<RecipeSerializer<?>,PlasmaForgeRecipe.Serializer> SERIALIZER=SERIALIZERS.register("plasma_forge",PlasmaForgeRecipe.Serializer::new);
    public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);bus.addListener(PlasmaForgeSelection::register);bus.addListener(PlasmaForgeContent::capabilities);bus.addListener(PlasmaForgeRecipeConfig::setup);PlasmaForgeRecipeConfig.register();}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->new SidedInvWrapper(machine,side==null?net.minecraft.core.Direction.DOWN:side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->new SidedInvWrapper(machine,side==null?net.minecraft.core.Direction.DOWN:side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private PlasmaForgeContent(){}
}
