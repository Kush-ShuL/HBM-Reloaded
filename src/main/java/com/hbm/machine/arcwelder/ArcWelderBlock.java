package com.hbm.machine.arcwelder;
import com.hbm.multiblock.DummyableBlock;import com.mojang.serialization.MapCodec;import net.minecraft.core.BlockPos;import net.minecraft.world.entity.LivingEntity;import net.minecraft.world.item.ItemStack;import net.minecraft.world.level.Level;import net.minecraft.world.level.block.SoundType;import net.minecraft.world.level.block.entity.*;import net.minecraft.world.level.block.state.BlockState;import net.minecraft.world.level.material.MapColor;
/** Source3x2 footprint, two blocks high; six bottom connector positions, offset0. */
public final class ArcWelderBlock extends DummyableBlock {
 private static final MapCodec<ArcWelderBlock> CODEC=simpleCodec(ignored->new ArcWelderBlock());
 public ArcWelderBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,18).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}@Override public int[] dimensions(){return new int[]{1,0,1,0,1,1};}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(!level.isClientSide)for(var port:ArcWelderBlockEntity.ports(pos,placementFacing(state)))setPort(level,port.pos(),true);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ArcWelderBlockEntity(pos,state):isPort(state)?new ArcWelderPort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ArcWelderContent.ENTITY.get(),ArcWelderBlockEntity::tick):null;}
}
