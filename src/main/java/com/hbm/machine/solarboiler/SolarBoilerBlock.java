package com.hbm.machine.solarboiler;
import com.hbm.multiblock.DummyableBlock;import com.mojang.serialization.MapCodec;import net.minecraft.core.*;import net.minecraft.world.entity.LivingEntity;import net.minecraft.world.item.ItemStack;import net.minecraft.world.level.Level;import net.minecraft.world.level.block.SoundType;import net.minecraft.world.level.block.entity.*;import net.minecraft.world.level.block.state.BlockState;import net.minecraft.world.level.material.MapColor;
/** Source3x3x3 offset1, top and bottom real private-fluid combo ports. */
public final class SolarBoilerBlock extends DummyableBlock {
 public SolarBoilerBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(p->new SolarBoilerBlock());}@Override public int[] dimensions(){return new int[]{2,0,1,1,1,1};}@Override public int offset(){return 1;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new SolarBoilerBlockEntity(pos,state):isPort(state)?new SolarBoilerProxy(pos,state):null;}@Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,SolarBoilerContent.BOILER_BE.get(),SolarBoilerBlockEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack item){super.setPlacedBy(level,pos,state,placer,item);if(!level.isClientSide){var core=pos.relative(placementFacing(state),-offset());setPort(level,core.above(2),true);}}
}
