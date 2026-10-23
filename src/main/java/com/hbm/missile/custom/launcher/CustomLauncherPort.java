package com.hbm.missile.custom.launcher;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.fluid.*;
import com.hbm.fluid.network.StandardFluidReceiver;
import com.hbm.multiblock.DummyableBlock;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Actual TileEntityDummy delegation: no second inventory, power buffer or fluid tank exists at any port. */
public class CustomLauncherPort extends BlockEntity implements WorldlyContainer, MachineView, EnergyReceiver, SidedEnergyConnector, StandardFluidReceiver {
    public CustomLauncherPort(BlockPos pos, BlockState state) { this(CustomLauncherContent.COMPACT_PORT.get(), pos, state); }
    protected CustomLauncherPort(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }
    @Override public boolean isValidBlockState(BlockState state) { return super.isValidBlockState(state) && DummyableBlock.isPort(state); }
    public CustomLauncherEntity core() {
        if (level == null || !(getBlockState().getBlock() instanceof CustomLauncherBlock block)) return null;
        var core = block.findCore(level, worldPosition); return core != null && level.getBlockEntity(core) instanceof CustomLauncherEntity machine ? machine : null;
    }
    @Override public boolean isLoaded() { return !isRemoved() && core() != null; }
    @Override public boolean canConnect(Direction side) { var core = core(); return core != null && core.canConnect(side); }
    @Override public boolean canConnect(HbmFluidType type, Direction side) { return canConnect(side); }
    @Override public long getPower() { var core = core(); return core == null ? 0 : core.getPower(); }
    @Override public long getMaxPower() { return CustomLauncherEntity.MAX_POWER; }
    @Override public void setPower(long value) { var core = core(); if (core != null) core.setPower(value); }
    @Override public long transferPower(long value) { var core = core(); return core == null ? value : core.transferPower(value); }
    @Override public FluidTank[] getAllTanks() { var core = core(); return core == null ? FluidTank.EMPTY_ARRAY : core.tanks; }
    @Override public FluidTank[] getReceivingTanks() { return getAllTanks(); }
    @Override public long transferFluid(HbmFluidType type, int pressure, long amount) { var core = core(); return core == null ? amount : core.transferFluid(type, pressure, amount); }
    @Override public long energyStored() { return getPower(); }
    @Override public long energyCapacity() { return getMaxPower(); }
    @Override public List<TankView> tanks() { var core = core(); return core == null ? List.of() : core.tanks(); }
    @Override public int getContainerSize() { return core() == null ? 0 : 8; }
    @Override public boolean isEmpty() { var core = core(); return core == null || core.isEmpty(); }
    @Override public ItemStack getItem(int slot) { var core = core(); return core == null ? ItemStack.EMPTY : core.getItem(slot); }
    @Override public ItemStack removeItem(int slot, int count) { var core = core(); return core == null ? ItemStack.EMPTY : core.removeItem(slot, count); }
    @Override public ItemStack removeItemNoUpdate(int slot) { var core = core(); return core == null ? ItemStack.EMPTY : core.removeItemNoUpdate(slot); }
    @Override public void setItem(int slot, ItemStack stack) { var core = core(); if (core != null) core.setItem(slot, stack); }
    @Override public void clearContent() { var core = core(); if (core != null) core.clearContent(); }
    @Override public void setChanged() { super.setChanged(); var core = core(); if (core != null) core.setChanged(); }
    @Override public boolean stillValid(Player player) { var core = core(); return core != null && core.stillValid(player); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return false; }
    @Override public int[] getSlotsForFace(Direction side) { return new int[]{0}; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }
}
