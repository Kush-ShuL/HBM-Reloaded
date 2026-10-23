package com.hbm.network.rtty.telex;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
public final class RadioTelexBlock extends DummyableBlock {
 public static Consumer<RadioTelexEntity> OPEN_SCREEN=ignored->{};
 public RadioTelexBlock(){super(Properties.of().strength(3,6).sound(SoundType.STONE).ignitedByLava().noOcclusion());}@Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{0,0,0,0,1,0};}@Override public int offset(){return 0;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new RadioTelexEntity(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return !level.isClientSide&&isCore(state)?createTickerHelper(type,RadioTelexContent.ENTITY.get(),RadioTelexEntity::tick):null;}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(player.isShiftKeyDown())return InteractionResult.PASS;if(level.isClientSide){var core=findCore(level,pos);if(core==null||!(level.getBlockEntity(core) instanceof RadioTelexEntity telex))return InteractionResult.PASS;OPEN_SCREEN.accept(telex);}return InteractionResult.sidedSuccess(level.isClientSide);}
}
