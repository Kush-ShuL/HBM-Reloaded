package com.hbm.machine.steelfurnace;
import com.hbm.multiblock.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Source3x2x3/offset1, all non-core cells inventory-only. The orange extra is a preview, not collision. */
public final class SteelFurnaceBlock extends DummyableBlock {
    public SteelFurnaceBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends SteelFurnaceBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{1,0,1,1,1,1};}
    @Override public int offset(){return 1;}
    public double[][] sourcePreviewExtras(){return new double[][]{{0,0,.5,-.3,.4,-.4,1}};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new SteelFurnaceEntity(pos,state):new DummyInventoryProxy(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,SteelFurnaceContent.ENTITY.get(),SteelFurnaceEntity::tick);}
}
