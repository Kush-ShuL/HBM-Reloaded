package com.hbm.world.structure.vending;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
/** Original nested TileEntityVendingMachine owns no inventory, tick or custom persistent data. */
public final class VendingMachineBlockEntity extends BlockEntity {
    int removedVariant=-1; // Transient native destruction context, never a second saved variant.
    public VendingMachineBlockEntity(BlockPos pos,BlockState state){super(VendingMachineContent.ENTITY.get(),pos,state);}
}
