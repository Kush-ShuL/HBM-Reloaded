package com.hbm.compat.opencomputers;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/** Keeps the optional OC API outside the classes loaded by an installation without OC. */
public final class HbmOpenComputers {
    private HbmOpenComputers() {}

    public static void register(IEventBus bus) {
        if (ModList.get().isLoaded("opencomputers")) Loaded.register(bus);
    }

    private static final class Loaded {
        private static void register(IEventBus bus) {
            com.hbm.compat.opencomputers.machine.HbmMachineEnvironments.register(bus);
            bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(HbmOpenComputerItems::registerDrivers));
        }
    }
}
