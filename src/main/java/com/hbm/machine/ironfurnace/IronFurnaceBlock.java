package com.hbm.machine.ironfurnace;

import com.hbm.multiblock.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Source dimensions {1,0,1,0,1,0}; every non-core is the real inventory-only source proxy. */
public final class IronFurnaceBlock extends DummyableBlock {
    public IronFurnaceBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends IronFurnaceBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{1,0,1,0,1,0};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new IronFurnaceEntity(pos,state):new DummyInventoryProxy(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,IronFurnaceContent.ENTITY.get(),IronFurnaceEntity::tick);}
}
