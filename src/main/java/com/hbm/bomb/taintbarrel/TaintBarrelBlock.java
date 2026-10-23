package com.hbm.bomb.taintbarrel;

import com.hbm.bomb.conventional.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/** Actual nonflammable RedBarrel identity; all source fuse/collision/shot/shape logic stays in the sole existing family. */
public final class TaintBarrelBlock extends HazardousBarrelBlock {
    public TaintBarrelBlock(){super(Kind.TAINT);}
    @Override protected MapCodec<? extends net.minecraft.world.level.block.Block> codec(){return MapCodec.unit(this);}
    @Override public void explodeEntity(Level level,double x,double y,double z,PrimedExplosive source){if(level instanceof ServerLevel server)TaintBarrelBurst.explode(server,x,y,z,source);}
}
