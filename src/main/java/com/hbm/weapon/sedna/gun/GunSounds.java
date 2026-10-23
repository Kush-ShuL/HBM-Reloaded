package com.hbm.weapon.sedna.gun;

import com.hbm.registry.HbmSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public final class GunSounds {
    public static void play(LivingEntity entity,String name,float volume,float pitch){
        if(entity.level().isClientSide)entity.level().playLocalSound(entity.getX(),entity.getY(),entity.getZ(),HbmSounds.get(name),SoundSource.PLAYERS,volume,pitch,false);
        else entity.level().playSound(null,entity.getX(),entity.getY(),entity.getZ(),HbmSounds.get(name),SoundSource.PLAYERS,volume,pitch);
    }
    private GunSounds(){}
}
