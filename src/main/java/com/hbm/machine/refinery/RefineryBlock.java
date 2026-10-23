package com.hbm.machine.refinery;

import com.hbm.api.machine.IRepairable;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

public final class RefineryBlock extends DummyableBlock implements ToolableBlock {
    private static final MapCodec<RefineryBlock> CODEC=simpleCodec(ignored->new RefineryBlock());
    public RefineryBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,12).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}
    @Override public int[] dimensions(){return new int[]{8,0,1,1,1,1};}
    @Override public int offset(){return 1;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new RefineryBlockEntity(pos,state):isPort(state)?new RefineryPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,RefineryContent.ENTITY.get(),RefineryBlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());for(var port:RefineryBlockEntity.portPositions(core))setPort(level,port,true);if(level.getBlockEntity(core) instanceof RefineryBlockEntity machine)machine.restoreItem(stack);}
    private RefineryBlockEntity core(BlockGetter level,BlockPos pos){var position=findCore(level,pos);return position!=null&&level.getBlockEntity(position) instanceof RefineryBlockEntity machine?machine:null;}
    private InteractionResult interact(Level level,BlockPos pos,Player player,ItemStack stack){if(level.isClientSide)return InteractionResult.SUCCESS;var machine=core(level,pos);if(machine==null||machine.hasExploded)return InteractionResult.PASS;if(!player.isShiftKeyDown()&&player instanceof ServerPlayer server)server.openMenu(machine,buf->buf.writeBlockPos(machine.getBlockPos()));return InteractionResult.CONSUME;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return interact(level,pos,player,ItemStack.EMPTY);}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){var toolResult=super.useItemOn(stack,state,level,pos,player,hand,hit);if(toolResult!=ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)return toolResult;return interact(level,pos,player,stack)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){if(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof RefineryBlockEntity machine)return List.of(machine.dropStack());var origin=params.getOptionalParameter(LootContextParams.ORIGIN);if(origin!=null){var machine=core(params.getLevel(),BlockPos.containing(origin));if(machine!=null)return List.of(machine.dropStack());}return List.of();}
    @Override public boolean canDropFromExplosion(BlockState state,BlockGetter level,BlockPos pos,Explosion explosion){return false;}
    @Override public void onBlockExploded(BlockState state,Level level,BlockPos pos,Explosion explosion){var machine=core(level,pos);if(machine==null||machine.lastExplosion==explosion)return;machine.lastExplosion=explosion;if(!machine.hasExploded){machine.explode(level,pos);com.hbm.advancement.HbmAdvancements.zetaIgnited(level,pos,explosion);}else level.removeBlock(machine.getBlockPos(),false);}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){return tool==ToolType.TORCH&&IRepairable.tryRepairMultiblock(level,pos,this,player);}
}
