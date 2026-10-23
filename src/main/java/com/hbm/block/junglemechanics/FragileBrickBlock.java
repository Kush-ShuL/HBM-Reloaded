package com.hbm.block.junglemechanics;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
/** Original FragileBrick, including the source's second explicit neighbor notification after destroying itself. */
public final class FragileBrickBlock extends Block {
 public FragileBrickBlock(){super(JungleMechanicsContent.properties(0));}@Override protected MapCodec<? extends Block> codec(){return MapCodec.unit(this);}
 @Override protected List<ItemStack> getDrops(BlockState state,LootParams.Builder params){return JungleDrops.drops(this,state,params,0,false);}
 private void notifySourceNeighbors(Level level,BlockPos pos){for(var direction:new Direction[]{Direction.DOWN,Direction.UP,Direction.NORTH,Direction.SOUTH,Direction.WEST,Direction.EAST}){var neighbor=pos.relative(direction);if(level.getBlockState(neighbor).is(this))level.scheduleTick(neighbor,this,level.random.nextInt(4)+8);}}
 @Override protected void tick(BlockState state,ServerLevel level,BlockPos pos,RandomSource random){level.destroyBlock(pos,false);notifySourceNeighbors(level,pos);}
 @Override public void stepOn(Level level,BlockPos pos,BlockState state,Entity entity){if(!level.isClientSide){level.destroyBlock(pos,false);notifySourceNeighbors(level,pos);}}
 @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(state.getBlock()!=next.getBlock())notifySourceNeighbors(level,pos);super.onRemove(state,level,pos,next,moving);}
}
