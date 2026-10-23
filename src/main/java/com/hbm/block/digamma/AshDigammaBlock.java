package com.hbm.block.digamma;

import com.hbm.block.falling.*;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Original BlockAshes visual counter atop the already shared true1.7 falling kernel. */
public final class AshDigammaBlock extends LegacyFallingBlock {
 public AshDigammaBlock(){super(new FallingSpec("ash_digamma","BlockAshes","sand","sand","ash_digamma",.5F,90,false,false));}
 @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){if(random.nextInt(25)==0)AshVisuals.ACCUMULATE.run();}
}
