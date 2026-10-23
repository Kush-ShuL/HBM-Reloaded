package com.hbm.machine.combinationfurnace;
import com.hbm.multiblock.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Source3x2x3/offset1, real inv+fluid Combo factory for every non-core state. */
public final class CombinationFurnaceBlock extends DummyableBlock {
    public CombinationFurnaceBlock(){super(Properties.of().mapColor(MapColor.STONE).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends CombinationFurnaceBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{1,0,1,1,1,1};}@Override public int offset(){return 1;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new CombinationFurnaceEntity(pos,state):new DummyInventoryProxy(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,CombinationFurnaceContent.ENTITY.get(),CombinationFurnaceEntity::tick);}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;BlockPos core=pos.relative(placementFacing(state),-offset());for(BlockPos port:CombinationFurnaceEntity.portPositions(core))setPort(level,port,true);}
}
