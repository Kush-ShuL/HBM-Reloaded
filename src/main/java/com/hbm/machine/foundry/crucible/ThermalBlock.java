package com.hbm.machine.foundry.crucible;
import com.hbm.item.autogen.ScrapsItem;
import com.hbm.material.MaterialStack;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.*;
/** Source offset3x3 firebox and hollow3x2x3 crucible, using the shared one-step core placement. */
public final class ThermalBlock extends DummyableBlock {
 public final boolean firebox;public ThermalBlock(boolean firebox){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());this.firebox=firebox;}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{firebox?0:1,0,1,1,1,1};}@Override public int offset(){return 1;}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?firebox?new FireboxBlockEntity(pos,state):new CrucibleBlockEntity(pos,state):new ThermalProxy(pos,state);}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){if(!isCore(state))return null;return firebox?createTickerHelper(type,CrucibleContent.FIREBOX_ENTITY.get(),FireboxBlockEntity::tick):createTickerHelper(type,CrucibleContent.CRUCIBLE_ENTITY.get(),CrucibleBlockEntity::tick);}
 private VoxelShape crucibleShape(BlockState state,BlockGetter level,BlockPos pos){BlockPos core=findCore(level,pos);if(core==null)return Shapes.block();double dx=core.getX()+.5-pos.getX(),dy=core.getY()-pos.getY(),dz=core.getZ()+.5-pos.getZ();double[][] bounds={{-1.5,0,-1.5,1.5,.5,1.5},{-1.25,.5,-1.25,1.25,1.5,-1},{-1.25,.5,-1.25,-1,1.5,1.25},{-1.25,.5,1,1.25,1.5,1.25},{1,.5,-1.25,1.25,1.5,1.25}};VoxelShape shape=Shapes.empty();for(var b:bounds)shape=Shapes.or(shape,Shapes.box(b[0]+dx,b[1]+dy,b[2]+dz,b[3]+dx,b[4]+dy,b[5]+dz));return Shapes.joinUnoptimized(shape,Shapes.block(),BooleanOp.AND);}
 @Override protected VoxelShape getCollisionShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return firebox?Shapes.block():crucibleShape(state,level,pos);}@Override protected VoxelShape getShape(BlockState state,BlockGetter level,BlockPos pos,CollisionContext context){return firebox?Shapes.block():crucibleShape(state,level,pos);}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(!firebox&&stack.getItem() instanceof ShovelItem&&!player.isShiftKeyDown()){if(!level.isClientSide){BlockPos core=findCore(level,pos);if(core!=null&&level.getBlockEntity(core) instanceof CrucibleBlockEntity crucible){for(var material:java.util.stream.Stream.concat(crucible.recipeStack.stream(),crucible.wasteStack.stream()).toList()){ItemStack scrap=ScrapsItem.create(material.copy());if(!player.getInventory().add(scrap))level.addFreshEntity(new ItemEntity(level,hit.getLocation().x,hit.getLocation().y,hit.getLocation().z,scrap));}player.inventoryMenu.broadcastChanges();crucible.recipeStack.clear();crucible.wasteStack.clear();crucible.setChanged();}}return ItemInteractionResult.sidedSuccess(level.isClientSide);}return super.useItemOn(stack,state,level,pos,player,hand,hit);}
 @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){if(!state.is(next.getBlock())&&!level.isClientSide&&level.getBlockEntity(pos) instanceof CrucibleBlockEntity crucible){for(var material:java.util.stream.Stream.concat(crucible.recipeStack.stream(),crucible.wasteStack.stream()).toList())level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,ScrapsItem.create(material.copy())));crucible.recipeStack.clear();crucible.wasteStack.clear();}super.onRemove(state,level,pos,next,moving);}
}
