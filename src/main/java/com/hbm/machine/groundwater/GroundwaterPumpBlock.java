package com.hbm.machine.groundwater;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Actual MachinePump dimensions/offset and all four original cardinal fluid/HE proxy positions. */
public final class GroundwaterPumpBlock extends DummyableBlock {
    public final GroundwaterPumpKind kind;
    public GroundwaterPumpBlock(GroundwaterPumpKind kind){super(Properties.of().strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());this.kind=kind;}
    @Override protected MapCodec<? extends GroundwaterPumpBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{3,0,1,1,1,1};}
    @Override public int offset(){return 1;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){
        super.setPlacedBy(level,pos,state,placer,stack);
        if(!level.isClientSide){var core=pos.relative(placementFacing(state),-offset());for(var side:Direction.Plane.HORIZONTAL)setPort(level,core.relative(side),true);}
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){
        if(isCore(state))return kind==GroundwaterPumpKind.STEAM?new SteamGroundwaterPump(pos,state):new ElectricGroundwaterPump(pos,state);
        return isPort(state)?new GroundwaterPumpPort(pos,state):null;
    }
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){
        if(!isCore(state))return null;
        return kind==GroundwaterPumpKind.STEAM?createTickerHelper(type,GroundwaterPumpContent.STEAM_ENTITY.get(),GroundwaterPumpEntity::tick)
                :createTickerHelper(type,GroundwaterPumpContent.ELECTRIC_ENTITY.get(),GroundwaterPumpEntity::tick);
    }
}
