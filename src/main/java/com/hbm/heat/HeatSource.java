package com.hbm.heat;
/** Original IHeatSource: bounded native TU, separate from HE and every external capability. */
public interface HeatSource {int getHeatStored();void useUpHeat(int heat);}
