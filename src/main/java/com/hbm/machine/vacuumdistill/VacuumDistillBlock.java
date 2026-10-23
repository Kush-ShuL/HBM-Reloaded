package com.hbm.machine.vacuumdistill;

import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

/** Source nine-high dummy assembly, four corner proxies with native power/fluid only. */
public final class VacuumDistillBlock extends DummyableBlock {
    private static final MapCodec<VacuumDistillBlock> CODEC=simpleCodec(ignored->new VacuumDistillBlock());
    public VacuumDistillBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,12).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
    @Override protected MapCodec<? extends DummyableBlock> codec(){return CODEC;}
    @Override public int[] dimensions(){return new int[]{8,0,1,1,1,1};}
    @Override public int offset(){return 1;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new VacuumDistillBlockEntity(pos,state):isPort(state)?new VacuumDistillPort(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,VacuumDistillContent.ENTITY.get(),VacuumDistillBlockEntity::tick):null;}
    @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());for(var port:VacuumDistillBlockEntity.portPositions(core))setPort(level,port,true);if(level.getBlockEntity(core) instanceof VacuumDistillBlockEntity machine)machine.restoreItem(stack);}
    private VacuumDistillBlockEntity core(BlockGetter level,BlockPos pos){var core=findCore(level,pos);return core!=null&&level.getBlockEntity(core) instanceof VacuumDistillBlockEntity machine?machine:null;}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){if(params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof VacuumDistillBlockEntity machine)return List.of(machine.dropStack());var origin=params.getOptionalParameter(LootContextParams.ORIGIN);if(origin!=null){var machine=core(params.getLevel(),BlockPos.containing(origin));if(machine!=null)return List.of(machine.dropStack());}return List.of();}
}
