package com.hbm.machine.fielddisturber;

import com.hbm.bomb.nuclear.NuclearExplosionMK3;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** The real X5846 MachineFieldDisturber is a scheduled ordinary iron block, not a powered BE or teleportation machine. */
public final class FieldDisturberBlock extends Block {
    public static final int SOURCE_TICK_RATE=10;
    public FieldDisturberBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5F,120F).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState previous,boolean moving){
        super.onPlace(state,level,pos,previous,moving);
        if(!level.isClientSide&&!previous.is(this))level.scheduleTick(pos,this,SOURCE_TICK_RATE);
    }
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){
        level.scheduleTick(pos,this,SOURCE_TICK_RATE);
        // Reuse the one source table consumed by statFacFleija and DfcCoreEntity.meltdown.
        // In particular, no other bomb/explosion protection, placement-time protection or remove-time deletion is invented.
        synchronized(NuclearExplosionMK3.at){
            NuclearExplosionMK3.at.put(new NuclearExplosionMK3.ATEntry(level.dimension(),pos.getX(),pos.getY(),pos.getZ()),level.getGameTime()+100);
        }
    }
}
