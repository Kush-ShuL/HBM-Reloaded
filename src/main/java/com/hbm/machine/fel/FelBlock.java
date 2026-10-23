package com.hbm.machine.fel;
import com.hbm.multiblock.DummyableBlock;import com.mojang.serialization.MapCodec;import net.minecraft.core.*;import net.minecraft.world.entity.LivingEntity;import net.minecraft.world.item.ItemStack;import net.minecraft.world.level.Level;import net.minecraft.world.level.block.SoundType;import net.minecraft.world.level.block.entity.*;import net.minecraft.world.level.block.state.BlockState;import net.minecraft.world.level.material.MapColor;
/** Original offset-two7x3x3 body and its one power-only rear proxy. */
public final class FelBlock extends DummyableBlock {
 private static final MapCodec<FelBlock> CODEC=simpleCodec(ignored->new FelBlock());
 public FelBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}@Override public int[] dimensions(){return new int[]{2,0,4,2,1,1};}@Override public int offset(){return 2;}
 @Override public void setPlacedBy(Level world,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(world,pos,state,placer,stack);if(!world.isClientSide){var facing=placementFacing(state);var core=pos.relative(facing,-offset());setPort(world,FelBlockEntity.portConnection(core,facing).origin(),true);}}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new FelBlockEntity(pos,state):isPort(state)?new FelPort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,FelContent.ENTITY.get(),FelBlockEntity::tick):null;}
}
