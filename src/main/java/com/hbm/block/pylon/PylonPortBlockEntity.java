package com.hbm.block.pylon;

import com.hbm.energy.EnergyConductor;
import com.hbm.energy.HbmEnergy;
import com.hbm.energy.PowerNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original TileEntityProxyConductor: substation corner ports expose the core conductor. */
public final class PylonPortBlockEntity extends BlockEntity implements EnergyConductor,com.hbm.multiblock.proxy.SourceRebindableProxy {
    public PylonPortBlockEntity(BlockPos pos,BlockState state) { super(PylonContent.PORT_ENTITY.get(),pos,state); }
    // The source proxy is an all-sided connector; UNINOS resolves its own occupied position.
    // Requiring a loaded core BE would break a substation split across chunk boundaries.
    @Override public PowerNode energyNode() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.energyNode();} return level instanceof ServerLevel server?HbmEnergy.nodespace(server).getNode(worldPosition):null; }
    @Override public boolean isLoaded() {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.isLoaded();} return level!=null&&!isRemoved(); }
    @Override public boolean canConnect(Direction side) {var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null){return binding.canConnect(side);} return side!=null; }

 @Override protected void saveAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.save(this,tag);}

 @Override protected void loadAdditional(net.minecraft.nbt.CompoundTag tag,net.minecraft.core.HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);com.hbm.multiblock.proxy.SourceProxyBindings.load(this,tag);}

 @Override public void setChanged(){super.setChanged();var binding=com.hbm.multiblock.proxy.SourceProxyBindings.get(this);if(binding!=null)binding.setChanged();}
}
