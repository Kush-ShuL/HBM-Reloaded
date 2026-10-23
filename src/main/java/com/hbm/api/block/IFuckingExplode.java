package com.hbm.api.block;

import com.hbm.bomb.conventional.PrimedExplosive;
import net.minecraft.world.level.Level;

/** Original EntityTNTPrimedBase callback; distinct from the remote-trigger protocol. */
public interface IFuckingExplode {
    void explodeEntity(Level level, double x, double y, double z, PrimedExplosive entity);
}
