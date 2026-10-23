package com.hbm.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Source unstable vortex, including its unusual timer<=20 subtraction and client-side RNG branch. */
public final class EntityRagingVortex extends EntityBlackHole {
    int timer;
    public EntityRagingVortex(EntityType<? extends EntityRagingVortex> type,Level level){super(type,level);}
    public EntityRagingVortex(Level level){this(BlackHoleContent.RAGING_VORTEX.get(),level);}
    public EntityRagingVortex(Level level,float size){this(level);setSize(size);}
    @Override public void tick(){
        timer++;if(timer<=20)timer-=20;
        float pulse=(float)(Math.sin(timer)*Math.PI/20D)*.35F,decrement=0;
        if(random.nextInt(100)==0){decrement=.1F;level().explode(null,getX(),getY(),getZ(),10,false,Level.ExplosionInteraction.NONE);}
        setSize(size()-pulse-decrement);if(size()<=0){discard();return;}super.tick();
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putInt("vortexTimer",timer);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);timer=tag.getInt("vortexTimer");}
}
