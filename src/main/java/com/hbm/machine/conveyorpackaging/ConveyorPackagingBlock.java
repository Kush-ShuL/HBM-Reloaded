package com.hbm.machine.conveyorpackaging;

import api.hbm.conveyor.*;
import com.hbm.conveyor.crane.*;
import net.minecraft.core.*;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Real separate source Boxer/Unboxer entrance rules, including Boxer's intentionally unrestricted package entrance. */
public final class ConveyorPackagingBlock extends CraneBaseBlock implements IEnterableBlock {
    public final boolean unboxer;
    private static final String[] BOX={"crane_top","crane_side","crane_in","crane_side_in","crane_box","crane_side_box","crane_boxer_top","crane_boxer_side_up","crane_boxer_side_down","crane_boxer_top_left","crane_boxer_top_right","crane_boxer_side_left_turn_up","crane_boxer_side_right_turn_up","crane_boxer_side_left_turn_down","crane_boxer_side_right_turn_down","crane_boxer_side_up_turn_left","crane_boxer_side_up_turn_right","crane_boxer_side_down_turn_left","crane_boxer_side_down_turn_right"};
    private static final String[] UNBOX={"crane_top","crane_side","crane_in","crane_side_in","crane_box","crane_side_box","crane_unboxer_top","crane_unboxer_side_down","crane_unboxer_side_up","crane_unboxer_top_right","crane_unboxer_top_left","crane_unboxer_side_up_turn_left","crane_unboxer_side_up_turn_right","crane_unboxer_side_down_turn_left","crane_unboxer_side_down_turn_right","crane_unboxer_side_left_turn_up","crane_unboxer_side_right_turn_up","crane_unboxer_side_left_turn_down","crane_unboxer_side_right_turn_down"};
    public ConveyorPackagingBlock(boolean unboxer){this.unboxer=unboxer;}
    @Override public ConveyorPackagingEntity createCrane(BlockPos pos,BlockState state){return new ConveyorPackagingEntity(pos,state,unboxer);}
    @Override protected boolean hasAnalogOutputSignal(BlockState state){return true;}
    @Override protected int getAnalogOutputSignal(BlockState state,Level level,BlockPos pos){return level.getBlockEntity(pos)instanceof CraneBaseEntity crane?net.minecraft.world.inventory.AbstractContainerMenu.getRedstoneSignalFromContainer(crane):0;}
    @Override public String[] sourceTextures(){return(unboxer?UNBOX:BOX).clone();}
    @Override public int sourceRotation(int meta,int override,int side){if(!unboxer)return super.sourceRotation(meta,override,side);if(meta>1&&side==1){int hand=handRotation(override<6?direction(override):CraneBaseEntity.opposite(direction(meta)),direction(meta));if(hand==1)return switch(meta){case 2->2;case 3->1;case 4->3;default->0;};if(hand==0)return switch(meta){case 2->1;case 3->2;case 4->0;case 5->3;default->0;};return switch(meta){case 2->0;case 3->3;case 4->2;case 5->1;default->0;};}return 0;}
    @Override public boolean canItemEnter(Level world,int x,int y,int z,Direction side,IConveyorItem item){return !unboxer&&getInputSide(world,new BlockPos(x,y,z))==side;}
    @Override public void onItemEnter(Level world,int x,int y,int z,Direction side,IConveyorItem item){if(world.getBlockEntity(new BlockPos(x,y,z))instanceof ConveyorPackagingEntity machine){var remainder=CraneInventoryTransfer.addToInventory(machine,machine.getSlotsForFace(side),item.getItemStack(),side);drop(world,x,y,z,remainder);}}
    @Override public boolean canPackageEnter(Level world,int x,int y,int z,Direction side,IConveyorPackage pack){return !unboxer||getOutputSide(world,new BlockPos(x,y,z))==side;}
    @Override public void onPackageEnter(Level world,int x,int y,int z,Direction side,IConveyorPackage pack){if(world.getBlockEntity(new BlockPos(x,y,z))instanceof ConveyorPackagingEntity machine){Direction access=CraneBaseEntity.opposite(getOutputSide(world,new BlockPos(x,y,z)));for(var stack:pack.getItemStacks())drop(world,x,y,z,CraneInventoryTransfer.addToInventory(machine,machine.getSlotsForFace(access),stack,access));}}
    private static void drop(Level world,int x,int y,int z,net.minecraft.world.item.ItemStack remainder){if(!remainder.isEmpty())world.addFreshEntity(new ItemEntity(world,x+.5,y+.5,z+.5,remainder.copy()));}
}
