package com.hbm.machine.deuterium;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
public final class DeuteriumTowerBlock extends DummyableBlock {
 public DeuteriumTowerBlock(){super(Properties.of().strength(10,12).sound(SoundType.STONE).requiresCorrectToolForDrops().noOcclusion());}
 @Override protected MapCodec<? extends DeuteriumTowerBlock> codec(){return MapCodec.unit(this);}
 @Override public int[] dimensions(){return new int[]{9,0,1,0,0,1};}
 @Override public void setPlacedBy(Level level,BlockPos pos,BlockState state,LivingEntity placer,ItemStack stack){super.setPlacedBy(level,pos,state,placer,stack);if(!level.isClientSide)for(var c:DeuteriumTowerEntity.connections(pos,placementFacing(state)))if(!c.origin().equals(pos))setPort(level,c.origin(),true);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new DeuteriumTowerEntity(pos,state):isPort(state)&&state.getValue(LEGACY_META)>=8?new DeuteriumPort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,DeuteriumContent.TOWER_ENTITY.get(),DeuteriumEntity::tick):null;}
}
