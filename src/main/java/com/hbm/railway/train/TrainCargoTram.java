package com.hbm.railway.train;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
/** Actual 29-slot electric flat-bed tram; source engine acceleration .01 and cap .5, no active fuel gate. */
public final class TrainCargoTram extends RailCarEntity {
    public TrainCargoTram(EntityType<? extends TrainCargoTram> type,Level level){super(type,level,false);}
    public TrainCargoTram(Level level){this(RailTrainContent.TRAM.get(),level);}
}
