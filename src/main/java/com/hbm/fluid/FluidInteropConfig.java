package com.hbm.fluid;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** The native fluid graph stays private unless an installation explicitly opts in. */
public final class FluidInteropConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ALLOW_EXTERNAL_FLUIDS;
    private static boolean registered;

    static {
        var builder = new ModConfigSpec.Builder();
        ALLOW_EXTERNAL_FLUIDS = builder.comment(
                "Allow other mods to exchange fluids with HBM pipes, machines and containers.",
                "Disabled by default. Enable on both client and server, then restart.",
                "HBM's internal tanks and networks always use native HBM fluid identities.")
                .define("allowExternalFluidInterop", false);
        SPEC = builder.build();
    }

    private FluidInteropConfig() {}

    public static void register(ModContainer container) {
        // STARTUP is loaded synchronously by registerConfig, before registry suppliers run.
        container.registerConfig(ModConfig.Type.STARTUP, SPEC, "hbm-fluid-interop.toml");
        registered = true;
    }

    public static boolean enabled() {
        return registered && ALLOW_EXTERNAL_FLUIDS.get();
    }

    public static boolean sharedContainerTags() {
        return enabled() && FluidContainerConfig.CONTAINER_COMPAT.get();
    }
}
