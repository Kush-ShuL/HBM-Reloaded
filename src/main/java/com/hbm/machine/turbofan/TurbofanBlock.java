package com.hbm.machine.turbofan;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
public final class TurbofanBlock extends DummyableBlock {
 public TurbofanBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}@Override protected MapCodec<? extends TurbofanBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{2,0,1,1,3,3};}@Override public int offset(){return 1;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new TurbofanEntity(pos,state):isPort(state)?new TurbofanPort(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,TurbofanContent.ENTITY.get(),TurbofanEntity::tick);}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var d=placementFacing(state);var core=pos.relative(d,-offset());for(var c:TurbofanEntity.connections(core,d))setPort(level,c.origin(),true);}
 @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){if(level.isClientSide)return InteractionResult.SUCCESS;if(player.isShiftKeyDown())return InteractionResult.CONSUME;return super.useWithoutItem(state,level,pos,player,hit);}
}
