package com.hbm.machine.pilelegacy;

import api.hbm.block.IPileNeutronReceiver;
import com.hbm.config.GeneralModes;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

/** Original lithium breeding: exact integer neutron accumulation and two secondary streams, not a fabricated recipe. */
public final class LegacyPileBreedingEntity extends LegacyPileEntity implements IPileNeutronReceiver {
    public int neutrons,lastNeutrons,progress;
    public LegacyPileBreedingEntity(BlockPos pos,BlockState state){super(LegacyPileContent.BREEDING.get(),pos,state);}
    public int maxProgress(){return GeneralModes.mode528()?50000:30000;}
    @Override public void updateEntity(){if(level.isClientSide)return;lastNeutrons=neutrons;progress+=neutrons;neutrons=0;if(lastNeutrons>0)for(int i=0;i<2;i++)castRay(1);if(progress>=maxProgress())LegacyGraphiteBlock.replace(level,worldPosition,LegacyPileKind.TRITIUM,metadata(),3);setChanged();}
    @Override public void receiveNeutrons(int value){neutrons+=value;setChanged();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("progress",progress);tag.putInt("neutrons",neutrons);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);progress=tag.getInt("progress");neutrons=tag.getInt("neutrons");}
    @Override public List<ProgressView> progressBars(){return List.of(new ProgressView("gui.hbm.pile_legacy.depletion",progress,maxProgress(),0x83be62));}
}
