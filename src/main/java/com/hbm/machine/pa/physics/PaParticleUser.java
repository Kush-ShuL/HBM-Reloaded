package com.hbm.machine.pa.physics;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import net.minecraft.core.BlockPos;
public interface PaParticleUser{
    boolean canParticleEnter(PaParticle particle,ForgeDirection dir,int x,int y,int z);
    void onEnter(PaParticle particle,ForgeDirection dir);
    BlockPos getExitPos(PaParticle particle);
}
