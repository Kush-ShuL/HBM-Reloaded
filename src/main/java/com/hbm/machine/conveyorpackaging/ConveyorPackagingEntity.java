package com.hbm.machine.conveyorpackaging;

import api.hbm.conveyor.IConveyorBelt;
import com.hbm.conveyor.*;
import com.hbm.conveyor.crane.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/** Source Boxer21-slot/full-stack thresholds and Unboxer23-slot/redstone-ejection behavior. */
public final class ConveyorPackagingEntity extends CraneBaseEntity {
    public final boolean unboxer;public byte mode;private boolean lastRedstone;
    private static final int[] ACCESS=java.util.stream.IntStream.range(0,21).toArray();
    public ConveyorPackagingEntity(BlockPos pos,BlockState state,boolean unboxer){super((unboxer?ConveyorPackagingContent.UNBOXER_ENTITY:ConveyorPackagingContent.BOXER_ENTITY).get(),pos,state,unboxer?23:21);this.unboxer=unboxer;}
    @Override protected String nameKey(){return unboxer?"container.craneUnboxer":"container.craneBoxer";}
    @Override protected void serverTick(ServerLevel world){if(unboxer){unbox(world);return;}boolean redstone=world.hasNeighborSignal(worldPosition);if(mode==3&&redstone&&!lastRedstone)box(world,0);if(lastRedstone!=redstone){lastRedstone=redstone;setChanged();}if(mode!=3&&world.getGameTime()%2==0)box(world,switch(mode){case 0->4;case 1->8;case 2->16;default->1;});synchronize(15);}
    private static Vec3 snap(IConveyorBelt belt,ServerLevel world,BlockPos core,Direction output){var at=relative(core,output);double dx=output==null?0:output.getStepX(),dy=output==null?0:output.getStepY(),dz=output==null?0:output.getStepZ();return belt.getClosestSnappingPosition(world,at.getX(),at.getY(),at.getZ(),new Vec3(core.getX()+.5+dx*.55,core.getY()+.5+dy*.55,core.getZ()+.5+dz*.55));}
    private void box(ServerLevel world,int required){var output=getOutputSide();var at=relative(worldPosition,output);if(!(world.getBlockState(at).getBlock()instanceof IConveyorBelt belt))return;int count=0;for(var stack:slots)if(!stack.isEmpty()&&(required==0||stack.getCount()==stack.getMaxStackSize()))count++;if(required==0?count==0:count<required)return;int pack=required==0?count:required;var contents=new ItemStack[pack];for(int i=0;i<slots.length&&pack>0;i++)if(!slots[i].isEmpty()&&(required==0||slots[i].getCount()==slots[i].getMaxStackSize())){contents[--pack]=slots[i].copy();slots[i]=ItemStack.EMPTY;}var moving=new MovingPackage(world);moving.setPos(snap(belt,world,worldPosition,output));moving.setItemStacks(contents);world.addFreshEntity(moving);setChanged();}
    private void unbox(ServerLevel world){int delay=20;if(CraneMetaUpgradeItem.isEjector(slots[22]))delay=switch(CraneMetaUpgradeItem.level(slots[22])){case 0->10;case 1->5;case 2->2;default->20;};if(world.getGameTime()%delay!=0||world.hasNeighborSignal(worldPosition))return;int amount=1;if(CraneMetaUpgradeItem.isStack(slots[21]))amount=switch(CraneMetaUpgradeItem.level(slots[21])){case 0->4;case 1->16;case 2->64;default->1;};var output=getInputSide();var at=relative(worldPosition,output);if(!(world.getBlockState(at).getBlock()instanceof IConveyorBelt belt))return;for(int i=0;i<21;i++)if(!slots[i].isEmpty()){var stack=slots[i].copy();int count=Math.min(amount,stack.getCount());removeItem(i,count);stack.setCount(count);var moving=new MovingItem(world);moving.setPos(snap(belt,world,worldPosition,output));moving.setItemStack(stack);world.addFreshEntity(moving);break;}}
    public boolean hasPermission(Player player){return player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<400;}
    public void toggle(){mode=(byte)((mode+1)%4);setChanged();}
    @Override protected void saveNetwork(CompoundTag tag){if(!unboxer)tag.putByte("mode",mode);}
    @Override protected void loadNetwork(CompoundTag tag){if(!unboxer)mode=tag.getByte("mode");}
    @Override protected void saveCrane(CompoundTag tag){if(!unboxer){tag.putByte("mode",mode);tag.putBoolean("lastRedstone",lastRedstone);}}
    @Override protected void loadCrane(CompoundTag tag){if(!unboxer){mode=tag.getByte("mode");lastRedstone=tag.getBoolean("lastRedstone");}}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return true;}
    @Override public int[] getSlotsForFace(Direction side){return ACCESS.clone();}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return unboxer;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new ConveyorPackagingMenu(id,inventory,this,unboxer,worldPosition);}
}
