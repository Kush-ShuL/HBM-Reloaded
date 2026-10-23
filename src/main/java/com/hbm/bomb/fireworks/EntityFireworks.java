package com.hbm.bomb.fireworks;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Original rising, colliding entity; it is not a native fireworks rocket or a generic explosion. */
public final class EntityFireworks extends Entity {
    private int color,character;
    public EntityFireworks(EntityType<? extends EntityFireworks> type,Level world){super(type,world);}
    public EntityFireworks(Level world,double x,double y,double z,int color,int character){this(FireworksContent.BALL.get(),world);moveTo(x,y,z,0,0);this.color=color;this.character=character;}
    @Override protected void defineSynchedData(SynchedEntityData.Builder data){}
    @Override public void tick(){
        move(MoverType.SELF,new Vec3(0,3,0));
        level().addParticle(ParticleTypes.FLAME,getX(),getY(),getZ(),0,-.3,0);level().addParticle(ParticleTypes.SMOKE,getX(),getY(),getZ(),0,-.2,0);
        // The source adds this in addition to the real outer world tick increment.
        if(!level().isClientSide&&++tickCount>30){level().playSound(null,getX(),getY(),getZ(),SoundEvents.FIREWORK_ROCKET_BLAST,SoundSource.BLOCKS,20,1+random.nextFloat()*.2F);discard();FireworksEffects.burst(level(),getX(),getY(),getZ(),color,character);}
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag){tag.putInt("char",character);tag.putInt("color",color);tag.putInt("ticksExisted",tickCount);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){character=tag.getInt("char");color=tag.getInt("color");tickCount=tag.getInt("ticksExisted");}
}
