package com.hbm.machine.pyrooven;
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
public final class PyroOvenContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<PyroOvenBlock> BLOCK=BLOCKS.register("machine_pyrooven",PyroOvenBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PyroOvenEntity>> ENTITY=ENTITIES.register("pyrooven",()->BlockEntityType.Builder.of(PyroOvenEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<PyroOvenPort>> PORT=ENTITIES.register("pyrooven_port",()->BlockEntityType.Builder.of(PyroOvenPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<PyroOvenMenu>> MENU=MENUS.register("pyrooven",()->new MenuType<>((IContainerFactory<PyroOvenMenu>)PyroOvenMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<PyroOvenRecipe>> TYPE=TYPES.register("pyrooven",()->new RecipeType<PyroOvenRecipe>(){});public static final DeferredHolder<RecipeSerializer<?>,PyroOvenRecipe.Serializer> SERIALIZER=SERIALIZERS.register("pyrooven",PyroOvenRecipe.Serializer::new);
    private PyroOvenContent(){}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);PyroOvenRecipes.register();bus.addListener(PyroOvenContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(machine,side)->side==null?new InvWrapper(machine):new SidedInvWrapper(machine,side));event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,PORT.get(),(machine,side)->machine.inventory(side));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
}
