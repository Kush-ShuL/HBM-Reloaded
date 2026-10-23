package com.hbm.satellite;

import java.util.LinkedHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** The actual SatelliteRayScan location-keyed event repository; nuclear detector bursts are a separate source system. */
@EventBusSubscriber(modid="hbm")
public final class SatelliteRayEvents {
    public record Location(ResourceKey<Level> dimension,BlockPos position) {
        public Location { position=position.immutable(); }
    }
    public static final LinkedHashMap<Location,RayEvent> rayEvent=new LinkedHashMap<>();
    public static void reportEvent(Level world,int x,int y,int z,String info,int lifetime) {
        rayEvent.put(new Location(world.dimension(),new BlockPos(x,y,z)),new RayEvent(world,lifetime,x,z,info));
    }
    public static void updateSystem(Level world) {
        rayEvent.entrySet().removeIf(entry->entry.getKey().dimension().equals(world.dimension())&&world.getGameTime()>entry.getValue().expiresOn);
    }
    @SubscribeEvent public static void tick(LevelTickEvent.Post event) {
        if(event.getLevel() instanceof ServerLevel world&&world.getGameTime()%20==10)updateSystem(world);
    }
    public static final class RayEvent {
        public static final String INFO_ARC_FLASH="ARC_FLASH",INFO_NUCLEAR="NEUTRON_EMISSION",INFO_PARTICLE="HIGH_ENERGY_PARTICLES",INFO_RADAR="RADAR_WAVES",INFO_RADIO="RADIO_WAVES";
        public long expiresOn;
        public String info;
        public int x,z;
        public RayEvent(Level world,int lifetime,int x,int z,String info) {
            expiresOn=world.getGameTime()+lifetime;this.x=x;this.z=z;this.info=info;
        }
    }
    private SatelliteRayEvents(){}
}
