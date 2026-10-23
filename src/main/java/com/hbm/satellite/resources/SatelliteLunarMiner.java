package com.hbm.satellite.resources;
/** Same source15min/batch/pause/request lifecycle; all seven lunar pool entries remain in the sole ItemPool. */
public final class SatelliteLunarMiner extends SatelliteMiner {
    static {registerCargo(SatelliteLunarMiner.class,"POOL_SAT_LUNAR");}
    @Override public String getType(){return "LUNAR_MINER";}
    @Override protected String itemKind(){return "miner_lunar";}
}
