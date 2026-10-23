package com.hbm.machine.arcfurnace;
import com.hbm.item.autogen.ScrapsItem;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
/** Exact5x5x5 source body plus rear support strip, offset2 and six native HE/inventory ports. */
public final class ArcFurnaceBlock extends DummyableBlock {
 public ArcFurnaceBlock(){super(Properties.of().strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}
 @Override protected MapCodec<? extends DummyableBlock> codec(){return MapCodec.unit(this);}@Override public int[] dimensions(){return new int[]{4,0,2,2,2,2};}@Override public int offset(){return 2;}
 private static List<BlockPos> extension(BlockPos core,Direction facing){int[] d=rotate(new int[]{4,0,3,-2,1,1},facing);var result=new ArrayList<BlockPos>();BlockPos.betweenClosed(core.offset(-d[4],-d[1],-d[2]),core.offset(d[5],d[0],d[3])).forEach(pos->result.add(pos.immutable()));return result;}
 @Override public BlockState getStateForPlacement(BlockPlaceContext context){var state=super.getStateForPlacement(context);if(state==null)return null;var core=coreForPlacement(context);for(var pos:extension(core,placementFacing(state)))if(!context.getLevel().isInWorldBounds(pos)||!context.getLevel().hasChunkAt(pos)||!pos.equals(context.getClickedPos())&&!context.getLevel().getBlockState(pos).canBeReplaced())return null;return state;}
 @Override public void setPlacedBy(Level world,BlockPos pos,BlockState state,LivingEntity placer,ItemStack item){super.setPlacedBy(world,pos,state,placer,item);if(world.isClientSide)return;Direction facing=placementFacing(state);BlockPos core=pos.relative(facing,-offset());safely(()->{for(var part:extension(core,facing)){if(part.equals(core))continue;Direction away=part.getY()<core.getY()?Direction.DOWN:part.getY()>core.getY()?Direction.UP:part.getX()<core.getX()?Direction.WEST:part.getX()>core.getX()?Direction.EAST:part.getZ()<core.getZ()?Direction.NORTH:Direction.SOUTH;world.setBlock(part,defaultBlockState().setValue(LEGACY_META,away.get3DDataValue()),UPDATE_ALL);}});for(var port:ArcFurnaceBlockEntity.ports(core,facing))setPort(world,port.pos(),true);}
 @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ArcFurnaceBlockEntity(pos,state):isPort(state)?new ArcFurnacePort(pos,state):null;}
 @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ArcFurnaceContent.ENTITY.get(),ArcFurnaceBlockEntity::tick):null;}
 @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level world,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(stack.getItem() instanceof ShovelItem&&!player.isShiftKeyDown()){if(!world.isClientSide){var core=findCore(world,pos);if(core!=null&&world.getBlockEntity(core) instanceof ArcFurnaceBlockEntity furnace){for(var material:furnace.liquids){var scrap=ScrapsItem.create(material.copy());if(!player.getInventory().add(scrap))world.addFreshEntity(new ItemEntity(world,hit.getLocation().x,hit.getLocation().y,hit.getLocation().z,scrap));}player.inventoryMenu.broadcastChanges();furnace.liquids.clear();furnace.setChanged();}}return ItemInteractionResult.sidedSuccess(world.isClientSide);}return super.useItemOn(stack,state,world,pos,player,hand,hit);}
}
