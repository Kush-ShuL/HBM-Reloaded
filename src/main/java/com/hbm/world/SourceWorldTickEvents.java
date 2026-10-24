package com.hbm.world;

import com.hbm.entity.mob.fbi.FbiRaids;
import com.hbm.entity.mob.ghost.GhostEncounters;
import com.hbm.entity.mob.maskman.MaskManSpawn;
import com.hbm.entity.mob.radbeast.ElementalEncounters;
import com.hbm.machine.pedestal.PedestalEntries;
import com.hbm.satellite.SatelliteSavedData;
import com.hbm.world.meteor.MeteorScheduler;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Original world START sequence. Shared world RNG must not depend on content registration order. */
@EventBusSubscriber(modid = "hbm")
public final class SourceWorldTickEvents {
    @SubscribeEvent
    public static void beforeWorld(LevelTickEvent.Pre event) {
        if (!(event.getLevel() instanceof ServerLevel world)) return;
        PedestalEntries.expire(world);
        MaskManSpawn.update(world);
        FbiRaids.update(world);
        ElementalEncounters.update(world);
        MeteorScheduler.update(world);
        GhostEncounters.update(world);
        SatelliteSavedData.update(world);
    }

    private SourceWorldTickEvents() {}
}
