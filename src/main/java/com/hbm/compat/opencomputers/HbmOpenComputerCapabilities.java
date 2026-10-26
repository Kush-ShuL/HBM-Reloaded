package com.hbm.compat.opencomputers;

import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;

/** Public OC capability identities, without depending on OC implementation classes. */
public final class HbmOpenComputerCapabilities {
    public static final BlockCapability<Environment, Direction> ENVIRONMENT = BlockCapability.createSided(
            ResourceLocation.fromNamespaceAndPath("opencomputers", "environment"), Environment.class);
    public static final BlockCapability<SidedEnvironment, Direction> SIDED_ENVIRONMENT = BlockCapability.createSided(
            ResourceLocation.fromNamespaceAndPath("opencomputers", "sided_environment"), SidedEnvironment.class);

    private HbmOpenComputerCapabilities() {}
}
