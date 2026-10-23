package com.hbm.machine.conveyorextraction;

import api.hbm.conveyor.*;
import com.hbm.conveyor.crane.*;
import net.minecraft.core.*;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Source immediate insertion, local buffer and optional overflow destruction, including actual packages. */
public final class CraneInserterBlock extends CraneBaseBlock implements IEnterableBlock {
    @Override public CraneInserterEntity createCrane(BlockPos pos,BlockState state){return new CraneInserterEntity(pos,state);}
    @Override protected boolean hasAnalogOutputSignal(BlockState state){return true;}
    @Override protected int getAnalogOutputSignal(BlockState state,Level level,BlockPos pos){return level.getBlockEntity(pos)instanceof CraneBaseEntity crane?net.minecraft.world.inventory.AbstractContainerMenu.getRedstoneSignalFromContainer(crane):0;}
    @Override public String[] sourceTextures(){return CraneExtractionIcons.INSERTER.clone();}
    @Override public boolean canItemEnter(Level world,int x,int y,int z,Direction side,IConveyorItem item){return getInputSide(world,new BlockPos(x,y,z))==side;}
    @Override public boolean canPackageEnter(Level world,int x,int y,int z,Direction side,IConveyorPackage pack){return true;}
    @Override public void onItemEnter(Level world,int x,int y,int z,Direction side,IConveyorItem item){
        if(item==null||item.getItemStack()==null||item.getItemStack().isEmpty())return;
        insert(world,new BlockPos(x,y,z),new ItemStack[]{item.getItemStack().copy()});
    }
    @Override public void onPackageEnter(Level world,int x,int y,int z,Direction side,IConveyorPackage pack){if(pack!=null&&pack.getItemStacks()!=null&&pack.getItemStacks().length>0)insert(world,new BlockPos(x,y,z),pack.getItemStacks());}
    private void insert(Level world,BlockPos pos,ItemStack[] contents){
        Direction output=getOutputSide(world,pos),accessSide=CraneBaseEntity.opposite(output);
        var target=world.getBlockEntity(CraneBaseEntity.relative(pos,output));
        if(!world.hasNeighborSignal(pos)&&target instanceof Container inventory){
            int[] access=CraneInventoryTransfer.access(inventory,accessSide);
            for(var stack:contents)CraneInventoryTransfer.addToInventory(inventory,access,stack,accessSide);
        }
        for(var stack:contents)if(!stack.isEmpty()&&world.getBlockEntity(pos)instanceof CraneInserterEntity inserter){
            CraneInventoryTransfer.addToInventory(inserter,null,stack,accessSide);
            if(!stack.isEmpty()&&!inserter.destroyer)world.addFreshEntity(new ItemEntity(world,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,stack.copy()));
        }
    }
}
