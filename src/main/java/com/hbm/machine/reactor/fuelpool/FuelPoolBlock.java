package com.hbm.machine.reactor.fuelpool;

import com.hbm.machine.storage.CrateContents;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.BlockHitResult;

/** The source full opaque cube, top/bottom waste_drum and four waste_drum_side faces. */
public final class FuelPoolBlock extends BaseEntityBlock {
    public FuelPoolBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new FuelPoolBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,FuelPoolContent.ENTITY.get(),(world,pos,current,pool)->pool.tick());}
    private InteractionResult open(Level level,BlockPos pos,Player player){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof FuelPoolBlockEntity pool)server.openMenu(pool,buffer->buffer.writeBlockPos(pos));return InteractionResult.CONSUME;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return open(level,pos,player);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){return open(level,pos,player)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);var name=stack.get(net.minecraft.core.component.DataComponents.CUSTOM_NAME);if(name!=null&&level.getBlockEntity(pos) instanceof FuelPoolBlockEntity pool)pool.setCustomName(name);}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moved){if(!state.is(next.getBlock())&&level.getBlockEntity(pos) instanceof FuelPoolBlockEntity pool){CrateContents.ejectWorld(level,pos.getX(),pos.getY(),pos.getZ(),pool);level.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,level,pos,next,moved);}
    @Override public void animateTick(BlockState state,Level level,BlockPos pos,RandomSource random){for(var side:Direction.values()){if(side==Direction.DOWN||!(level.getBlockState(pos.relative(side)).getBlock() instanceof LiquidBlock)||!level.getFluidState(pos.relative(side)).is(FluidTags.WATER))continue;int dx=side.getStepX(),dy=side.getStepY(),dz=side.getStepZ();double x=pos.getX()+.5F+dx+random.nextDouble()-.5,y=pos.getY()+.5F+dy+random.nextDouble()-.5,z=pos.getZ()+.5F+dz+random.nextDouble()-.5;if(dx!=0)x=pos.getX()+.5F+dx*.5+random.nextDouble()*.125*dx;if(dy!=0)y=pos.getY()+.5F+dy*.5+random.nextDouble()*.125*dy;if(dz!=0)z=pos.getZ()+.5F+dz*.5+random.nextDouble()*.125*dz;level.addParticle(ParticleTypes.BUBBLE,x,y,z,0,.2,0);}}
}
