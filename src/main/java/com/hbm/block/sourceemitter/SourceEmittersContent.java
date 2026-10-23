package com.hbm.block.sourceemitter;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.*;

public final class SourceEmittersContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    static final Set<EmitterEntity> OPEN_SCANS = Collections.newSetFromMap(new WeakHashMap<>());
    public static final DeferredBlock<EmitterBlock> LIGHT = BLOCKS.register("deco_emitter", EmitterBlock::new);
    public static final DeferredBlock<PartEmitterBlock> PARTICLE = BLOCKS.register("part_emitter", PartEmitterBlock::new);
    public static final DeferredItem<EmitterItem> LIGHT_ITEM = ITEMS.register("deco_emitter", () -> new EmitterItem(LIGHT.get(), true));
    public static final DeferredItem<EmitterItem> PARTICLE_ITEM = ITEMS.register("part_emitter", () -> new EmitterItem(PARTICLE.get(), false));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EmitterEntity>> LIGHT_ENTITY =
            ENTITIES.register("deco_emitter", () -> BlockEntityType.Builder.of(EmitterEntity::new, LIGHT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PartEmitterEntity>> PARTICLE_ENTITY =
            ENTITIES.register("part_emitter", () -> BlockEntityType.Builder.of(PartEmitterEntity::new, PARTICLE.get()).build(null));
    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus);
        NeoForge.EVENT_BUS.addListener(SourceEmittersContent::unload);
        NeoForge.EVENT_BUS.addListener(SourceEmittersContent::stopped);
    }
    private static void unload(LevelEvent.Unload event) { if (!(event.getLevel() instanceof ServerLevel)) return; for (var emitter : List.copyOf(OPEN_SCANS)) if (emitter.getLevel() == event.getLevel()) emitter.closeScan(); }
    private static void stopped(ServerStoppedEvent event) { for (var emitter : List.copyOf(OPEN_SCANS)) emitter.closeScan(); }
    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("block") ? List.of(new ItemStack(LIGHT_ITEM.get()), new ItemStack(PARTICLE_ITEM.get())) : List.of();
    }
    public static Map<String, Item> indexedItems() { return Map.of("deco_emitter", LIGHT_ITEM.get(), "part_emitter", PARTICLE_ITEM.get()); }
    private SourceEmittersContent() { }
}
