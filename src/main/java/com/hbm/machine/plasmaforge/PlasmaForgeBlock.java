package com.hbm.machine.plasmaforge;

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

/** Original seven-box footprint and two extra fluid proxies, placed at its final core once. */
public final class PlasmaForgeBlock extends DummyableBlock {
    private static final int[][] BOXES={{2,0,2,2,5,5},{4,-3,0,0,4,4},{2,0,3,-2,4,4},{2,0,-2,3,4,4},{2,0,4,-3,3,3},{2,0,-3,4,3,3},{2,0,5,-4,2,2},{2,0,-4,5,2,2},{3,-2,1,1,5,5}};
    public PlasmaForgeBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,60).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(ignored->new PlasmaForgeBlock());}
    @Override public int[] dimensions(){return BOXES[0].clone();}
    @Override public int offset(){return 5;}
    private static Set<BlockPos> parts(BlockPos core,Direction facing){var result=new LinkedHashSet<BlockPos>();for(var box:BOXES){var d=rotate(box,facing);for(var part:BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3])))result.add(part.immutable());}return result;}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){var facing=context.getHorizontalDirection().getOpposite();for(var part:parts(coreForPlacement(context),facing)){if(!context.getLevel().isInWorldBounds(part)||!context.getLevel().hasChunkAt(part))return null;if(!part.equals(context.getClickedPos())&&!context.getLevel().getBlockState(part).canBeReplaced())return null;}return defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue());}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new PlasmaForgeBlockEntity(pos,state):isPort(state)?new PlasmaForgePort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,PlasmaForgeContent.ENTITY.get(),PlasmaForgeBlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());
        safely(()->{if(!pos.equals(core))level.removeBlock(pos,false);level.setBlock(core,defaultBlockState().setValue(LEGACY_META,facing.get3DDataValue()+10),UPDATE_ALL);for(var part:parts(core,facing)){if(part.equals(core))continue;Direction away=part.getY()>core.getY()?Direction.UP:part.getX()<core.getX()?Direction.WEST:part.getX()>core.getX()?Direction.EAST:part.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;level.setBlock(part,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}});
        for(var port:PlasmaForgeBlockEntity.portPositions(core,facing))setPort(level,port,true);
        if(level.getBlockEntity(core) instanceof PlasmaForgeBlockEntity machine){if(stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME))machine.setCustomName(stack.getHoverName());}
    }
    public PlasmaForgeBlockEntity core(BlockGetter level,BlockPos pos){var core=findCore(level,pos);return core!=null&&level.getBlockEntity(core) instanceof PlasmaForgeBlockEntity machine?machine:null;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(player.isShiftKeyDown())return InteractionResult.PASS;if(!level.isClientSide&&player instanceof ServerPlayer server){var machine=core(level,pos);if(machine!=null)server.openMenu(machine,buffer->buffer.writeBlockPos(machine.getBlockPos()));}return InteractionResult.sidedSuccess(level.isClientSide);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){var base=super.useItemOn(stack,state,level,pos,player,hand,hit);if(base!=ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)return base;return useWithoutItem(state,level,pos,player,hit)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
}
