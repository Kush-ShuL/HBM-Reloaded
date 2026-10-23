package com.hbm.energy;

import java.util.Set;

/** Network membership independent of a particular world's position type. */
public interface PowerLink {
    PowerNetwork network();
    void setNetwork(PowerNetwork network);
    Set<EnergyProvider> providers();
    Set<EnergyReceiver> receivers();
}
