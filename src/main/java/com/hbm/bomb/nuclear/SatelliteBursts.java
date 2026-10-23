package com.hbm.bomb.nuclear;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** The real source SatelliteDetector burst repository and lifetime system; satellite inventory/commands bind to this same state. */
@EventBusSubscriber(modid="hbm")
public final class SatelliteBursts {
 public enum BurstIntensity{LOW,MEDIUM,HIGH}
 public static final int DURATION_LOW=15*20,DURATION_MEDIUM=20/2,DURATION_HIGH=60*20;
 public static final double INACCURACY_LOW=10_000,INACCURACY_MEDIUM=2_500,INARRCURACY_HIGH=500;
 public static List<RadiationBurst> bursts=new java.util.concurrent.CopyOnWriteArrayList<>();
 public static void reportEvent(Level world,int lifetime,BurstIntensity intensity,double x,double z){bursts.add(new RadiationBurst(world,lifetime,intensity,Mth.floor(x),Mth.floor(z)));}
 public static void updateSystem(Level world){bursts.removeIf(burst->world.dimension()==burst.dimension&&world.getGameTime()>burst.expiresOn);}
 @SubscribeEvent public static void tick(LevelTickEvent.Pre event){if(event.getLevel() instanceof ServerLevel level)updateSystem(level);}
 public static final class RadiationBurst {
  public ResourceKey<Level> dimension;public long expiresOn;public BurstIntensity intensity;public int x,z;
  public RadiationBurst(Level world,int lifetime,BurstIntensity intensity,int x,int z){dimension=world.dimension();expiresOn=world.getGameTime()+lifetime;this.intensity=intensity;this.x=x;this.z=z;double error=intensity==BurstIntensity.LOW?INACCURACY_LOW:intensity==BurstIntensity.MEDIUM?INACCURACY_MEDIUM:INARRCURACY_HIGH;this.x+=Mth.clamp(world.random.nextGaussian(),-1D,1D)*error;this.z+=Mth.clamp(world.random.nextGaussian(),-1D,1D)*error;}
 }
 private SatelliteBursts(){}
}
