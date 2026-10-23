package com.hbm.block.light.reinforced;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** X5846's two real lamp identities, immediate turn-on and four-tick delayed turn-off. */
public final class ReinforcedLampBlock extends Block {
    public final boolean isOn;
    public ReinforcedLampBlock(boolean isOn) {
        super(Properties.of().strength(15F,48F).requiresCorrectToolForDrops().lightLevel(state->isOn?15:0));
        this.isOn=isOn;
    }
    @Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
    private void updatePower(Level level,BlockPos pos){
        if(level.isClientSide)return;
        boolean powered=level.hasNeighborSignal(pos);
        if(isOn&&!powered)level.scheduleTick(pos,this,4);
        else if(!isOn&&powered)level.setBlock(pos,ReinforcedLampContent.ON.get().defaultBlockState(),2);
    }
    @Override protected void onPlace(BlockState state,Level level,BlockPos pos,BlockState old,boolean moving){updatePower(level,pos);}
    @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block neighbor,BlockPos from,boolean moving){updatePower(level,pos);}
    @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){
        if(isOn&&!level.hasNeighborSignal(pos))level.setBlock(pos,ReinforcedLampContent.OFF.get().defaultBlockState(),2);
    }
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){return new ItemStack(ReinforcedLampContent.OFF.get());}
}
