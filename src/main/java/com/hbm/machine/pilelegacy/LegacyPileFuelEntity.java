package com.hbm.machine.pilelegacy;

import api.hbm.block.IPileNeutronReceiver;
import com.hbm.block.gas.*;
import com.hbm.config.GeneralModes;
import java.util.List;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Original heat feedback, fission, Pu239 transmutation, comparator notifications and actual radon meltdown. */
public final class LegacyPileFuelEntity extends LegacyPileEntity implements IPileNeutronReceiver {
    public static final int MAX_HEAT=1000;
    public int heat,neutrons,lastNeutrons,progress;
    public LegacyPileFuelEntity(BlockPos pos,BlockState state){super(LegacyPileContent.FUEL.get(),pos,state);if((metadata()&8)!=0)progress=maxProgress()-1000;}
    public int maxProgress(){return GeneralModes.mode528()?75000:50000;}
    @Override public void updateEntity(){
        if(level.isClientSide)return;
        heat-=(metadata()&4)!=0?heat*.065:heat*.05;
        int previous=react();
        int oldLevel=net.minecraft.util.Mth.clamp(previous*16/maxProgress(),0,15),newLevel=net.minecraft.util.Mth.clamp(progress*16/maxProgress(),0,15);
        if(oldLevel!=newLevel)level.updateNeighborsAt(worldPosition,getBlockState().getBlock());
        if((metadata()&8)!=0){if(progress<maxProgress()-1000)progress=maxProgress()-1000;}
        else if(progress>=maxProgress()-1000)level.setBlock(worldPosition,getBlockState().setValue(LegacyGraphiteBlock.META,metadata()|8),3);
        if(heat>=MAX_HEAT){level.explode(null,worldPosition.getX()+.5,worldPosition.getY()+.5,worldPosition.getZ()+.5,4,true,Level.ExplosionInteraction.BLOCK);level.setBlock(worldPosition,GasContent.BLOCKS_BY_KIND.get(GasKind.RADON_DENSE).get().defaultBlockState(),3);}
        // Intentionally continues after meltdown, just as the source update body does.
        if(level.random.nextFloat()*2F<=heat/(float)MAX_HEAT){double x=worldPosition.getX()+.25+level.random.nextDouble()*.5,z=worldPosition.getZ()+.25+level.random.nextDouble()*.5;LegacyPileSmoke.send(level,worldPosition,x,worldPosition.getY()+1,z);}
        if(progress>=maxProgress())LegacyGraphiteBlock.replace(level,worldPosition,LegacyPileKind.PLUTONIUM,metadata()&7,3);
        setChanged();
    }
    private int react(){int reaction=(int)(neutrons*(1D-((double)heat/(double)MAX_HEAT)*.5D));lastNeutrons=neutrons;neutrons=0;int previous=progress;progress+=reaction;if(reaction<=0)return previous;heat+=reaction;for(int i=0;i<12;i++)castRay((int)Math.max(reaction*.25,1));return previous;}
    @Override public void receiveNeutrons(int value){neutrons+=value;setChanged();}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.saveAdditional(tag,lookup);tag.putInt("heat",heat);tag.putInt("progress",progress);tag.putInt("neutrons",neutrons);}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider lookup){super.loadAdditional(tag,lookup);heat=tag.getInt("heat");progress=tag.getInt("progress");neutrons=tag.getInt("neutrons");}
    @Override public List<ProgressView> progressBars(){return List.of(new ProgressView("gui.hbm.pile_legacy.heat",heat,MAX_HEAT,0xff7048),new ProgressView("gui.hbm.pile_legacy.depletion",progress,maxProgress(),0x83be62));}
}
