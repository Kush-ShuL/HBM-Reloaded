package com.hbm.compat.opencomputers.machine;

import com.hbm.api.computer.ComputerControl;
import com.hbm.compat.opencomputers.HbmOpenComputerCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/** Native adjacent-node integration for the existing machine/proxy ComputerControl contract. */
public final class HbmMachineEnvironments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "hbm");
    private static final Supplier<AttachmentType<MachineEnvironment>> ENVIRONMENT = ATTACHMENTS.register(
            "opencomputer_machine", () -> AttachmentType.serializable(MachineEnvironment::new).build());
    private static final Set<MachineEnvironment> ACTIVE = Collections.newSetFromMap(new IdentityHashMap<>());
    private static final Map<ServerLevel, Set<Long>> LOADED_CHUNKS = new ConcurrentHashMap<>();

    private HbmMachineEnvironments() {}

    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
        bus.addListener(HbmMachineEnvironments::capabilities);
        NeoForge.EVENT_BUS.addListener(HbmMachineEnvironments::tick);
        NeoForge.EVENT_BUS.addListener(HbmMachineEnvironments::chunkLoaded);
        NeoForge.EVENT_BUS.addListener(HbmMachineEnvironments::placed);
        NeoForge.EVENT_BUS.addListener(HbmMachineEnvironments::chunkUnloaded);
        NeoForge.EVENT_BUS.addListener(HbmMachineEnvironments::levelUnloaded);
        NeoForge.EVENT_BUS.addListener(HbmMachineEnvironments::stopped);
    }

    private static void capabilities(RegisterCapabilitiesEvent event) {
        BuiltInRegistries.BLOCK_ENTITY_TYPE.entrySet().forEach(entry -> {
            if (entry.getKey().location().getNamespace().equals("hbm")) registerType(event, entry.getValue());
        });
    }

    private static <T extends BlockEntity> void registerType(RegisterCapabilitiesEvent event, BlockEntityType<T> type) {
        event.registerBlockEntity(HbmOpenComputerCapabilities.ENVIRONMENT, type, HbmMachineEnvironments::environment);
        event.registerBlockEntity(HbmOpenComputerCapabilities.SIDED_ENVIRONMENT, type, HbmMachineEnvironments::environment);
    }

    private static MachineEnvironment environment(BlockEntity owner, Direction side) {
        MachineEnvironment environment = track(owner);
        if (environment == null) return null;
        ComputerControl control = (ComputerControl) owner;
        if (side == null ? MachineEnvironment.connectionMask(control) == 0 : !control.canComputerConnect(side)) return null;
        return environment;
    }

    private static MachineEnvironment track(BlockEntity owner) {
        if (!(owner instanceof ComputerControl) || !MachineEnvironment.available(owner)) return null;
        MachineEnvironment environment = owner.getData(ENVIRONMENT);
        if (ACTIVE.add(environment)) environment.activate();
        return environment;
    }

    private static void placed(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        Set<Long> chunks = LOADED_CHUNKS.computeIfAbsent(level, ignored -> ConcurrentHashMap.newKeySet());
        chunks.add(new ChunkPos(event.getPos()).toLong());
        if (event instanceof BlockEvent.EntityMultiPlaceEvent multiple) {
            for (var snapshot : multiple.getReplacedBlockSnapshots()) chunks.add(new ChunkPos(snapshot.getPos()).toLong());
        }
    }

    private static void chunkLoaded(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            // Load may run before FULL. Resolve block entities only from a subsequent server tick.
            LOADED_CHUNKS.computeIfAbsent(level, ignored -> ConcurrentHashMap.newKeySet()).add(event.getChunk().getPos().toLong());
        }
    }

    private static void tick(ServerTickEvent.Post event) {
        LOADED_CHUNKS.forEach((level, chunks) -> {
            if (level.getServer() != event.getServer()) return;
            for (long position : chunks) {
                var chunk = level.getChunkSource().getChunkNow(ChunkPos.getX(position), ChunkPos.getZ(position));
                if (chunk == null) continue;
                chunks.remove(position);
                // Ports can become connectable only after their multiblock has finished binding.
                for (BlockEntity owner : chunk.getBlockEntities().values()) track(owner);
            }
        });
        // Capability queries while joining can activate another environment.
        for (MachineEnvironment environment : List.copyOf(ACTIVE)) {
            if (environment.owner().getLevel() instanceof ServerLevel level && level.getServer() != event.getServer()) continue;
            if (!environment.tick()) {
                environment.release();
                ACTIVE.remove(environment);
            }
        }
    }

    private static void chunkUnloaded(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        long position = event.getChunk().getPos().toLong();
        Set<Long> pending = LOADED_CHUNKS.get(level);
        if (pending != null) pending.remove(position);
        release(level, position);
    }

    private static void levelUnloaded(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        LOADED_CHUNKS.remove(level);
        release(level, null);
    }

    private static void release(ServerLevel level, Long chunk) {
        ACTIVE.removeIf(environment -> {
            BlockEntity owner = environment.owner();
            if (owner.getLevel() != level || chunk != null && new ChunkPos(owner.getBlockPos()).toLong() != chunk) return false;
            environment.release();
            return true;
        });
    }

    private static void stopped(ServerStoppedEvent event) {
        for (MachineEnvironment environment : ACTIVE) environment.release();
        ACTIVE.clear();
        LOADED_CHUNKS.clear();
    }
}
