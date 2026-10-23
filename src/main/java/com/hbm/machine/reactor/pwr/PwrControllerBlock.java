package com.hbm.machine.reactor.pwr;

import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

/** Original free-layout flood-fill; casing bounds terminate recursion and six true interior kinds continue it. */
public final class PwrControllerBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING=BlockStateProperties.HORIZONTAL_FACING;
    public PwrControllerBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(FACING,Direction.SOUTH));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(FACING);}
    @Override public BlockState getStateForPlacement(BlockPlaceContext context){return defaultBlockState().setValue(FACING,context.getHorizontalDirection().getOpposite());}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override protected BlockState rotate(BlockState state,Rotation rotation){return state.setValue(FACING,rotation.rotate(state.getValue(FACING)));}
    @Override protected BlockState mirror(BlockState state,Mirror mirror){return state.rotate(mirror.getRotation(state.getValue(FACING)));}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new PwrControllerBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,PwrContent.ENTITY.get(),PwrControllerBlockEntity::tick);}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.PASS;if(!(level.getBlockEntity(pos) instanceof PwrControllerBlockEntity core))return InteractionResult.PASS;if(!core.assembled)assemble(level,pos,player);else {if(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).equals(PwrContent.id("pwr_printer")))return InteractionResult.PASS;if(player instanceof ServerPlayer server)server.openMenu(core,pos);}return InteractionResult.CONSUME;}
    public void assemble(Level level,BlockPos pos,Player player){
        var parts=new HashMap<BlockPos,Block>();var fuel=new HashMap<BlockPos,Block>();var sources=new HashMap<BlockPos,Block>();parts.put(pos,this);var pending=new ArrayDeque<BlockPos>();pending.push(pos.relative(level.getBlockState(pos).getValue(FACING).getOpposite()));boolean errored=false;
        while(!pending.isEmpty()){
            var at=pending.pop();if(parts.containsKey(at))continue;if(parts.size()>=4096){errored=true;error(player,at,"message.hbm.pwr.max_size");continue;}
            var block=level.getBlockState(at).getBlock();if(isCasing(block)){parts.put(at,block);continue;}if(isInterior(block)){
                parts.put(at,block);if(block==PwrContent.block("pwr_fuel"))fuel.put(at,block);if(block==PwrContent.block("pwr_neutron_source"))sources.put(at,block);
                // Reverse stack pushes exactly preserve source +X,-X,+Y,-Y,+Z,-Z DFS.
                pending.push(at.north());pending.push(at.south());pending.push(at.below());pending.push(at.above());pending.push(at.west());pending.push(at.east());continue;
            }errored=true;error(player,at,"message.hbm.pwr.non_reactor");
        }
        if(fuel.isEmpty()){errored=true;error(player,pos,"message.hbm.pwr.fuel_required");}if(sources.isEmpty()){errored=true;error(player,pos,"message.hbm.pwr.source_required");}
        if(!(level.getBlockEntity(pos) instanceof PwrControllerBlockEntity controller))return;
        if(!errored){for(var entry:parts.entrySet()){var at=entry.getKey();var original=entry.getValue();if(original==this)continue;level.setBlock(at,PwrContent.ASSEMBLY.get().defaultBlockState().setValue(PwrAssemblyBlock.PORT,original==PwrContent.block("pwr_port")),3);if(level.getBlockEntity(at) instanceof PwrAssemblyBlockEntity part){part.original=original;part.controllerPos=pos.immutable();part.setChanged();level.invalidateCapabilities(at);level.sendBlockUpdated(at,part.getBlockState(),part.getBlockState(),3);}}controller.setup(parts,fuel);}
        controller.assembled=!errored;controller.setChanged();
    }
    private static void error(Player player,BlockPos pos,String key){if(player instanceof ServerPlayer server)PwrPackets.marker(server,pos,key);}
    private static boolean isInterior(Block block){return block==PwrContent.block("pwr_fuel")||block==PwrContent.CONTROL.get()||block==PwrContent.CHANNEL.get()||block==PwrContent.block("pwr_heatex")||block==PwrContent.block("pwr_heatsink")||block==PwrContent.block("pwr_neutron_source");}
    private static boolean isCasing(Block block){return block==PwrContent.block("pwr_casing")||block==PwrContent.block("pwr_reflector")||block==PwrContent.block("pwr_port");}
}
