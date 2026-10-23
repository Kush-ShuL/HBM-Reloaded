package com.hbm.material.crystallizer;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
/** Actual BlockGraphite -> BlockFlammable(30,5) -> BlockBase, with metal material/sound. */
public final class GraphiteBlock extends Block {
 public GraphiteBlock(){super(Properties.of().mapColor(MapColor.METAL).strength(5,6).sound(SoundType.METAL).requiresCorrectToolForDrops());}
 @Override protected MapCodec<? extends GraphiteBlock> codec(){return simpleCodec(p->new GraphiteBlock());}
 @Override public int getFlammability(BlockState state,BlockGetter level,BlockPos pos,Direction side){return 5;}
 @Override public int getFireSpreadSpeed(BlockState state,BlockGetter level,BlockPos pos,Direction side){return 30;}
 public boolean shouldIgnite(Level level,BlockPos pos){for(Direction side:Direction.values())if(level.getBlockState(pos.relative(side)).is(Blocks.FIRE))return true;return false;}
 public void dismantle(Level level,BlockPos pos){level.removeBlock(pos,false);float x=level.random.nextFloat()*.6F+.2F,y=level.random.nextFloat()*.2F,z=level.random.nextFloat()*.6F+.2F;var entity=new ItemEntity(level,pos.getX()+x,pos.getY()+y+1,pos.getZ()+z,new ItemStack(this));entity.setDeltaMovement((float)level.random.nextGaussian()*.05F,(float)level.random.nextGaussian()*.05F+.2F,(float)level.random.nextGaussian()*.05F);if(!level.isClientSide)level.addFreshEntity(entity);}
}
