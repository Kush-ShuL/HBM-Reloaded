package com.hbm.machine.electricpress;

import api.hbm.conveyor.IConveyorBelt;
import com.hbm.api.tool.ToolableBlock;
import com.hbm.item.StampItem;
import com.hbm.multiblock.DummyableBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.*;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

/** Source belt exists in the middle dummy cell; the block below must be the real press core. No container GUI exists. */
public final class ConveyorPressBlock extends DummyableBlock implements IConveyorBelt,ToolableBlock {
    public ConveyorPressBlock(){super(Properties.of().mapColor(net.minecraft.world.level.material.MapColor.METAL).strength(5,6).sound(SoundType.STONE).noOcclusion().requiresCorrectToolForDrops());}
    @Override protected MapCodec<? extends ConveyorPressBlock> codec(){return MapCodec.unit(this);}
    @Override public int[] dimensions(){return new int[]{2,0,0,0,0,0};}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return isCore(state)?new ConveyorPressEntity(pos,state):null;}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return isCore(state)?createTickerHelper(type,ElectricPressContent.CONVEYOR_ENTITY.get(),PoweredPressEntity::tick):null;}
    public ConveyorPressEntity core(Level level,BlockPos pos){var core=findCore(level,pos);return core!=null&&level.getBlockEntity(core) instanceof ConveyorPressEntity machine?machine:null;}
    @Override protected ItemInteractionResult useItemOn(ItemStack stack,BlockState state,Level level,BlockPos pos,Player player,InteractionHand hand,BlockHitResult hit){if(level.isClientSide)return ItemInteractionResult.SUCCESS;var machine=core(level,pos);if(machine==null)return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;if(stack.getItem() instanceof StampItem&&machine.slots.get(0).isEmpty()){machine.slots.set(0,stack.copyWithCount(1));stack.shrink(1);level.playSound(null,pos,com.hbm.registry.HbmSounds.get("item.upgradePlug"),SoundSource.BLOCKS,1,1);machine.setChanged();level.sendBlockUpdated(pos,state,state,UPDATE_CLIENTS);return ItemInteractionResult.CONSUME;}return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;}
    @Override protected InteractionResult useWithoutItem(BlockState state,Level level,BlockPos pos,Player player,BlockHitResult hit){return level.isClientSide?InteractionResult.SUCCESS:InteractionResult.PASS;}
    @Override public boolean onScrew(Level level,Player player,BlockPos pos,Direction side,float x,float y,float z,ToolType tool){if(tool!=ToolType.SCREWDRIVER)return false;var machine=core(level,pos);if(machine==null||machine.slots.get(0).isEmpty())return false;if(!level.isClientSide){var stack=machine.slots.get(0);if(!player.getInventory().add(stack.copy()))level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY()+1,pos.getZ()+.5,stack.copy()));else player.inventoryMenu.broadcastChanges();machine.slots.set(0,ItemStack.EMPTY);machine.setChanged();}return true;}
    private Direction travelDirection(Level level,int x,int y,int z){var below=level.getBlockState(new BlockPos(x,y-1,z));int source=(below.hasProperty(LEGACY_META)?below.getValue(LEGACY_META):0)-10;return source>=2&&source<=5?Direction.from3DDataValue(source).getClockWise():source==0?Direction.DOWN:source==1?Direction.UP:null;}
    @Override public boolean canItemStay(Level level,int x,int y,int z,Vec3 item){var state=level.getBlockState(new BlockPos(x,y-1,z));return state.is(this)&&isCore(state);}
    @Override public Vec3 getClosestSnappingPosition(Level level,int x,int y,int z,Vec3 item){var dir=travelDirection(level,x,y,z);double px=x+.5,pz=z+.5;if(dir!=null&&dir.getStepX()!=0)px=Mth.clamp(item.x,x,x+1);if(dir!=null&&dir.getStepZ()!=0)pz=Mth.clamp(item.z,z,z+1);return new Vec3(px,y+.25,pz);}
    @Override public Vec3 getTravelLocation(Level level,int x,int y,int z,Vec3 item,double speed){var dir=travelDirection(level,x,y,z);var snap=getClosestSnappingPosition(level,x,y,z,item);var motion=snap.subtract(dir==null?Vec3.ZERO:new Vec3(dir.getStepX()*speed,dir.getStepY()*speed,dir.getStepZ()*speed)).subtract(item);double length=motion.length();return item.add(motion.x/length*speed,motion.y/length*speed,motion.z/length*speed);}
}
