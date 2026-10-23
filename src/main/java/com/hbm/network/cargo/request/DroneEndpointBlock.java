package com.hbm.network.cargo.request;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
public final class DroneEndpointBlock extends BaseEntityBlock {
 public final CargoRequestContent.Kind kind;private final Random drops=new Random();public DroneEndpointBlock(CargoRequestContent.Kind kind){super(Properties.of().strength(.1F,6).sound(SoundType.STONE));this.kind=kind;}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}@Override protected RenderShape getRenderShape(BlockState s){return RenderShape.MODEL;}@Override public BlockEntity newBlockEntity(BlockPos p,BlockState s){return switch(kind){case DOCK->new DroneDockEntity(p,s);case PROVIDER->new DroneProviderEntity(p,s);case REQUESTER->new DroneRequesterEntity(p,s);};}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level l,BlockState s,BlockEntityType<T> t){if(l.isClientSide)return null;return switch(kind){case DOCK->createTickerHelper(t,CargoRequestContent.DOCK_ENTITY.get(),(world,pos,state,tile)->tile.serverTick());case PROVIDER->createTickerHelper(t,CargoRequestContent.PROVIDER_ENTITY.get(),(world,pos,state,tile)->tile.networkTick());case REQUESTER->createTickerHelper(t,CargoRequestContent.REQUESTER_ENTITY.get(),(world,pos,state,tile)->tile.serverTick());};}
 @Override protected InteractionResult useWithoutItem(BlockState s,Level l,BlockPos p,Player player,BlockHitResult hit){if(l.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&l.getBlockEntity(p) instanceof RequestContainerEntity container)server.openMenu(container,p);return InteractionResult.CONSUME;}
 @Override protected void onRemove(BlockState s,Level l,BlockPos p,BlockState next,boolean moving){if(!s.is(next.getBlock())&&l.getBlockEntity(p) instanceof RequestContainerEntity tile){int start=kind==CargoRequestContent.Kind.REQUESTER?9:0,end=kind==CargoRequestContent.Kind.REQUESTER?18:9;if(!l.isClientSide)for(int i=start;i<end;i++){var stack=tile.getItem(i);if(stack.isEmpty())continue;float x=drops.nextFloat()*.8F+.1F,y=drops.nextFloat()*.8F+.1F,z=drops.nextFloat()*.8F+.1F;while(!stack.isEmpty()){var entity=new ItemEntity(l,p.getX()+x,p.getY()+y,p.getZ()+z,stack.split(Math.min(drops.nextInt(21)+10,stack.getCount())));entity.setDeltaMovement((float)drops.nextGaussian()*.05F,(float)drops.nextGaussian()*.05F+.2F,(float)drops.nextGaussian()*.05F);l.addFreshEntity(entity);}}l.updateNeighbourForOutputSignal(p,this);}super.onRemove(s,l,p,next,moving);}@Override protected List<ItemStack> getDrops(BlockState s,LootParams.Builder b){return List.of(new ItemStack(this));}
}
