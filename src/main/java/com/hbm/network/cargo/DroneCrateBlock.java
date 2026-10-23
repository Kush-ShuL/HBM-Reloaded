package com.hbm.network.cargo;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
public final class DroneCrateBlock extends BaseEntityBlock {
 private static final Random DROP_RANDOM=new Random();public DroneCrateBlock(){super(Properties.of().strength(.1F,6).sound(SoundType.STONE));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}@Override protected RenderShape getRenderShape(BlockState s){return RenderShape.MODEL;}@Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return new DroneCrateEntity(p,s);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> type){return l.isClientSide?null:createTickerHelper(type,CargoDroneContent.CRATE_ENTITY.get(),(level,pos,state,tile)->tile.serverTick());}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(player.getMainHandItem().is(CargoDroneContent.LINKER.get()))return InteractionResult.PASS;if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof DroneCrateEntity crate)server.openMenu(crate,pos);return InteractionResult.CONSUME;}
 @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof DroneCrateEntity crate){if(!level.isClientSide)for(int i=0;i<crate.getContainerSize();i++){var stack=crate.getItem(i);if(stack.isEmpty())continue;float x=DROP_RANDOM.nextFloat()*.8F+.1F,y=DROP_RANDOM.nextFloat()*.8F+.1F,z=DROP_RANDOM.nextFloat()*.8F+.1F;while(!stack.isEmpty()){var split=stack.split(Math.min(DROP_RANDOM.nextInt(21)+10,stack.getCount()));var entity=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,split);entity.setDeltaMovement((float)DROP_RANDOM.nextGaussian()*.05F,(float)DROP_RANDOM.nextGaussian()*.05F+.2F,(float)DROP_RANDOM.nextGaussian()*.05F);level.addFreshEntity(entity);}}level.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,level,pos,next,moving);}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder p){return List.of(new ItemStack(this));}
}
