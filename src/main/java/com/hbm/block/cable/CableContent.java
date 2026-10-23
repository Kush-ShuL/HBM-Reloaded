package com.hbm.block.cable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class CableContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    public static final EnumMap<CableKind, DeferredBlock<NativeCableBlock>> BLOCKS_BY_KIND = new EnumMap<>(CableKind.class);
    public static final EnumMap<CableKind, DeferredItem<CableBlockItem>> ITEMS_BY_KIND = new EnumMap<>(CableKind.class);
    static {
        for (CableKind kind : CableKind.values()) {
            var block = BLOCKS.register(kind.id(), () -> switch (kind) {
                case BOX -> new BoxCableBlock();
                case PAINTABLE -> new PaintableCableBlock();
                case GAUGE, DIODE -> new FacingCableBlock(kind);
                case SWITCH, DETECTOR -> new SwitchedCableBlock(kind);
                default -> new NativeCableBlock(kind);
            });
            BLOCKS_BY_KIND.put(kind, block);
            ITEMS_BY_KIND.put(kind, ITEMS.register(kind.id(), () -> new CableBlockItem(block.get())));
        }
    }
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<NativeCableBlockEntity>> ENTITY =
            ENTITIES.register("cable", () -> BlockEntityType.Builder.of(NativeCableBlockEntity::new,
                    BLOCKS_BY_KIND.entrySet().stream().filter(entry -> entry.getKey() != CableKind.GAUGE)
                            .map(entry -> entry.getValue().get()).toArray(NativeCableBlock[]::new)).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GaugeCableBlockEntity>> GAUGE_ENTITY =
            ENTITIES.register("cable_gauge", () -> BlockEntityType.Builder.of(GaugeCableBlockEntity::new,
                    BLOCKS_BY_KIND.get(CableKind.GAUGE).get()).build(null));
    private CableContent() {}
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); }
    public static List<ItemStack> creativeStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        for (CableKind kind : CableKind.values()) {
            if (kind == CableKind.DIODE) continue; // Deprecated and hidden in the original.
            if (kind == CableKind.BOX) for (int size = 0; size < 5; size++) stacks.add(ITEMS_BY_KIND.get(kind).get().stack(size));
            else stacks.add(new ItemStack(ITEMS_BY_KIND.get(kind).get()));
        }
        return List.copyOf(stacks);
    }
}
