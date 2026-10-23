package com.hbm.machine.cargoelevator;

import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** The actual free-standing X5846 cargo lift; no invented energy or GUI machinery. */
public final class CargoElevatorContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<CargoElevatorBlock> BLOCK=BLOCKS.register("cargo_elevator",CargoElevatorBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("cargo_elevator",()->new BlockItem(BLOCK.get(),new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<CargoElevatorEntity>> ENTITY=ENTITIES.register("cargo_elevator",()->BlockEntityType.Builder.of(CargoElevatorEntity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM.get())):List.of();}
    public static Map<String,Item> indexedItems(){return Map.of("cargo_elevator",ITEM.get());}
    private CargoElevatorContent(){}
}
