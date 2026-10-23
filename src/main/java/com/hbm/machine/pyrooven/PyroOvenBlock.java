package com.hbm.machine.pyrooven;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Actual five-by-seven-by-three body, offset3, five side ports plus the separate upper smoke port. */
public final class PyroOvenBlock extends DummyableBlock {
 private static final MapCodec<PyroOvenBlock> CODEC=simpleCodec(ignored->new PyroOvenBlock());
 public PyroOvenBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}@Override public int[] dimensions(){return new int[]{2,0,3,3,2,2};}@Override public int offset(){return 3;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new PyroOvenEntity(pos,state):isPort(state)?new PyroOvenPort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,PyroOvenContent.ENTITY.get(),PyroOvenEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());var dir=placementFacing(state);for(var port:PyroOvenEntity.portPositions(core,dir))setPort(level,port,true);setPort(level,PyroOvenEntity.smokePosition(core,dir),true);}
}
