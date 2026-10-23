package com.hbm.bomb.fissure;

import com.hbm.bomb.conventional.PrimedExplosive;
import com.hbm.bomb.conventional.TntExplosiveBlock;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

/** Real BlockFissureBomb: every inherited source TNT interaction uses the sole primed TNT entity. */
public final class FissureBombBlock extends TntExplosiveBlock {
    public FissureBombBlock(){super(0);}
    @Override public void explodeEntity(Level level,double x,double y,double z,PrimedExplosive primed){
        if(!(level instanceof ServerLevel))return;
        var pending=new FissureDetonation(FissureBombContent.PENDING.get(),level);
        pending.setPos(x,y,z);
        level.addFreshEntity(pending);
    }
}
