package com.hbm.machine.pilelegacy;

import api.hbm.block.IPileNeutronReceiver;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

/** Source detector deliberately is not TileEntityPileBase: it receives flux but is never a cached PILE stream node. */
public final class LegacyPileDetectorEntity extends LegacyPileEntity implements IPileNeutronReceiver {
    public int lastNeutrons,neutrons,maxNeutrons=10;
    public LegacyPileDetectorEntity(BlockPos pos,BlockState state){super(LegacyPileContent.DETECTOR.get(),pos,state);}
    @Override protected boolean streamBase(){return false;}
    @Override public void updateEntity(){if(level.isClientSide)return;var block=(LegacyGraphiteBlock)getBlockState().getBlock();if(neutrons>=maxNeutrons&&(metadata()&8)>0)block.triggerRods(level,worldPosition);if(neutrons<maxNeutrons&&lastNeutrons<maxNeutrons&&(metadata()&8)==0)block.triggerRods(level,worldPosition);lastNeutrons=neutrons;neutrons=0;setChanged();}
    @Override public void receiveNeutrons(int value){neutrons+=value;}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("maxNeutrons",maxNeutrons);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);maxNeutrons=tag.getInt("maxNeutrons");}
    @Override public List<ProgressView> progressBars(){return List.of(new ProgressView("gui.hbm.pile_legacy.flux",lastNeutrons,maxNeutrons,0x61bfe3));}
}
