package com.hbm.hazard;

/** Original defaults, configurable by the shared server configuration when integrated. */
public record HazardSettings(boolean hot,boolean blinding,boolean hydroactive,boolean explosive,
                             boolean mode528,boolean lbsm,boolean safeCrates,boolean safeMEDrives,
                             int droppedItemTickRate,boolean offhand) {
    public static final HazardSettings DEFAULT=new HazardSettings(true,true,true,true,false,false,true,true,2,true);
    public HazardSettings { if(droppedItemTickRate<1)throw new IllegalArgumentException("Drop hazard tick rate must be positive"); }
}
