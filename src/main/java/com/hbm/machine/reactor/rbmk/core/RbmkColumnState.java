package com.hbm.machine.reactor.rbmk.core;
/** Actual source heat/water/steam fields exposed without copying or inventing another inventory. */
public interface RbmkColumnState extends RbmkFluxColumn {
 double heat();void heat(double value);int reasimWater();void reasimWater(int value);int reasimSteam();void reasimSteam(int value);double passiveCooling(int neighbors);void changed();
}
