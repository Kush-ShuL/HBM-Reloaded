package com.hbm.machine.turbine.quartz;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Original BlockNTMGlassCT(0,quartz,packedIce,true): solid collision, no melting, self drop without silk. */
public final class QuartzGlassBlock extends HalfTransparentBlock {
    private static final MapCodec<QuartzGlassBlock> CODEC=simpleCodec(QuartzGlassBlock::new);
    public QuartzGlassBlock(){this(Properties.of().mapColor(MapColor.ICE).strength(1,24).sound(SoundType.GLASS).noOcclusion().isViewBlocking((state,world,pos)->false).isSuffocating((state,world,pos)->false).isValidSpawn((state,world,pos,type)->false));}
    public QuartzGlassBlock(Properties properties){super(properties);}
    @Override public MapCodec<? extends HalfTransparentBlock> codec(){return CODEC;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter world,BlockPos pos){return true;}
    @Override protected float getShadeBrightness(BlockState state,BlockGetter world,BlockPos pos){return 1F;}
}
