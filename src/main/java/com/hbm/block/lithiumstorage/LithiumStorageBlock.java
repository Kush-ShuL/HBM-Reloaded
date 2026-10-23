package com.hbm.block.lithiumstorage;

import com.hbm.block.fluid.LegacyMaterials;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Actual BlockLithium water reactivity and client rain smoke; source BlockBeaconable supplied by native beacon tag. */
public final class LithiumStorageBlock extends Block {
    public LithiumStorageBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.METAL).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    static boolean touchesWater(Level level,BlockPos pos){
        if(level.isClientSide)return false;
        for(Direction side:Direction.values())if(LegacyMaterials.of(level.getBlockState(pos.relative(side))).name().equals("water"))return true;
        return false;
    }
    static void react(Level level,BlockPos pos){
        level.destroyBlock(pos,false);
        // Original World.newExplosion(null,x+.5,y+.5,z+.5,15,false,true), not a nuclear warhead/config proxy.
        level.explode(null,pos.getX()+.5D,pos.getY()+.5D,pos.getZ()+.5D,15F,false,Level.ExplosionInteraction.BLOCK);
    }
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){
        if(touchesWater(level,pos))react(level,pos);
    }
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){
        if(level.isRainingAt(pos.above())){
            float x=random.nextFloat(),z=random.nextFloat();
            level.addParticle(ParticleTypes.LARGE_SMOKE,pos.getX()+x,pos.getY()+1,pos.getZ()+z,0,0,0);
        }
    }
}
