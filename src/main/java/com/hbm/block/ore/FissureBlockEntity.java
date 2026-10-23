package com.hbm.block.ore;

import com.hbm.fluid.FluidTank;
import com.hbm.fluid.HbmFluids;
import com.hbm.fluid.network.FluidPorts;
import com.hbm.fluid.network.StandardFluidSender;
import com.hbm.registry.HbmOres;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Source tank is transient; only TileEntityLoadedBase's muffled/tilted fields persist. */
public final class FissureBlockEntity extends BlockEntity implements StandardFluidSender, com.hbm.multiblock.MuffledMachine {
    public final FluidTank lava = new FluidTank(HbmFluids.LAVA, 1000);
    public boolean muffled, tilted;
    private FluidPorts ports;
    public FissureBlockEntity(BlockPos pos, BlockState state) { super(HbmOres.FISSURE_ENTITY.get(), pos, state); }
    public static void tick(ServerLevel level, FissureBlockEntity fissure) {
        if (fissure.ports == null) fissure.ports = new FluidPorts(fissure.worldPosition);
        // FluidPorts preserves source setupFluidPorts + updatePortFIFO before replenishment.
        fissure.ports.update(level, fissure);
        fissure.lava.setFill(1000);
    }
    @Override public FluidTank[] getAllTanks() { return new FluidTank[]{lava}; }
    @Override public FluidTank[] getSendingTanks() { return new FluidTank[]{lava}; }
    private void removePorts() { if (ports != null && level instanceof ServerLevel server) ports.remove(server); ports = null; }
    @Override public void setRemoved() { removePorts(); super.setRemoved(); }
    @Override public void onChunkUnloaded() { removePorts(); super.onChunkUnloaded(); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries); tag.putBoolean("muffled", muffled); tag.putBoolean("tilted", tilted);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries); muffled = tag.getBoolean("muffled"); tilted = tag.getBoolean("tilted");
    }

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
