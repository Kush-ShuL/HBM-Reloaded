package com.hbm.satellite;
/** Exact original ItemDrive.EnumDriveType order. Data identity, not a fabricated registered drive item. */
public enum SatelliteDriveType {FLASH_EMPTY,DISK_EMPTY,FLASH_BROKEN,DISK_BROKEN,FLASH_FLIGHTSIM,FLASH_PARTICLESIM,DISK_FLIGHTDATA,DISK_FLIGHTDATA_PROCESSED,DISK_ORBITDATA,DISK_ORBITDATA_PROCESSED,KLAUS;
 public static SatelliteDriveType safe(int ordinal){return values()[Math.abs(ordinal%values().length)];}
}
