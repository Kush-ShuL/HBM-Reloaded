package com.hbm.machine.electricpress;

import com.hbm.api.tool.ToolableBlock;
import com.hbm.multiblock.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Original one-wide three-tall electric press; hand drill removes only a clicked dummy,without tearing down the core. */
public final class ElectricPressBlock extends DummyableBlock implements ToolableBlock {
    public ElectricPressBlock(){super(Properties.of().mapColor(net.minecraft.world.level.material.MapColor.METAL).strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends ElectricPressBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{2,0,0,0,0,0};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ElectricPressEntity(pos,state):isPort(state)?new DummyInventoryProxy(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,ElectricPressContent.ELECTRIC_ENTITY.get(),PoweredPressEntity::tick);}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.HAND_DRILL||isCore(level.getBlockState(pos)))return false;if(!level.isClientSide)safely(()->level.removeBlock(pos,false));return true;}
    /** Original fixLegacyMonoblock promotes an existing orphan TE only; it does not invent new dummy blocks. */
    private boolean promoteLegacy(Level level,BlockPos pos,BlockState state){if(level.isClientSide||!(level.getBlockEntity(pos) instanceof ElectricPressEntity)||isCore(state))return false;int meta=state.getValue(LEGACY_META);var parent=pos.relative(Direction.from3DDataValue(meta%6).getOpposite());if(!level.hasChunksAt(pos.offset(-1,-1,-1),pos.offset(1,1,1))||level.getBlockState(parent).is(this))return false;safely(()->level.setBlock(pos,state.setValue(LEGACY_META,(meta+10)&15),UPDATE_ALL));return true;}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){if(!promoteLegacy(level,pos,state))super.neighborChanged(state,level,pos,neighbor,from,moving);}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){promoteLegacy(level,pos,state);}
}
