package com.hbm.network.separator;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.core.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
public final class NetworkSeparatorBlock extends DummyableBlock {
 public static Consumer<NetworkSeparatorEntity> OPEN_SCREEN=tile->{};public NetworkSeparatorBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}@Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(ignored->new NetworkSeparatorBlock());}@Override public int[] dimensions(){return new int[]{1,0,0,0,1,1};}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new NetworkSeparatorEntity(pos,state):new NetworkSeparatorPort(pos,state);}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide||!isCore(state)?null:createTickerHelper(type,NetworkSeparatorContent.ENTITY.get(),(world,pos,s,tile)->tile.tickServer());}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var dir=placementFacing(state).getCounterClockWise();setPort(level,pos.relative(dir),true);setPort(level,pos.relative(dir.getOpposite()),true);}
 public NetworkSeparatorEntity core(BlockGetter level,BlockPos pos){var core=findCore(level,pos);return core!=null&&level.getBlockEntity(core) instanceof NetworkSeparatorEntity machine?machine:null;}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(player.isShiftKeyDown())return InteractionResult.PASS;var machine=core(level,pos);if(machine==null)return InteractionResult.PASS;if(level.isClientSide)OPEN_SCREEN.accept(machine);return InteractionResult.sidedSuccess(level.isClientSide);}
}
