package com.hbm.machine.pilelegacy;

import com.hbm.api.machine.MachineView;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Source TileEntityPileBase has neither LoadedBase/muffler nor inventory/GUI/energy. */
public abstract class LegacyPileEntity extends BlockEntity implements MachineView {
    protected LegacyPileEntity(BlockEntityType<?> type,BlockPos pos,BlockState state){super(type,pos,state);}
    public abstract void updateEntity();
    public int metadata(){return LegacyGraphiteBlock.metadata(getBlockState());}
    protected void castRay(int flux){LegacyPileNeutrons.cast(this,flux);}
    protected boolean streamBase(){return true;}
    @Override public long energyStored(){return 0;}
    @Override public long energyCapacity(){return 0;}
    private void removeNode(){if(streamBase())LegacyPileNeutrons.remove(level,worldPosition);}
    @Override public void setRemoved(){removeNode();super.setRemoved();}
    @Override public void onChunkUnloaded(){removeNode();super.onChunkUnloaded();}
}
