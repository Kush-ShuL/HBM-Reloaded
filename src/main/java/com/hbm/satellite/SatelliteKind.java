package com.hbm.satellite;
import java.util.Locale;
/** ItemSatellite.EnumSatType metadata order, independent from XSatelliteRegistry source class IDs. */
public enum SatelliteKind {SPY(0),SCANNER(1),RADAR(2),MINER_ASTRO(6),MINER_LUNAR(7),PRECISION_LASER(9),DEATH_RAY(3),XENIUM_RESONATOR(4),RELAY(5),DETECTOR(10),RAY_SCAN(11),SCIENCE(12),SCIENCE_ASSEMBLER(-1),SCIENCE_SENSOR(-1);
 public final int sourceClassId;SatelliteKind(int id){sourceClassId=id;}public String serializedName(){return name().toLowerCase(Locale.US);}public boolean ready(){return SatelliteRegistry.ready(this==SCIENCE_ASSEMBLER||this==SCIENCE_SENSOR?12:sourceClassId);}
}
