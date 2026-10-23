package com.hbm.machine.reactor.rbmk.control;
import com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class RbmkControlBlock extends RbmkColumnBlock {
    public final RbmkControlKind kind;public RbmkControlBlock(RbmkControlKind kind){this.kind=kind;}
    @Override protected MapCodec<? extends RbmkControlBlock> codec(){return MapCodec.unit(this);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new RbmkControlEntity(pos,state):isPort(state)&&!kind.automatic?new RbmkControlPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,RbmkControlsContent.ENTITY.get(),RbmkControlEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(!level.isClientSide&&stack.has(DataComponents.CUSTOM_NAME)&&level.getBlockEntity(pos) instanceof RbmkControlEntity entity)entity.setCustomName(stack.get(DataComponents.CUSTOM_NAME));}
}
