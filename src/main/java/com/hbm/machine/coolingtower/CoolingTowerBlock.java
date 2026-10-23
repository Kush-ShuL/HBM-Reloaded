package com.hbm.machine.coolingtower;
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
/** Source exact5x19x5/9x13x9 dummy spaces, offset2/4, real cardinal combo proxies. */
public final class CoolingTowerBlock extends DummyableBlock {
 public final CoolingTowerKind kind;
 public CoolingTowerBlock(CoolingTowerKind kind){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());this.kind=kind;}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return simpleCodec(p->new CoolingTowerBlock(kind));}
 @Override public int[] dimensions(){int r=kind.radius();return new int[]{kind==CoolingTowerKind.SMALL?18:12,0,r,r,r,r};}
 @Override public int offset(){return kind.radius();}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new CoolingTowerBlockEntity(pos,state):isPort(state)?new CoolingTowerProxy(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,CoolingTowerContent.ENTITIES.get(kind).get(),CoolingTowerBlockEntity::tick):null;}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack item){super.setPlacedBy(level,pos,state,placer,item);if(level.isClientSide)return;var core=pos.relative(placementFacing(state),-offset());for(var port:CoolingTowerBlockEntity.ports(kind,core))setPort(level,port.position(),true);}
}
