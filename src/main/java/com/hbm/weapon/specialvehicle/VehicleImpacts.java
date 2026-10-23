package com.hbm.weapon.specialvehicle;

import com.hbm.entity.specialvehicle.*;
import com.hbm.registry.HbmSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Original XFactory44 / XFactory12ga secret impacts, independent of the gun implementation. */
public final class VehicleImpacts {
    public static void spawnBoxcar(Level level,Vec3 hit){if(level.isClientSide)return;var entity=new EntityBoxcar(level);entity.setPos(hit.x,hit.y+50,hit.z);level.addFreshEntity(entity);level.playSound(null,entity.getX(),entity.getY()+50,entity.getZ(),HbmSounds.get("alarm.trainHorn"),SoundSource.NEUTRAL,100F,1F);}
    public static void spawnBoat(Level level,Vec3 hit){if(level.isClientSide)return;var entity=new EntityDuchessGambit(level);entity.setPos(hit.x,hit.y+50,hit.z);level.addFreshEntity(entity);level.playSound(null,entity.getX(),entity.getY()+50,entity.getZ(),HbmSounds.get("weapon.boat"),SoundSource.NEUTRAL,100F,1F);}
    private VehicleImpacts(){}
}
