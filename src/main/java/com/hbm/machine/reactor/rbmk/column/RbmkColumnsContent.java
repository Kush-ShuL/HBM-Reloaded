package com.hbm.machine.reactor.rbmk.column;
import com.hbm.machine.reactor.rbmk.core.RbmkNeutronField;
import java.util.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class RbmkColumnsContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE, "hbm");
    public static final EnumMap<RbmkColumnKind, DeferredBlock<RbmkPassiveBlock>> COLUMNS = new EnumMap<>(RbmkColumnKind.class);
    public static final EnumMap<RbmkColumnKind, DeferredItem<BlockItem>> COLUMN_ITEMS = new EnumMap<>(RbmkColumnKind.class);
    static { for (var kind : RbmkColumnKind.values()) { var block = BLOCKS.register(kind.id, () -> new RbmkPassiveBlock(kind)); COLUMNS.put(kind, block); COLUMN_ITEMS.put(kind, ITEMS.registerSimpleBlockItem(block)); } }
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RbmkPassiveEntity>> PASSIVE = ENTITIES.register("rbmk_passive", () -> BlockEntityType.Builder.of(RbmkPassiveEntity::new, COLUMNS.values().stream().map(DeferredBlock::get).toArray(net.minecraft.world.level.block.Block[]::new)).build(null));
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); RbmkNeutronField.register(); }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("machine") ? COLUMN_ITEMS.values().stream().map(item -> new ItemStack(item.get())).toList() : List.of(); }
    private RbmkColumnsContent() { }
}
