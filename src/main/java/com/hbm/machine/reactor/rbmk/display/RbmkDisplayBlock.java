package com.hbm.machine.reactor.rbmk.display;
import com.hbm.api.tool.ToolableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class RbmkDisplayBlock extends RbmkMiniPanelBlock implements ToolableBlock{
    @Override protected MapCodec<? extends RbmkDisplayBlock> codec(){return MapCodec.unit(this);}@Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new RbmkDisplayEntity(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,RbmkDisplayContent.ENTITY.get(),RbmkDisplayEntity::tick);}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER)return false;if(!level.isClientSide&&level.getBlockEntity(pos) instanceof RbmkDisplayEntity display)display.rotate();return true;}
}
