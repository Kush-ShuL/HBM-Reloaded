package com.hbm.machine.reactor.dfc;
import com.mojang.serialization.MapCodec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
/** Original full cube collision and actual CoreCore fragment drops; the visible core is separately rendered. */
public final class DfcCoreBlock extends BaseEntityBlock {
    private final Random dropRandom=new Random();
    public DfcCoreBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends DfcCoreBlock> codec(){return MapCodec.unit(this);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new DfcCoreEntity(pos,state);}
    @Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,DfcCoreContent.ENTITY.get(),DfcCoreEntity::tick);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos)instanceof DfcCoreEntity core)server.openMenu(core,pos);return InteractionResult.SUCCESS;}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moved){if(!next.is(this)&&level.getBlockEntity(pos)instanceof DfcCoreEntity core){for(var stack:core.slots){if(stack.isEmpty())continue;float x=dropRandom.nextFloat()*.8F+.1F,y=dropRandom.nextFloat()*.8F+.1F,z=dropRandom.nextFloat()*.8F+.1F;while(!stack.isEmpty()){int count=Math.min(dropRandom.nextInt(21)+10,stack.getCount());var drop=stack.copyWithCount(count);stack.shrink(count);var entity=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,drop);entity.setDeltaMovement((float)dropRandom.nextGaussian()*.05F,(float)dropRandom.nextGaussian()*.05F+.2F,(float)dropRandom.nextGaussian()*.05F);level.addFreshEntity(entity);}}level.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,level,pos,next,moved);}
}
