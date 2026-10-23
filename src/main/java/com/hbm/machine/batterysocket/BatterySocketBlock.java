package com.hbm.machine.batterysocket;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

public final class BatterySocketBlock extends DummyableBlock {
    private static final MapCodec<BatterySocketBlock> CODEC=simpleCodec(ignored->new BatterySocketBlock());
    public BatterySocketBlock() { super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops()); }
    @Override protected MapCodec<? extends DummyableBlock> codec() { return CODEC; }
    @Override public int[] dimensions() { return new int[]{1,0,1,0,1,0}; }
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack) {
        super.setPlacedBy(level,pos,state,placer,stack);
        if(!level.isClientSide) for(BlockPos port:BatterySocketBlockEntity.portPositions(pos,state)) if(!port.equals(pos)) setPort(level,port,true);
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state) { return isCore(state)?new BatterySocketBlockEntity(pos,state):isPort(state)?new BatterySocketPort(pos,state):null; }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type) { return isCore(state)?createTickerHelper(type,BatterySocketContent.ENTITY.get(),BatterySocketBlockEntity::tick):null; }
    @Override protected boolean hasAnalogOutputSignal(BlockState state) { return true; }
    @Override protected int getAnalogOutputSignal(BlockState state,Level level,BlockPos pos) {
        if(state.getValue(LEGACY_META)<6) return 0;
        BlockPos core=findCore(level,pos);
        return core!=null&&level.getBlockEntity(core) instanceof BatterySocketBlockEntity socket?socket.comparatorPower():0;
    }
}
