package com.hbm.weapon.sedna.gun.flaregun;
import java.util.function.Supplier;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
public final class FlaregunSettings {
    private static final ModConfigSpec.Builder BUILDER=new ModConfigSpec.Builder();
    private static final ModConfigSpec.DoubleValue VALUE=BUILDER.defineInRange(java.util.List.of("damage","flare"),15D,0D,(double)Float.MAX_VALUE);
    public static final Supplier<Float> DAMAGE=()->VALUE.get().floatValue();
    private FlaregunSettings(){}
    public static void register(ModContainer container){container.registerConfig(ModConfig.Type.SERVER,BUILDER.build(),"hbm-sedna-flaregun.toml");}
}
