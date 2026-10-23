package com.hbm.network.fluidcontrols;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.network.chat.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
/** Three real occupied cells and two distinct physical fluid nodes; no invented MenuType for source null-container GUI. */
public final class FlowPumpBlock extends DummyableBlock {
 public static Consumer<PumpEntity> OPEN_SCREEN=machine->{};
 public FlowPumpBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(ignored->new FlowPumpBlock());}
 @Override public int[] dimensions(){return new int[]{0,0,0,0,1,1};}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new PumpEntity(false,pos,state):new PumpPort(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,FluidControlsContent.FLOW_ENTITY.get(),PumpEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){super.setPlacedBy(level,pos,state,player,stack);if(level.isClientSide)return;var core=findCore(level,pos);if(core==null)return;var direction=PumpEntity.direction(level.getBlockState(core),false);setPort(level,core.relative(direction),true);setPort(level,core.relative(direction.getOpposite()),true);}
 public PumpEntity core(BlockGetter world,BlockPos pos){var found=findCore(world,pos);return found!=null&&world.getBlockEntity(found) instanceof PumpEntity pump?pump:null;}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){var pump=core(level,pos);if(pump==null)return InteractionResult.PASS;var stack=player.getMainHandItem();
  if(stack.getItem() instanceof FluidIdentifier identifier){if(!level.isClientSide){var selected=identifier.getType(level,pos,stack);pump.tank.setTankType(selected);pump.setChanged();pump.sync();player.displayClientMessage(Component.translatable("message.hbm.flow_control_pump.type",Component.translatable(selected.getConditionalName())).withStyle(net.minecraft.ChatFormatting.YELLOW),false);}return InteractionResult.sidedSuccess(level.isClientSide);}
  if(player.isShiftKeyDown())return InteractionResult.PASS;if(level.isClientSide)OPEN_SCREEN.accept(pump);return InteractionResult.sidedSuccess(level.isClientSide);
 }
}
