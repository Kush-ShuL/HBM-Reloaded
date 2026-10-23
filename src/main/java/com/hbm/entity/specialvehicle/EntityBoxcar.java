package com.hbm.entity.specialvehicle;

import com.hbm.damage.HbmDamageSources;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.specialvehicle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** Original EntityBoxcar: three shock rings, absolute 1000 damage and the real fallen DecoBlock. */
public final class EntityBoxcar extends FallingVehicle {
    public EntityBoxcar(Level level){this(VehicleContent.BOXCAR_ENTITY.get(),level);}
    public EntityBoxcar(EntityType<?> type,Level level){super(type,level);}
    @Override protected double burstWidth(){return 3;}@Override protected double burstHeight(){return 15;}@Override protected double burstLength(){return 3;}
    @Override protected void impact(){
        discard();if(level().isClientSide)return;
        level().playSound(null,getX(),getY(),getZ(),HbmSounds.get("weapon.trainImpact"),SoundSource.NEUTRAL,100F,1F);
        VehicleShockEffects.shock(level(),getX(),getY()+1,getZ(),24,3);VehicleShockEffects.shock(level(),getX(),getY()+1,getZ(),24,2.5);VehicleShockEffects.shock(level(),getX(),getY()+1,getZ(),24,2);
        var damage=HbmDamageSources.source(level(),HbmDamageSources.boxcar);
        for(var entity:level().getEntities(null,new AABB(getX()-2,getY()-2,getZ()-2,getX()+2,getY()+2,getZ()+2)))entity.hurt(damage,1000F);
        level().setBlock(BlockPos.containing(Math.floor(getX()),Math.floor(getY()+.5),Math.floor(getZ())),VehicleContent.BOXCAR.get().defaultBlockState(),3);
    }
}
