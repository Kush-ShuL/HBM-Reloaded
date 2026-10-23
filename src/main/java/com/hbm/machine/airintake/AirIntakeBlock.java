package com.hbm.machine.airintake;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/** Original MachineIntake:2x2x1,offset0; one genuine core and3 power/fluid combo proxies. */
public final class AirIntakeBlock extends DummyableBlock {
    public AirIntakeBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(10F,12F).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(properties->new AirIntakeBlock());}
    @Override public int[] dimensions(){return new int[]{0,0,1,0,1,0};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new AirIntakeBlockEntity(pos,state):isPort(state)?new AirIntakeProxy(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,AirIntakeContent.ENTITY.get(),AirIntakeBlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=findCore(level,pos);if(core!=null)for(var port:AirIntakeBlockEntity.portDefinitions(core,level.getBlockState(core)))setPort(level,port.position(),true);}
}
