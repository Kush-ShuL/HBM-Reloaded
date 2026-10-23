package com.hbm.machine.conveyorextraction;

import com.hbm.conveyor.crane.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/** Original one successful stack per tick, then the one-item retry for restricted targets. */
public final class CraneInserterEntity extends CraneBaseEntity {
    public boolean destroyer=true;
    public CraneInserterEntity(BlockPos pos,BlockState state){super(CraneExtractionContent.INSERTER_ENTITY.get(),pos,state,21);}
    @Override protected String nameKey(){return "container.craneInserter";}
    @Override protected void serverTick(ServerLevel world){
        if(!world.hasNeighborSignal(worldPosition)){
            Direction output=getOutputSide(),side=opposite(output);var at=relative(worldPosition,output);
            var tile=world.hasChunkAt(at)?world.getBlockEntity(at):null;
            if(tile instanceof Container inventory){
                int[] access=CraneInventoryTransfer.access(inventory,side);boolean didSomething=false;
                for(int i=0;i<slots.length;i++)if(!slots[i].isEmpty()){
                    var remaining=CraneInventoryTransfer.addToInventory(inventory,access,slots[i].copy(),side);
                    if(remaining.isEmpty()||remaining.getCount()!=slots[i].getCount()){slots[i]=remaining;setChanged();didSomething=true;break;}
                }
                if(!didSomething)for(int i=0;i<slots.length;i++)if(!slots[i].isEmpty()){
                    var single=slots[i].copyWithCount(1);var remaining=CraneInventoryTransfer.addToInventory(inventory,access,single.copy(),side);
                    if(remaining.isEmpty()||remaining.getCount()!=single.getCount()){removeItem(i,1);setChanged();break;}
                }
            }
        }
        synchronize(15);
    }
    @Override public boolean canPlaceItem(int slot,ItemStack stack){return true;}
    @Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return true;}
    @Override public int[] getSlotsForFace(Direction side){return java.util.stream.IntStream.range(0,21).toArray();}
    @Override protected void saveCrane(CompoundTag tag){tag.putBoolean("destroyer",destroyer);}
    @Override protected void loadCrane(CompoundTag tag){destroyer=tag.getBoolean("destroyer");}
    public boolean hasPermission(Player player){return player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<400;}
    public void toggle(){destroyer=!destroyer;}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new CraneExtractionMenu(id,inventory,this,false);}
}
