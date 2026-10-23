package com.hbm.entity.effect;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** The original digamma quasar uses the complete shared black-hole behavior and its own disc renderer. */
public final class EntityQuasar extends EntityBlackHole {
    public EntityQuasar(EntityType<? extends EntityQuasar> type,Level level){super(type,level);}
    public EntityQuasar(Level level){this(BlackHoleContent.QUASAR.get(),level);}
    public EntityQuasar(Level level,float size){this(level);setSize(size);}
}
