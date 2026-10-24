package com.hbm.machine.reactor.iter.torus;

import com.hbm.satellite.SatelliteDriveType;
import java.util.Objects;
import net.minecraft.world.item.Item;

/** Fixed authored drive contents; satellite data and computer progress remain in their existing owners. */
public final class DriveItem extends Item {
    private final SatelliteDriveType type;
    public DriveItem(SatelliteDriveType type){super(new Properties().stacksTo(64));this.type=Objects.requireNonNull(type);}
    public SatelliteDriveType type(){return type;}
}
