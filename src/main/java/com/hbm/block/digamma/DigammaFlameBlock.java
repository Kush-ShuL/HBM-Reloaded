package com.hbm.block.digamma;

import com.hbm.exposure.HbmExposure;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.*;

/** Exact source contact DIGAMMA0.05, no ordinary fire damage/spread/extinguishing loop. */
public final class DigammaFlameBlock extends Block {
 public DigammaFlameBlock(){super(Properties.of().mapColor(MapColor.FIRE).strength(0,90).lightLevel(state->15).noCollission().noOcclusion().noLootTable());}
 @Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.empty();}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return Shapes.empty();}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder context){return List.of();}
 @Override protected boolean canSurvive(BlockState state,LevelReader level,BlockPos pos){return level.getBlockState(pos.below()).isFaceSturdy(level,pos.below(),Direction.UP);}
 @Override protected void neighborChanged(BlockState state,Level level,BlockPos pos,Block block,BlockPos from,boolean moving){if(!canSurvive(state,level,pos))level.removeBlock(pos,false);}
 @Override protected void entityInside(BlockState state,Level level,BlockPos pos,Entity entity){if(entity instanceof LivingEntity living)HbmExposure.contaminateDigamma(living,.05F,false);}
}
