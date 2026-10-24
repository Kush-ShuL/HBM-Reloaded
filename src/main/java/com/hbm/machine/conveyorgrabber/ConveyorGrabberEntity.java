package com.hbm.machine.conveyorgrabber;

import api.hbm.conveyor.IConveyorBelt;
import com.hbm.conveyor.MovingItem;
import com.hbm.conveyor.belt.ConveyorContent;
import com.hbm.conveyor.crane.*;
import com.hbm.machine.conveyorpackaging.*;
import com.hbm.pneumatic.PatternMatcher;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

/** Real TileEntityCraneGrabber, with the sole PatternMatcher and original cooldown / belt-vs-inventory branches. */
public final class ConveyorGrabberEntity extends CraneBaseEntity implements CraneBaseEntity.Filter {
    public boolean isWhitelist;
    public final PatternMatcher matcher=new PatternMatcher(9);
    public long lastGrabbedTick;
    public ConveyorGrabberEntity(BlockPos pos,BlockState state){super(ConveyorGrabberContent.ENTITY.get(),pos,state,11);}
    @Override protected String nameKey(){return "container.craneGrabber";}
    @Override public int[] getFilterSlots(){return new int[]{0,9};}
    @Override public void nextMode(int index){matcher.nextMode(slots[index],index);setChanged();}
    public boolean matchesFilter(ItemStack stack){for(int i=0;i<9;i++)if(!slots[i].isEmpty()&&matcher.isValidForFilter(slots[i],i,stack))return true;return false;}
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return false;}
    @Override protected void serverTick(ServerLevel world){
        int delay=20;
        if(CraneMetaUpgradeItem.isEjector(slots[10]))delay=switch(CraneMetaUpgradeItem.level(slots[10])){case 0->10;case 1->5;case 2->2;default->20;};
        if(world.getGameTime()>=lastGrabbedTick+delay&&!world.hasNeighborSignal(worldPosition)){
            int amount=1;
            if(CraneMetaUpgradeItem.isStack(slots[9]))amount=switch(CraneMetaUpgradeItem.level(slots[9])){case 0->4;case 1->16;case 2->64;default->1;};
            var input=getInputSide();var output=getOutputSide();var target=relative(worldPosition,output);var block=world.getBlockState(target).getBlock();
            if(block instanceof IConveyorBelt belt){
                for(var item:world.getEntitiesOfClass(MovingItem.class,inputBox(world,input))){
                    if(item.isRemoved())continue;var stack=item.getItemStack();if(stack.isEmpty()){item.discard();continue;}if(rejected(stack))continue;
                    lastGrabbedTick=world.getGameTime();
                    double dx=output==null?0:output.getStepX(),dy=output==null?0:output.getStepY(),dz=output==null?0:output.getStepZ();
                    var snap=belt.getClosestSnappingPosition(world,target.getX(),target.getY(),target.getZ(),new Vec3(worldPosition.getX()+.5+dx*.55,worldPosition.getY()+.5+dy*.55,worldPosition.getZ()+.5+dz*.55));
                    var moved=new MovingItem(world);moved.setItemStack(stack.copy());moved.setPos(snap);item.discard();world.addFreshEntity(moved);break;
                }
            }else if(world.getBlockEntity(target)instanceof Container inventory){
                var insertionSide=opposite(output);var access=CraneInventoryTransfer.access(inventory,insertionSide);
                for(var item:world.getEntitiesOfClass(MovingItem.class,inputBox(world,input))){
                    if(item.isRemoved())continue;var stack=item.getItemStack();if(stack.isEmpty()){item.discard();continue;}if(rejected(stack))continue;
                    // Source throttles after a matching attempt even when the target accepts no items.
                    lastGrabbedTick=world.getGameTime();int toAdd=Math.min(stack.getCount(),amount);var copy=stack.copyWithCount(toAdd);
                    var remainder=CraneInventoryTransfer.addToInventory(inventory,access,copy,insertionSide);int inserted=toAdd-(remainder.isEmpty()?0:remainder.getCount());stack.shrink(inserted);
                    if(stack.isEmpty())item.discard();amount-=inserted;if(amount<=0)break;
                }
            }
        }
        synchronize(15);
    }
    private boolean rejected(ItemStack stack){boolean matches=matchesFilter(stack);return isWhitelist&&!matches||!isWhitelist&&matches;}
    private AABB inputBox(ServerLevel world,Direction input){
        double reach=1;
        if(getBlockState().getValue(CraneBaseBlock.META)>1){var block=world.getBlockState(relative(worldPosition,input)).getBlock();if(block==ConveyorContent.DOUBLE.get())reach=.5;if(block==ConveyorContent.TRIPLE.get())reach=.33;}
        double x=worldPosition.getX()+(input==null?0:input.getStepX())*reach,y=worldPosition.getY()+(input==null?0:input.getStepY())*reach,z=worldPosition.getZ()+(input==null?0:input.getStepZ())*reach;
        return new AABB(x+.1875,y+.1875,z+.1875,x+.8125,y+.8125,z+.8125);
    }
    public boolean hasPermission(Player player){return player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<400;}
    public void receiveControl(Player player,CompoundTag data){
        if(!hasPermission(player))return;
        if(data.contains("whitelist")){isWhitelist=!isWhitelist;setChanged();}
        if(data.contains("slot")){int index=data.getInt("slot");if(index>=0&&index<9){var stack=ItemStack.parseOptional(level.registryAccess(),data.getCompound("stack"));setItem(index,stack.isEmpty()?ItemStack.EMPTY:stack.copyWithCount(1));nextMode(index);}}
    }
    @Override protected void saveNetwork(CompoundTag data){data.putBoolean("isWhitelist",isWhitelist);matcher.write(data);}
    @Override protected void loadNetwork(CompoundTag data){isWhitelist=data.getBoolean("isWhitelist");matcher.read(data);}
    @Override protected void saveCrane(CompoundTag data){saveNetwork(data);data.putLong("lastGrabbedTick",lastGrabbedTick);}
    @Override protected void loadCrane(CompoundTag data){loadNetwork(data);lastGrabbedTick=data.getLong("lastGrabbedTick");}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new ConveyorGrabberMenu(id,inventory,this);}
}
