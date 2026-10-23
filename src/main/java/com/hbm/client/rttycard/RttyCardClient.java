package com.hbm.client.rttycard;

import com.hbm.compat.computercraft.rtty.RttyCardUpgrades;
import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent;
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller;
import net.neoforged.bus.api.IEventBus;

/** CC's native flat-item modeller displays the original card icon on either real turtle side. */
public final class RttyCardClient {
    public static void register(IEventBus bus) { bus.addListener(RttyCardClient::modellers); }
    private static void modellers(RegisterTurtleModellersEvent event) { event.register(RttyCardUpgrades.TURTLE.get(), TurtleUpgradeModeller.flatItem()); }
    private RttyCardClient() { }
}
