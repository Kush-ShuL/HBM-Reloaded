package com.hbm.machine.bat9000;

import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

/** Original three-XR footprint and eight fluid-only proxies, placed at its final core once. */
public final class Bat9000Block extends DummyableBlock {
    public static final int[][] BOXES={{4,0,2,2,1,1},{4,0,1,1,2,-2},{4,0,1,1,-2,2}};
    public Bat9000Block(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(ignored->new Bat9000Block());}
    @Override public int[] dimensions(){return BOXES[0].clone();}
    @Override public int offset(){return 2;}
    public static Set<BlockPos> parts(BlockPos core,Direction facing){var result=new LinkedHashSet<BlockPos>();for(var box:BOXES){var d=rotate(box,facing);for(var part:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3])))result.add(part.immutable());}return result;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var facing=context.getHorizontalDirection().getOpposite();for(var part:parts(coreForPlacement(context),facing)){if(!context.getLevel().isInWorldBounds(part)||!context.getLevel().hasChunkAt(part))return null;if(!part.equals(context.getClickedPos())&&!context.getLevel().getBlockState(part).canBeReplaced())return null;}return defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue());}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new Bat9000BlockEntity(pos,state):isPort(state)?new Bat9000Port(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,Bat9000Content.ENTITY.get(),Bat9000BlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());
        safely(()->{if(!pos.equals(core))level.removeBlock(pos,false);level.setBlock(core,defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue()+10),UPDATE_ALL);for(var part:parts(core,facing)){if(part.equals(core))continue;Direction away=part.getY()>core.getY()?Direction.UP:part.getX()<core.getX()?Direction.WEST:part.getX()>core.getX()?Direction.EAST:part.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(part,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}});
        for(var port:Bat9000BlockEntity.portPositions(core))setPort(level,port,true);
        if(level.getBlockEntity(core) instanceof Bat9000BlockEntity machine){machine.restoreItem(stack);if(stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME))machine.setCustomName(stack.getHoverName());}
    }
    public Bat9000BlockEntity core(BlockGetter level,BlockPos pos){var core=findCore(level,pos);return core!=null&&level.getBlockEntity(core) instanceof Bat9000BlockEntity machine?machine:null;}
    private InteractionResult interact(Level level,BlockPos pos,Player player,ItemStack stack){if(level.isClientSide)return InteractionResult.SUCCESS;var machine=core(level,pos);if(machine==null)return InteractionResult.PASS;if(!player.isShiftKeyDown()){if(player instanceof ServerPlayer server)server.openMenu(machine,buffer->buffer.writeBlockPos(machine.getBlockPos()));}else if(stack.getItem() instanceof FluidIdentifier identifier){var type=identifier.getType(level,machine.getBlockPos(),stack);machine.tank.setTankType(type);machine.setChanged();player.displayClientMessage(Component.translatable("message.hbm.barrel.changed_type",type.displayName()).withStyle(ChatFormatting.YELLOW),false);}return InteractionResult.CONSUME;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return interact(level,pos,player,ItemStack.EMPTY);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){var result=super.useItemOn(stack,state,level,pos,player,hand,hit);if(result!=ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)return result;return interact(level,pos,player,stack)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
    @Override public boolean onDestroyedByPlayer(BlockState state,Level level,BlockPos pos,Player player,boolean willHarvest,net.minecraft.world.level.material.FluidState fluid){if(!level.isClientSide&&willHarvest&&core(level,pos)!=null)return true;return super.onDestroyedByPlayer(state,level,pos,player,willHarvest,fluid);}
    @Override public void playerDestroy(Level level,Player player,BlockPos pos,BlockState state,BlockEntity original,ItemStack tool){var machine=core(level,pos);try{super.playerDestroy(level,player,pos,state,machine!=null?machine:original,tool);}finally{if(machine!=null&&core(level,pos)==machine)level.removeBlock(pos,false);}}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){if(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof Bat9000BlockEntity machine)return List.of(machine.dropStack());var origin=params.getOptionalParameter(LootContextParams.ORIGIN);var machine=origin==null?null:core(params.getLevel(),BlockPos.containing(origin));return machine==null?List.of():List.of(machine.dropStack());}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){var machine=core(level,pos);return machine==null?new ItemStack(this):machine.dropStack();}
    @Override protected boolean hasAnalogOutputSignal(BlockState state){return true;}
    @Override protected int getAnalogOutputSignal(BlockState state,Level level,BlockPos pos){var machine=state.getValue(LEGACY_META)>=6?core(level,pos):null;return machine==null?0:machine.comparatorPower();}
}
