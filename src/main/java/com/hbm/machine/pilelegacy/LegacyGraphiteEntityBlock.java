package com.hbm.machine.pilelegacy;

import api.hbm.block.IBlowable;
import net.minecraft.core.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;

/** Only the five actual TE-bearing channel blocks create entities; only fuel implements the fan target contract. */
public class LegacyGraphiteEntityBlock extends LegacyGraphiteBlock implements EntityBlock {
    public LegacyGraphiteEntityBlock(LegacyPileKind kind){super(kind);}
    @Override public BlockEntity newBlockEntity(BlockPos pos,BlockState state){return switch(kind){case FUEL->new LegacyPileFuelEntity(pos,state);case SOURCE,PLUTONIUM->new LegacyPileSourceEntity(pos,state);case LITHIUM->new LegacyPileBreedingEntity(pos,state);case DETECTOR->new LegacyPileDetectorEntity(pos,state);default->throw new IllegalStateException("Not an entity channel: "+kind);};}
    @Override public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,BlockState state,BlockEntityType<T> type){return level.isClientSide?null:(world,pos,current,entity)->{if(entity instanceof LegacyPileEntity pile)pile.updateEntity();};}
    public static final class Fuel extends LegacyGraphiteEntityBlock implements IBlowable {
        public Fuel(){super(LegacyPileKind.FUEL);}
        @Override public void applyFan(Level level,BlockPos pos,Direction direction,int distance){if(level.getBlockEntity(pos) instanceof LegacyPileFuelEntity fuel){fuel.heat-=fuel.heat*.025;fuel.setChanged();}}
    }
}
