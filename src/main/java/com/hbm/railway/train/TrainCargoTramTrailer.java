package com.hbm.railway.train;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
/** Actual 45-slot unpowered trailer, driven by its true coupled LTU rather than invented autonomous motion. */
public final class TrainCargoTramTrailer extends RailCarEntity {
    public TrainCargoTramTrailer(EntityType<? extends TrainCargoTramTrailer> type,Level level){super(type,level,true);}
    public TrainCargoTramTrailer(Level level){this(RailTrainContent.TRAILER.get(),level);}
}
