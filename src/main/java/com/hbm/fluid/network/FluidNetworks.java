package com.hbm.fluid.network;

import com.hbm.HbmReloaded;
import com.hbm.fluid.HbmFluidType;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public final class FluidNetworks {
    public static final BlockCapability<FluidUser, Direction> NATIVE_FLUID = BlockCapability.createSided(HbmReloaded.id("native_fluid"), FluidUser.class);
    private static final Map<ServerLevel, Map<HbmFluidType, FluidNodespace>> WORLDS = new IdentityHashMap<>();
    private static boolean registered;
    private FluidNetworks() {}
    public static void register(IEventBus bus) {
        if (registered) return; registered = true;
        bus.addListener(FluidNetworks::capabilities);
        NeoForge.EVENT_BUS.addListener(FluidNetworks::tick);
        NeoForge.EVENT_BUS.addListener(FluidNetworks::unload);
        NeoForge.EVENT_BUS.addListener(FluidNetworks::stopped);
    }
    public static FluidNodespace nodespace(ServerLevel level, HbmFluidType type) {
        return WORLDS.computeIfAbsent(level, ignored -> new IdentityHashMap<>()).computeIfAbsent(type, FluidNodespace::new);
    }
    /** Read-only lookup used by the optional standard-fluid view; never creates a network. */
    public static FluidNode nodeAt(ServerLevel level,HbmFluidType type,net.minecraft.core.BlockPos position) {
        var types=WORLDS.get(level);var space=types==null?null:types.get(type);return space==null?null:space.getNode(position);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        net.minecraft.core.registries.BuiltInRegistries.BLOCK_ENTITY_TYPE.entrySet().forEach(entry -> {
            if (!entry.getKey().location().getNamespace().equals("hbm")) return;
            event.registerBlockEntity(NATIVE_FLUID, entry.getValue(), (block, direction) -> block instanceof FluidUser user && user.isLoaded() ? user : null);
        });
    }
    private static void tick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) { var types = WORLDS.get(level); if (types != null) for (var space : types.values()) space.tick(); }
    }
    private static void unload(LevelEvent.Unload event) { if (event.getLevel() instanceof ServerLevel level) { var types = WORLDS.remove(level); if (types != null) types.values().forEach(FluidNodespace::clear); } }
    private static void stopped(ServerStoppedEvent event) { WORLDS.values().forEach(types -> types.values().forEach(FluidNodespace::clear)); WORLDS.clear(); }
}
