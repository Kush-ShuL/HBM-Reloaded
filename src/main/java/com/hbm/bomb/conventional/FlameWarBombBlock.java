package com.hbm.bomb.conventional;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.interfaces.IBomb;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Source BombFlameWar: all 150 dispersed damage-only blasts, followed by its real central VNT blast. */
public final class FlameWarBombBlock extends Block implements IBomb {
    public FlameWarBombBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,120).requiresCorrectToolForDrops());}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(level.hasNeighborSignal(pos))explode(level,pos.getX(),pos.getY(),pos.getZ());}
    @Override public BombReturnCode explode(Level level,int x,int y,int z){if(!level.isClientSide){level.destroyBlock(new BlockPos(x,y,z),false);
        for(int i=0;i<150;i++)new ExplosionVNT(level,x+level.random.nextInt(51)-25,y+level.random.nextInt(11)-5,z+level.random.nextInt(51)-25,4,null).setEntityProcessor(new EntityProcessorCrossSmooth(1,25)).setPlayerProcessor(new PlayerProcessorStandard()).setSFX(new ExplosionEffectTiny()).explode();
        new ExplosionVNT(level,x+.5,y+.5,z+.5,15).setBlockAllocator(new BlockAllocatorStandard(32)).setBlockProcessor(new BlockProcessorStandard().setNoDrop()).setEntityProcessor(new EntityProcessorCrossSmooth(2,200)).setPlayerProcessor(new PlayerProcessorStandard()).explode();LargeConventionalEffects.small(level,x+.5,y+.5,z+.5);
    }return BombReturnCode.DETONATED;}
}
