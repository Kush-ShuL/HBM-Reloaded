package com.hbm.fusion.plasma;
/** Original IFusionPowerReceiver: plasma heat is shared; neutron energy is not. It is neither HE nor a fluid. */
public interface FusionPowerReceiver {
    boolean receivesFusionPower();
    void receiveFusionPower(long fusionPower,double neutronPower,float red,float green,float blue);
}
