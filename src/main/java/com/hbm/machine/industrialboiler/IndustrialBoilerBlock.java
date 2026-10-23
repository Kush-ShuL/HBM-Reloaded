package com.hbm.machine.industrialboiler;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
/** Original5-high boiler, no inventory/GUI; non-sneaking identifier selects a valid BOILER fluid. */
public final class IndustrialBoilerBlock extends DummyableBlock {
 public IndustrialBoilerBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}@Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{4,0,1,1,1,1};}@Override public int offset(){return 1;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new IndustrialBoilerBlockEntity(pos,state):new IndustrialBoilerProxy(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,IndustrialBoilerContent.ENTITY.get(),IndustrialBoilerBlockEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity player,ItemStack stack){super.setPlacedBy(level,pos,state,player,stack);if(level.isClientSide)return;var core=findCore(level,pos);if(core!=null)for(var port:IndustrialBoilerBlockEntity.portPositions(core))setPort(level,port,true);}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(!player.isShiftKeyDown()&&stack.getItem() instanceof FluidIdentifier identifier){if(level.isClientSide)return ItemInteractionResult.SUCCESS;var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof IndustrialBoilerBlockEntity boiler))return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;var type=identifier.getType(level,core,stack);if(boiler.identify(type))player.displayClientMessage(Component.translatable("message.hbm.industrial_boiler.type",type.displayName()).withStyle(ChatFormatting.YELLOW),false);return ItemInteractionResult.CONSUME;}return super.useItemOn(stack,state,level,pos,player,hand,hit);}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return level.isClientSide||player.isShiftKeyDown()?InteractionResult.SUCCESS:InteractionResult.PASS;}
}
