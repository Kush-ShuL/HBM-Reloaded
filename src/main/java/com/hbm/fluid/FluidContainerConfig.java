package com.hbm.fluid;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Original GeneralConfig toggles used by the container registry. */
public final class FluidContainerConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue CONTAINER_COMPAT;
    static {
        var builder = new ModConfigSpec.Builder();
        CONTAINER_COMPAT = builder.comment("控制流体容器配方；与其他模组的流体互通在 hbm-fluid-interop.toml 中配置。")
                .define(java.util.List.of("containers","compatibilityRecipesEnabled"), true);
        SPEC = builder.build();
    }
    private FluidContainerConfig() {}
}
