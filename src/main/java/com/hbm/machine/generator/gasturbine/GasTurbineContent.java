package com.hbm.machine.generator.gasturbine;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

public final class GasTurbineContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,"hbm");
    public static final DeferredBlock<GasTurbineBlock> BLOCK=BLOCKS.register("machine_turbinegas",GasTurbineBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<GasTurbineBlockEntity>> ENTITY=ENTITIES.register("machine_gasturbine",()->BlockEntityType.Builder.of(GasTurbineBlockEntity::new,BLOCK.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<GasTurbinePort>> PORT=ENTITIES.register("gasturbine_port",()->BlockEntityType.Builder.of(GasTurbinePort::new,BLOCK.get()).build(null));
    public static final DeferredHolder<MenuType<?>,MenuType<GasTurbineMenu>> MENU=MENUS.register("machine_gasturbine",()->new MenuType<>((IContainerFactory<GasTurbineMenu>)GasTurbineMenu::new,FeatureFlags.DEFAULT_FLAGS));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);MENUS.register(bus);bus.addListener(GasTurbineControl::register);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    private GasTurbineContent(){}
}
