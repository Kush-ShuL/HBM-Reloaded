package com.hbm.machine.supercomputer;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class SuperComputerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<SuperComputerBlock> BLOCK=BLOCKS.register("machine_supercomputer",SuperComputerBlock::new);public static final DeferredItem<net.minecraft.world.item.BlockItem> ITEM=ITEMS.register("machine_supercomputer",()->new net.minecraft.world.item.BlockItem(BLOCK.get(),new net.minecraft.world.item.Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SuperComputerEntity>> ENTITY=ENTITIES.register("machine_supercomputer",()->BlockEntityType.Builder.of(SuperComputerEntity::new,BLOCK.get()).build(null));public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SuperComputerPort>> PORT=ENTITIES.register("machine_supercomputer_port",()->BlockEntityType.Builder.of(SuperComputerPort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<SuperComputerMenu>> MENU=MENUS.register("machine_supercomputer",()->new MenuType<>((IContainerFactory<SuperComputerMenu>)SuperComputerMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){SuperComputerRecipes.register(bus);BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(SuperComputerSelection::register);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private SuperComputerContent(){}
}
