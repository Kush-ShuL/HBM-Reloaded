package com.hbm.entity.specialvehicle;

import com.hbm.damage.HbmDamageSources;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.registry.HbmSounds;
import com.hbm.weapon.specialvehicle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/** Original EntityDuchessGambit: five destructive explosions along Z followed by its actual boat block. */
public final class EntityDuchessGambit extends FallingVehicle {
    public EntityDuchessGambit(Level level){this(VehicleContent.BOAT_ENTITY.get(),level);}
    public EntityDuchessGambit(EntityType<?> type,Level level){super(type,level);}
    @Override protected double burstWidth(){return 5;}@Override protected double burstHeight(){return 7;}@Override protected double burstLength(){return 20;}
    @Override protected void impact(){
        discard();if(level().isClientSide)return;
        level().playSound(null,getX(),getY(),getZ(),HbmSounds.get("alarm.gambit"),SoundSource.NEUTRAL,10000F,1F);
        var damage=HbmDamageSources.source(level(),HbmDamageSources.boat);
        for(var entity:level().getEntities(null,new AABB(getX()-5,getY()-2,getZ()-9,getX()+5,getY()+2,getZ()+9)))entity.hurt(damage,1000F);
        for(int dz=-6;dz<=6;dz+=3)ExplosionLarge.explode(level(),getX(),getY(),getZ()+dz,2,true,false,false);
        level().setBlock(new BlockPos((int)(getX()-.5),(int)(getY()+.5),(int)(getZ()-.5)),VehicleContent.BOAT.get().defaultBlockState(),3);
        for(double strength:new double[]{3,2.5,2,1.5,1})VehicleShockEffects.shock(level(),getX(),getY()+1,getZ(),24,strength);
    }
}
