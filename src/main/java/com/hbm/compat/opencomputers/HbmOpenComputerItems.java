package com.hbm.compat.opencomputers;

import com.hbm.compat.opencomputers.media.PwranglerDiskDriver;
import com.hbm.compat.opencomputers.rtty.RttyCardDriver;
import li.cil.oc.api.Driver;

/** Called from common setup only after the opencomputers mod has been detected. */
public final class HbmOpenComputerItems {
    public static void registerDrivers() {
        Driver.add(new RttyCardDriver());
        Driver.add(new PwranglerDiskDriver());
    }

    private HbmOpenComputerItems() { }
}
