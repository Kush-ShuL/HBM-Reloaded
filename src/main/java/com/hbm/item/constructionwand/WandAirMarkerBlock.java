package com.hbm.item.constructionwand;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Actual BlockWand(Blocks.air), a persistent authoring marker, not an AIR substitute. */
public final class WandAirMarkerBlock extends Block {
    public final Block exportAs=Blocks.AIR;
    public WandAirMarkerBlock(){this(Properties.of().mapColor(MapColor.NONE).strength(0,0).sound(SoundType.STONE).noCollission().noOcclusion());}
    private WandAirMarkerBlock(Properties properties){super(properties);}
    @Override protected MapCodec<? extends WandAirMarkerBlock> codec(){return simpleCodec(WandAirMarkerBlock::new);}
    @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return box(1,1,1,15,15,15);}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}
    @Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    @Override public boolean skipRendering(BlockState state,BlockState neighbor,Direction side){return neighbor.is(this)||super.skipRendering(state,neighbor,side);}
}
