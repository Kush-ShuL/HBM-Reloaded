package com.hbm.machine.reactor.rbmk.console;
import java.util.*;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class RbmkConsoleContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<RbmkConsoleBlock> BLOCK=BLOCKS.register("rbmk_console",RbmkConsoleBlock::new);public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);public static final DeferredItem<RbmkLinkerItem> TOOL=ITEMS.register("rbmk_tool",RbmkLinkerItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<RbmkConsoleEntity>> ENTITY=ENTITIES.register("rbmk_console",()->BlockEntityType.Builder.of(RbmkConsoleEntity::new,BLOCK.get()).build(null));
    public static Consumer<BlockPos> OPEN=pos->{};
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);RbmkConsoleNetwork.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){return switch(tab){case "machine"->List.of(new ItemStack(ITEM.get()));case "consumable"->List.of(new ItemStack(TOOL.get()));default->List.of();};}private RbmkConsoleContent(){}
}
