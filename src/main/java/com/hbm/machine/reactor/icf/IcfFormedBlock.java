package com.hbm.machine.reactor.icf;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
/** Real hidden formed laser component: first removal restores its actual stored construction block. */
public final class IcfFormedBlock extends BaseEntityBlock{
 public static final BooleanProperty PORT=BooleanProperty.create("port");private static final ThreadLocal<Boolean> RESTORING=ThreadLocal.withInitial(()->false);
 public IcfFormedBlock(){super(Properties.of().strength(5,10).sound(SoundType.STONE).requiresCorrectToolForDrops());registerDefaultState(stateDefinition.any().setValue(PORT,false));}@Override protected MapCodec<? extends BaseEntityBlock> codec(){return simpleCodec(p->new IcfFormedBlock());}@Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder){builder.add(PORT);}@Override protected RenderShape getRenderShape(BlockState state){return RenderShape.MODEL;}@Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of();}@Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return new IcfFormedEntity(pos,state);}@Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return createTickerHelper(type,IcfReactorContent.FORMED_ENTITY.get(),IcfFormedEntity::tick);}
 @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState replacement,boolean moving){if(!replacement.is(this)&&!RESTORING.get()&&level.getBlockEntity(pos) instanceof IcfFormedEntity entity){var original=entity.original;var controller=entity.core();super.onRemove(state,level,pos,replacement,moving);if(original!=null){RESTORING.set(true);try{level.setBlock(pos,original,UPDATE_ALL);}finally{RESTORING.set(false);}if(controller!=null){controller.assembled=false;controller.setChanged();}}return;}super.onRemove(state,level,pos,replacement,moving);}
 public void restore(Level level,BlockPos pos){var state=level.getBlockState(pos);if(!state.is(this))return;level.removeBlock(pos,false);}
}
