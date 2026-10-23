package com.hbm.block.masonry;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;

/** Original BlockNTMLadder inherits the 1.7.10 two-pixel collision thickness. */
public final class LegacyLadderBlock extends LadderBlock {
    private static final MapCodec<LadderBlock> CODEC=simpleCodec(LegacyLadderBlock::new);
    public LegacyLadderBlock() { this(Properties.of().strength(.25F,1.2F).sound(SoundType.STONE).noOcclusion()); }
    public LegacyLadderBlock(Properties properties) { super(properties); }
    @Override public MapCodec<LadderBlock> codec() { return CODEC; }
    public BlockState fromLegacy(int metadata) { return defaultBlockState().setValue(FACING,switch(metadata) { case 2->Direction.NORTH; case 3->Direction.SOUTH; case 4->Direction.WEST; case 5->Direction.EAST; default->throw new IllegalArgumentException("Invalid source ladder metadata: "+metadata); }); }
    @Override protected VoxelShape getShape(BlockState state,BlockGetter world,BlockPos pos,CollisionContext context) { return switch(state.getValue(FACING)) { case NORTH->box(0,0,14,16,16,16); case SOUTH->box(0,0,0,16,16,2); case WEST->box(14,0,0,16,16,16); default->box(0,0,0,2,16,16); }; }
}
