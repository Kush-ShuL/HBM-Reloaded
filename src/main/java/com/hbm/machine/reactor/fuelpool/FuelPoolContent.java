package com.hbm.machine.reactor.fuelpool;

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
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** Actual X5846 machine_waste_drum, not the separate mesh-rendered storage drum. */
public final class FuelPoolContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    private static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,"hbm");
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredBlock<FuelPoolBlock> BLOCK=BLOCKS.register("machine_waste_drum",FuelPoolBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<FuelPoolBlockEntity>> ENTITY=ENTITIES.register("waste_drum",()->BlockEntityType.Builder.of(FuelPoolBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<FuelPoolMenu>> MENU=MENUS.register("waste_drum",()->new MenuType<>((IContainerFactory<FuelPoolMenu>)FuelPoolMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<RecipeType<?>,RecipeType<FuelPoolRecipe>> TYPE=TYPES.register("fuel_pool",()->new RecipeType<FuelPoolRecipe>(){});
    public static final DeferredHolder<RecipeSerializer<?>,FuelPoolRecipe.Serializer> SERIALIZER=SERIALIZERS.register("fuel_pool",FuelPoolRecipe.Serializer::new);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);TYPES.register(bus);SERIALIZERS.register(bus);FuelPoolRecipeConfig.register();bus.addListener(FuelPoolContent::capabilities);}
    private static void capabilities(RegisterCapabilitiesEvent event){event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,ENTITY.get(),(pool,side)->new SidedInvWrapper(pool,side==null?net.minecraft.core.Direction.DOWN:side));}
    public static ResourceLocation id(String path){return ResourceLocation.fromNamespaceAndPath("hbm",path);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private FuelPoolContent(){}
}
