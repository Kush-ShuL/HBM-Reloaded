package com.hbm.machine.turret;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.server.level.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
/** Source ordinary (non-Dummyable) iron container: full one-block collision, non-opaque, no placement yaw. */
public final class SentryBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);public final boolean damaged;
    public SentryBlock(boolean damaged){super(Properties.of().strength(5,3).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());this.damaged=damaged;registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends SentryBlock> codec(){return MapCodec.unit(this);}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override protected int getLightBlock(BlockState state,BlockGetter level,BlockPos pos){return 0;}@Override protected boolean propagatesSkylightDown(BlockState state,BlockGetter level,BlockPos pos){return true;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return damaged?new DamagedSentryEntity(pos,state):new SentryEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return (world,pos,s,tile)->{if(tile instanceof SentryEntity sentry){if(world instanceof ServerLevel server)sentry.tickServer(server);else sentry.tickClient();}};}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(damaged)return InteractionResult.PASS;if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof SentryEntity sentry)server.openMenu(sentry,pos);return InteractionResult.SUCCESS;}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){if(!state.is(replacement.getBlock())&&!damaged&&level.getBlockEntity(pos) instanceof SentryEntity sentry){if(!level.isClientSide)for(var stack:sentry.slots){if(stack.isEmpty())continue;var random=level.random;float x=random.nextFloat()*.8F+.1F,y=random.nextFloat()*.8F+.1F,z=random.nextFloat()*.8F+.1F;while(!stack.isEmpty()){int count=Math.min(stack.getCount(),random.nextInt(21)+10);var entity=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,stack.split(count));entity.setDeltaMovement((float)random.nextGaussian()*.05F,(float)random.nextGaussian()*.05F+.2F,(float)random.nextGaussian()*.05F);level.addFreshEntity(entity);}}level.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,level,pos,replacement,moving);}
}
