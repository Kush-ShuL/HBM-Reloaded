package com.hbm.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Source vortex shrinks before the shared black-hole tick; its shrink rate is saved independently. */
public final class EntityVortex extends EntityBlackHole {
    public float shrinkRate=.0025F;
    public EntityVortex(EntityType<? extends EntityVortex> type,Level level){super(type,level);}
    public EntityVortex(Level level){this(BlackHoleContent.VORTEX.get(),level);}
    public EntityVortex(Level level,float size){this(level);setSize(size);}
    public EntityVortex setShrinkRate(float rate){shrinkRate=rate;return this;}
    @Override public void tick(){setSize(size()-shrinkRate);if(size()<=0){discard();return;}super.tick();}
    @Override protected void addAdditionalSaveData(CompoundTag tag){super.addAdditionalSaveData(tag);tag.putFloat("shrinkRate",shrinkRate);}
    @Override protected void readAdditionalSaveData(CompoundTag tag){super.readAdditionalSaveData(tag);shrinkRate=tag.getFloat("shrinkRate");}
}
