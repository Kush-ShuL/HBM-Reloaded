package com.hbm.machine.fensu;

import com.hbm.multiblock.*;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Genuine MachineBatteryREDD, source9-block height/5×9 footprint/offset2 and six real power-conductor extras. */
public final class FensuBlock extends DummyableBlock {
    public FensuBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends FensuBlock> codec(){return simpleCodec(ignored->new FensuBlock());}
    @Override public int[] dimensions(){return new int[]{9,0,2,2,4,4};}
    @Override public int offset(){return 2;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new FensuBlockEntity(pos,state):isPort(state)?new FensuPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,FensuContent.ENTITY.get(),FensuBlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());for(var port:FensuPorts.at(core,placementFacing(state)))setPort(level,port.pos(),true);if(level.getBlockEntity(core) instanceof FensuBlockEntity machine)machine.readItem(stack);}
    public FensuBlockEntity core(BlockGetter level,BlockPos pos){var core=findCore(level,pos);return core!=null&&level.getBlockEntity(core) instanceof FensuBlockEntity machine?machine:null;}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){var be=context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);if(be instanceof FensuBlockEntity machine)return List.of(machine.dropStack());var origin=context.getOptionalParameter(LootContextParams.ORIGIN);if(origin!=null){var machine=core(context.getLevel(),BlockPos.containing(origin));if(machine!=null)return List.of(machine.dropStack());}return List.of(new ItemStack(FensuContent.ITEM.get()));}
    @Override public ItemStack getCloneItemStack(LevelReader level,BlockPos pos,BlockState state){var machine=core(level,pos);return machine==null?new ItemStack(FensuContent.ITEM.get()):machine.dropStack();}
    @Override protected boolean hasAnalogOutputSignal(BlockState state){return true;}
    @Override protected int getAnalogOutputSignal(BlockState state,Level level,BlockPos pos){var machine=state.getValue(LEGACY_META)>=6?core(level,pos):null;return machine==null?0:machine.comparatorPower();}
}
