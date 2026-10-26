package com.hbm.compat.opencomputers.media;

import com.hbm.compat.opencomputers.media.PwranglerMedia;
import li.cil.oc.api.FileSystem;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverItem;
import net.minecraft.world.item.ItemStack;

/** Native read-only OC floppy using the original OpenOS program, not the CC port. */
public final class PwranglerDiskDriver extends DriverItem {
    public PwranglerDiskDriver() { super(new ItemStack(PwranglerMedia.item())); }

    @Override public String slot(ItemStack stack) { return Slot.Floppy; }

    @Override public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
        if (host.world() == null || host.world().isClientSide) return null;
        var files = FileSystem.fromClass(PwranglerDiskDriver.class, "hbm", "disks/pwrangler");
        if (files == null) throw new IllegalStateException("Missing PWRangler disk resources");
        return FileSystem.asManagedEnvironment(FileSystem.asReadOnly(files), "PWRangler", host, "opencomputers:floppy_access");
    }
}
