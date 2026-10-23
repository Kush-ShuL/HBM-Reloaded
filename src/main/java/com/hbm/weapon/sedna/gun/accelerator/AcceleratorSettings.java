package com.hbm.weapon.sedna.gun.accelerator;
import java.util.function.Supplier;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Original four reloadable server damage suppliers, initialized by the actual factory. */
public final class AcceleratorSettings {
    private static final ModConfigSpec.Builder BUILDER=new ModConfigSpec.Builder();
    private static ModConfigSpec spec;
    private static java.util.List<String> configPath(String key){return switch(key){
        case "GUN_TAU_CANNON_DAMAGE" -> java.util.List.of("damage","tauCannon");
        case "GUN_TAU_CANNON_CHARGE_DAMAGE" -> java.util.List.of("damage","tauCannonCharge");
        case "GUN_COILGUN_DAMAGE" -> java.util.List.of("damage","coilgun");
        case "GUN_NI4NI_DAMAGE" -> java.util.List.of("damage","ni4ni");
        default -> throw new IllegalArgumentException("Unknown configuration definition "+key);
    };}
    public static Supplier<Float> value(String key,float fallback){var field=BUILDER.defineInRange(configPath(key),(double)fallback,-Double.MAX_VALUE,Double.MAX_VALUE);return ()->spec!=null&&spec.isLoaded()?field.get().floatValue():fallback;}
    public static void register(ModContainer container){AcceleratorFactory.initConfig();spec=BUILDER.build();container.registerConfig(ModConfig.Type.SERVER,spec,"hbm-accelerator-guns-server.toml");}
    private AcceleratorSettings(){}
}
