package com.hbm.machine.pedestal;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** User opt-in for recipe-viewer disclosure; actual tablet gameplay is unchanged. */
public final class PedestalViewerConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue SHOW_RECIPES;
    private static boolean registered;
    static {
        var builder = new ModConfigSpec.Builder();
        SHOW_RECIPES = builder.comment("Explicitly show pedestal/clay-tablet recipes in JEI/REI. Default false. Restart the client or reload the recipe viewer after changing.")
            .translation("config.hbm.pedestal.show_recipes_in_viewers")
            .define("showRecipesInViewers", false);
        SPEC = builder.build();
    }
    public static boolean showRecipesInViewers() { return SPEC.isLoaded() && SHOW_RECIPES.get(); }
    public static void register(ModContainer container) {
        if (!registered) {
            registered = true;
            container.registerConfig(ModConfig.Type.CLIENT, SPEC, "hbm-pedestal-client.toml");
        }
    }
    private PedestalViewerConfig() { }
}
