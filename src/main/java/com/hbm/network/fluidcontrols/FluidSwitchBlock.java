package com.hbm.network.fluidcontrols;
import com.hbm.api.block.IAnalyzable;
import com.hbm.block.pipe.*;
import com.hbm.fluid.network.FluidNetworks;
import com.hbm.item.fluid.*;
import com.hbm.registry.HbmSounds;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
/** Source full cube, off/on atlas icons and redstone-only state; no fabricated manual toggling or inventory. */
public final class FluidSwitchBlock extends BaseEntityBlock implements IAnalyzable {
 public FluidSwitchBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(NativePipeBlock.OPEN,false));}
 @Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(ignored->new FluidSwitchBlock());}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(NativePipeBlock.OPEN);}
 @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new FluidSwitchEntity(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,FluidControlsContent.SWITCH_ENTITY.get(),NativePipeBlockEntity::tick);}
 @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block block,BlockPos from,boolean moving){if(level.isClientSide)return;boolean on=level.hasNeighborSignal(pos);if(on==state.getValue(NativePipeBlock.OPEN))return;level.setBlock(pos,state.setValue(NativePipeBlock.OPEN,on),2);level.playSound(null,pos,HbmSounds.get("block.reactorStart"),SoundSource.BLOCKS,1F,on?1F:.85F);if(level.getBlockEntity(pos) instanceof FluidSwitchEntity valve)valve.updateState();}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){
  var held=player.getMainHandItem();if(!(held.getItem() instanceof FluidIdentifier identifier)||!(level.getBlockEntity(pos) instanceof FluidSwitchEntity pipe))return InteractionResult.PASS;
  if(level.isClientSide)return InteractionResult.SUCCESS;var flags=PipeControls.flags(player);var selected=identifier.getType(level,pos,held);
  if(flags.alt()&&held.getItem() instanceof FluidIdentifierItem&&selected!=pipe.getFluidType()){FluidIdentifierItem.setType(held,pipe.getFluidType(),true);level.playSound(null,player,SoundEvents.EXPERIENCE_ORB_PICKUP,SoundSource.PLAYERS,.25F,.75F);return InteractionResult.SUCCESS;}
  if(flags.control()||player.isShiftKeyDown()){NativePipeBlock.changeType(level,pos,pipe.getFluidType(),selected,64);return InteractionResult.SUCCESS;}
  if(pipe.getFluidType()==selected)return InteractionResult.PASS;pipe.setType(selected);return InteractionResult.SUCCESS;
 }
 @Override public List<String> getDebugInfo(Level level,BlockPos pos){if(!(level instanceof ServerLevel server)||!(level.getBlockEntity(pos) instanceof FluidSwitchEntity pipe))return null;var node=FluidNetworks.nodeAt(server,pipe.getFluidType(),pos);if(node==null||node.network()==null)return null;var net=node.network();return List.of("Links: "+net.links.size(),"Subscribers: "+net.receiverEntries.size(),"Providers: "+net.providerEntries.size(),"Transfer: "+net.fluidTracker);}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder builder){return List.of(new ItemStack(FluidControlsContent.SWITCH_ITEM.get()));}
}
