package com.hbm.machine.pa.cooled;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** All five source bodies, exact final core height and actual private power/fluid/inventory ports. */
public final class PaBlock extends DummyableBlock {
 public final PaKind kind;
 public PaBlock(PaKind kind){super(Properties.of().mapColor(MapColor.METAL).strength(5,10).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion());this.kind=kind;}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(p->new PaBlock(kind));}@Override public int[] dimensions(){return kind.dimensions();}@Override public int heightOffset(){return kind.height;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(level.isClientSide)return;Direction facing=placementFacing(state);BlockPos core=pos.above(heightOffset());for(var port:PaPorts.positions(kind,core,facing))setPort(level,port.position(),true);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?PaContent.create(kind,pos,state):isPort(state)?new PaPort(pos,state):null;}
 @Override public <T extends BlockEntity>BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,PaContent.ENTITY.get(),PaCooledEntity::tick):null;}
}
