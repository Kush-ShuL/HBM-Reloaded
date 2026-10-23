package com.hbm.machine.reactor.pwr;

import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;

/** Source pwr_block0/1: assembled pressure vessel with actual underlying component restored on break. */
public final class PwrAssemblyBlock extends BaseEntityBlock {
    public static final BooleanProperty PORT=BooleanProperty.create("port");
    static final ThreadLocal<Boolean> RESTORING=ThreadLocal.withInitial(()->false);
    public PwrAssemblyBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(15,6).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(PORT,false));}
    @Override protected MapCodec<? extends BaseEntityBlock> codec(){return MapCodec.unit(this);}
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(PORT);}
    @Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new PwrAssemblyBlockEntity(pos,state);}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:createTickerHelper(type,PwrContent.PART.get(),PwrAssemblyBlockEntity::tick);}
    @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of();}
    @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!next.is(this)&&!level.isClientSide&&!RESTORING.get()&&level.getBlockEntity(pos) instanceof PwrAssemblyBlockEntity part)part.restore();super.onRemove(state,level,pos,next,moving);}
}
