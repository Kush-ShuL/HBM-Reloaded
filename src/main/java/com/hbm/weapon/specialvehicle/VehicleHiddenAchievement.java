package com.hbm.weapon.specialvehicle;

import com.hbm.damage.HbmDamageSources;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

/** Original ModEventHandler.onEntityDeath hidden achievement: actual tainted creeper only, all players within expanded50 AABB. */
@EventBusSubscriber(modid="hbm")
public final class VehicleHiddenAchievement {
    private static final ResourceLocation CREEPER=ResourceLocation.parse("hbm:entity_mob_tainted_creeper"),ADVANCEMENT=ResourceLocation.parse("hbm:hidden");
    @SubscribeEvent public static void death(LivingDeathEvent event){
        if(!(event.getEntity().level() instanceof ServerLevel level)||!event.getSource().is(HbmDamageSources.boxcar)||!CREEPER.equals(BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType())))return;
        var advancement=level.getServer().getAdvancements().get(ADVANCEMENT);if(advancement==null)return;
        for(var player:level.getPlayers(player->player.getBoundingBox().intersects(event.getEntity().getBoundingBox().inflate(50))))player.getAdvancements().award(advancement,"squash");
    }
    private VehicleHiddenAchievement(){}
}
