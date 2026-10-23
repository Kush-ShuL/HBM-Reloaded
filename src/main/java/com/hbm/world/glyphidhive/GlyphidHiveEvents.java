package com.hbm.world.glyphidhive;
import com.hbm.entity.mob.glyphid.GlyphidConfig;
import com.hbm.entity.mob.glyphid.hive.EntityGlyphidScout;
import com.hbm.entity.mob.glyphid.special.EntityGlyphidDigger;
import com.hbm.pollution.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
/** Only the two actual original PollutionHandler Scout hooks; no parallel pollution system or per-tick scan. */
@EventBusSubscriber(modid="hbm")
public final class GlyphidHiveEvents {
    // Source is one non-persistent, non-dimension-keyed static coordinate, updated by sleep attempts.
    public static Vec3 targetCoords;
    @SubscribeEvent public static void sleep(CanPlayerSleepEvent event){if(GlyphidConfig.guidance()){var pos=event.getPos();targetCoords=new Vec3(pos.getX(),pos.getY(),pos.getZ());}}
    @SubscribeEvent public static void population(LevelEvent.PotentialSpawns event){
        if(!GlyphidConfig.naturalScouts()||event.isCanceled()||!(event.getLevel() instanceof ServerLevel world)||world.dimension()!=Level.OVERWORLD||!world.canSeeSky(event.getPos()))return;
        if(world.random.nextInt(GlyphidConfig.SCOUT_NATURAL_CHANCE.get())!=0)return;
        if(PollutionSystem.get(world,event.getPos(),PollutionType.SOOT)<GlyphidConfig.SCOUT_NATURAL_THRESHOLD.get())return;
        var pos=event.getPos();var scout=new EntityGlyphidScout(world);scout.moveTo(pos.getX(),pos.getY(),pos.getZ(),world.random.nextFloat()*360,0);
        if(scout.isValidLightLevel()){
            var digger=new EntityGlyphidDigger(world);
            // Both assignments are source: Scout consumes its second independent yaw before the escort yaw.
            scout.moveTo(pos.getX(),pos.getY(),pos.getZ(),world.random.nextFloat()*360,0);digger.moveTo(pos.getX(),pos.getY(),pos.getZ(),world.random.nextFloat()*360,0);
            if(scout.checkSpawnObstruction(world))world.addFreshEntity(scout);
            if(digger.checkSpawnObstruction(world))world.addFreshEntity(digger);
        }
    }
    private GlyphidHiveEvents(){}
}
