package com.hbm.machine.reactor.icf.pellet;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
/** Source full cube uses its top texture on both Y faces and its side texture on four sides. */
public final class IcfPelletPressBlock extends BaseEntityBlock {
 public IcfPelletPressBlock(){super(Properties.of().strength(5,60).sound(SoundType.STONE).requiresCorrectToolForDrops());}
 @Override protected MapCodec<? extends IcfPelletPressBlock> codec(){return MapCodec.unit(this);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new IcfPelletPressEntity(pos,state);}
 @Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,IcfPelletPressContent.ENTITY.get(),IcfPelletPressEntity::tick);}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos)instanceof IcfPelletPressEntity press)server.openMenu(press,pos);return InteractionResult.SUCCESS;}
 @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&level.getBlockEntity(pos)instanceof IcfPelletPressEntity press){Containers.dropContents(level,pos,press);level.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,level,pos,next,moving);}
}
