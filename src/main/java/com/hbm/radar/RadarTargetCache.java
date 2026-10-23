package com.hbm.radar;
import api.hbm.entity.IRadarDetectableNT;import java.util.*;import net.minecraft.world.entity.Entity;import net.minecraft.world.entity.player.Player;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.event.tick.ServerTickEvent;import net.neoforged.neoforge.event.server.ServerStoppedEvent;
/** Original RadarNT.updateSystem server-START global loaded entity cache, shared by ABM and future radar. */
@EventBusSubscriber(modid="hbm") public final class RadarTargetCache {
 private static final List<Class<?>> classes=new ArrayList<>(List.of(IRadarDetectableNT.class,api.hbm.entity.IRadarDetectable.class,Player.class));private static List<Entity> matching=List.of();public static List<Entity> matchingEntities(){return matching;}public static void register(Class<?> type){if(!classes.contains(type))classes.add(type);}
 @SubscribeEvent public static void tick(ServerTickEvent.Pre event){var result=new ArrayList<Entity>();for(var world:event.getServer().getAllLevels())for(var entity:world.getAllEntities())for(var type:classes)if(type.isAssignableFrom(entity.getClass())){result.add(entity);break;}matching=List.copyOf(result);}@SubscribeEvent public static void stopped(ServerStoppedEvent event){matching=List.of();}private RadarTargetCache(){}
}
