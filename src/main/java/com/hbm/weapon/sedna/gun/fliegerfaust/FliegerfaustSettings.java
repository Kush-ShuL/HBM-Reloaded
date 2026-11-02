package com.hbm.weapon.sedna.gun.fliegerfaust;

import java.util.List;
import java.util.function.Supplier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class FliegerfaustSettings {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.DoubleValue VALUE = BUILDER.defineInRange(List.of("damage", "fliegerfaust"), 15D, 0D, (double)Float.MAX_VALUE);
    public static final Supplier<Float> DAMAGE = () -> VALUE.get().floatValue();

    private FliegerfaustSettings() { }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, BUILDER.build(), "hbm-sedna-fliegerfaust.toml");
    }
}
