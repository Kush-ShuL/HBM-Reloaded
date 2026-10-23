package com.hbm.machine.reactorcontroller;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** Still actually registered in X5846, but explicitly hidden from its creative tabs. */
public final class ReactorControllerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<ReactorControllerBlock> BLOCK=BLOCKS.register("machine_controller",ReactorControllerBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ReactorControllerEntity>> ENTITY=ENTITIES.register("machine_controller",()->BlockEntityType.Builder.of(ReactorControllerEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<ReactorControllerMenu>> MENU=MENUS.register("machine_controller",()->new MenuType<>((IContainerFactory<ReactorControllerMenu>)ReactorControllerMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(ReactorControllerPackets::register);}
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(ITEM.get()));}
    private ReactorControllerContent(){}
}
