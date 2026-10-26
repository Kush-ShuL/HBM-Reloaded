package com.hbm.compat.opencomputers.rtty;

import com.hbm.item.rttycard.RttyCardContent;
import li.cil.oc.api.driver.item.Slot;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.prefab.DriverItem;
import net.minecraft.world.item.ItemStack;

/** The original Tier 2 card installed in a native OC card slot. */
public final class RttyCardDriver extends DriverItem {
    public RttyCardDriver() { super(new ItemStack(RttyCardContent.CARD.get())); }

    @Override public String slot(ItemStack stack) { return Slot.Card; }
    @Override public int tier(ItemStack stack) { return 1; }
    @Override public ManagedEnvironment createEnvironment(ItemStack stack, EnvironmentHost host) {
        return host.world() == null || host.world().isClientSide ? null : new RttyCardEnvironment(host);
    }
}
