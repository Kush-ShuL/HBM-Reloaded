package com.hbm.fusion.klystron;
/** True source KyU receiver contract. Only actual ITER torus implements it; not an HE/plasma/fluid adapter. */
public interface KlystronReceiver {
    boolean acceptsKlystron();
    void receiveKlystron(long power);
}
