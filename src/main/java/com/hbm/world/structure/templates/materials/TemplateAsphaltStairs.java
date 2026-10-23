package com.hbm.world.structure.templates.materials;
import com.hbm.block.masonry.MasonryContent;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
/** Source BlockSpeedyStairs(asphalt,0,1.5), retaining parent strength and the original absence of waterlogging. */
public final class TemplateAsphaltStairs extends StairBlock {
    public TemplateAsphaltStairs(){super(MasonryContent.ASPHALT.get().defaultBlockState(),Properties.ofFullCopy(MasonryContent.ASPHALT.get()).noOcclusion());}
    @Override public MapCodec<? extends StairBlock> codec(){return MapCodec.unit(this);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return super.getStateForPlacement(context).setValue(WATERLOGGED,false);}
    @Override public boolean canPlaceLiquid(Player player,BlockGetter level,BlockPos pos,BlockState state,Fluid fluid){return false;}
    @Override public boolean placeLiquid(LevelAccessor level,BlockPos pos,BlockState state,FluidState fluid){return false;}
}
