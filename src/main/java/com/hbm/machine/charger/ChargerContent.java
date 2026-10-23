package com.hbm.machine.charger;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class ChargerContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<ChargerBlock> BLOCK=BLOCKS.register("charger",ChargerBlock::new);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<ChargerBlockEntity>> ENTITY=ENTITIES.register("charger",()->BlockEntityType.Builder.of(ChargerBlockEntity::new,BLOCK.get()).build(null));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(ITEM.get())):List.of();}
    private ChargerContent(){}
}
