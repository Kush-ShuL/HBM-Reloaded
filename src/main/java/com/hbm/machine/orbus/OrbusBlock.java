package com.hbm.machine.orbus;
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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
/** Source 4x5x4 dummy footprint, own eight y0/y4 proxy cells and persistent tank drop. */
public final class OrbusBlock extends DummyableBlock {
 public OrbusBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}@Override protected MapCodec<? extends OrbusBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{4,0,2,1,2,1};}@Override public int offset(){return 1;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new OrbusEntity(pos,state):isPort(state)?new OrbusPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,OrbusContent.ENTITY.get(),OrbusEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var facing=placementFacing(state);var core=pos.relative(facing,-offset());for(var p:OrbusEntity.proxyPositions(core,facing))setPort(level,p,true);if(level.getBlockEntity(core) instanceof OrbusEntity machine)machine.restoreItem(stack);}
 public OrbusEntity core(BlockGetter level,BlockPos pos){var p=findCore(level,pos);return p!=null&&level.getBlockEntity(p) instanceof OrbusEntity machine?machine:null;}
 private InteractionResult interact(Level level,BlockPos pos,Player player,ItemStack stack){if(level.isClientSide)return InteractionResult.SUCCESS;var c=core(level,pos);if(c==null)return InteractionResult.PASS;if(!player.isShiftKeyDown()){if(player instanceof ServerPlayer server)server.openMenu(c,buffer->buffer.writeBlockPos(c.getBlockPos()));}else if(stack.getItem() instanceof FluidIdentifier identifier){var type=identifier.getType(level,c.getBlockPos(),stack);c.tank.setTankType(type);c.setChanged();player.displayClientMessage(Component.translatable("message.hbm.barrel.changed_type",type.displayName()).withStyle(ChatFormatting.YELLOW),false);}return InteractionResult.CONSUME;}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return interact(level,pos,player,ItemStack.EMPTY);}@Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){var result=super.useItemOn(stack,state,level,pos,player,hand,hit);if(result!=ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION)return result;return interact(level,pos,player,stack)==InteractionResult.PASS?ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION:ItemInteractionResult.sidedSuccess(level.isClientSide);}
 @Override public boolean onDestroyedByPlayer(BlockState state,Level level,BlockPos pos,Player player,boolean willHarvest,net.minecraft.world.level.material.FluidState fluid){if(!level.isClientSide&&willHarvest&&core(level,pos)!=null)return true;return super.onDestroyedByPlayer(state,level,pos,player,willHarvest,fluid);}
 @Override public void playerDestroy(Level level,Player player,BlockPos pos,BlockState state,BlockEntity original,ItemStack tool){var c=core(level,pos);try{super.playerDestroy(level,player,pos,state,c!=null?c:original,tool);}finally{if(c!=null&&core(level,pos)==c)level.removeBlock(pos,false);}}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){if(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof OrbusEntity c)return List.of(c.dropStack());var origin=params.getOptionalParameter(LootContextParams.ORIGIN);var c=origin==null?null:core(params.getLevel(),BlockPos.containing(origin));return c==null?List.of():List.of(c.dropStack());}
}
