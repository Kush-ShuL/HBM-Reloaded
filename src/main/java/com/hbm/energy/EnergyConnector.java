package com.hbm.energy;

/** Native HE network participant. Implementations are used on the server thread. */
public interface EnergyConnector {
    /** Block entities must return false while removed or otherwise unavailable. */
    default boolean isLoaded() {
        return true;
    }
}
