package com.hbm.world.structure.templates.microwave;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
/** Original BlockMachineBase: full-cube bounds, yaw[2,5,3,4] and world-RNG fragment drops. */
public final class MicrowaveBlock extends BaseEntityBlock {
    public static final IntegerProperty META=IntegerProperty.create("legacy_meta",0,15);private static final MapCodec<MicrowaveBlock> CODEC=simpleCodec(ignored->new MicrowaveBlock());
    public MicrowaveBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5F,6F).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(META,0));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return CODEC;}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(META);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){float yaw=context.getPlayer()==null?0F:context.getPlayer().getYRot();return defaultBlockState().setValue(META,new int[]{2,5,3,4}[Mth.floor((double)(yaw*4F/360F)+.5D)&3]);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.ENTITYBLOCK_ANIMATED;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new MicrowaveBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,MicrowaveContent.ENTITY.get(),(world,pos,block,machine)->machine.tickServer());}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(player instanceof ServerPlayer server&&level.getBlockEntity(pos) instanceof MicrowaveBlockEntity machine)server.openMenu(machine,pos);return InteractionResult.CONSUME;}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moved){if(!next.is(this)&&level.getBlockEntity(pos) instanceof MicrowaveBlockEntity machine){var random=level.random;for(int slot=0;slot<3;slot++){var stack=machine.getItem(slot);if(stack.isEmpty())continue;float x=random.nextFloat()*.8F+.1F,y=random.nextFloat()*.8F+.1F,z=random.nextFloat()*.8F+.1F;while(!stack.isEmpty()){int count=Math.min(random.nextInt(21)+10,stack.getCount());var drop=stack.copyWithCount(count);stack.shrink(count);var entity=new ItemEntity(level,pos.getX()+x,pos.getY()+y,pos.getZ()+z,drop);entity.setDeltaMovement((float)random.nextGaussian()*.05F,(float)random.nextGaussian()*.05F+.2F,(float)random.nextGaussian()*.05F);level.addFreshEntity(entity);}}level.updateNeighbourForOutputSignal(pos,this);}super.onRemove(state,level,pos,next,moved);}
}
