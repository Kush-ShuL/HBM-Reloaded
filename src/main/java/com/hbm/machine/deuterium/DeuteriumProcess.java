package com.hbm.machine.deuterium;

/** Exact hardcoded source update and source NEI ratio; not a configurable recipe table absent from X5846. */
public final class DeuteriumProcess {
    public static final int WATER_PER_HEAVY=50,MINIMUM_WATER=100,VIEW_WATER=1000,VIEW_HEAVY=20;
    public static int convert(int water,int output,int outputCapacity) {
        return Math.min(Math.min(outputCapacity,water)/WATER_PER_HEAVY,outputCapacity-output);
    }
    private DeuteriumProcess() {}
}
