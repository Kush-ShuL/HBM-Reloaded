package com.hbm.world.structure.generated.algorithm.legacy;
import com.hbm.world.structure.generated.compat.World;
/** Preserve FIFO source room-job ordering while planning the persistent native pieces. */
public final class TimedGenerator {public interface ITimedJob{void work();}public static void addOp(World world,ITimedJob job){world.defer(job::work);}}
