package com.hbm.railway.track;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** X5846 custom train rails; deliberately distinct from ordinary minecart RailBlock. */
public interface IRailNTM {
    Vec3 getSnappingPos(Level level, BlockPos position, double trainX, double trainY, double trainZ);
    Vec3 getTravelLocation(Level level, BlockPos position, double trainX, double trainY, double trainZ,
            double motionX, double motionY, double motionZ, double speed, RailContext info, MoveContext context);
    TrackGauge getGauge();
    enum TrackGauge { STANDARD, NARROW }
    final class RailContext {
        public float yaw; public double overshoot; public BlockPos pos;
        public RailContext yaw(float value){yaw=value;return this;}
        public RailContext dist(double value){overshoot=value;return this;}
        public RailContext pos(BlockPos value){pos=value;return this;}
    }
    final class MoveContext {
        public final RailCheckType type; public final double collisionBogieDistance;
        public boolean collision; public double overshoot;
        public MoveContext(RailCheckType type,double collisionBogieDistance){this.type=type;this.collisionBogieDistance=collisionBogieDistance;}
    }
    enum RailCheckType { CORE, FRONT, BACK, OTHER }
}
