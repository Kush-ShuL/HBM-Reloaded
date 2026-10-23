package com.hbm.weapon.sedna.gun.quadro;
import java.util.function.Supplier;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
public final class QuadroSettings {
    private static final ModConfigSpec.Builder BUILDER=new ModConfigSpec.Builder();
    private static final ModConfigSpec.DoubleValue VALUE=BUILDER.defineInRange(java.util.List.of("damage","quadro"),40D,0D,(double)Float.MAX_VALUE);
    public static final Supplier<Float> DAMAGE=()->VALUE.get().floatValue();
    private QuadroSettings(){}
    public static void register(ModContainer container){container.registerConfig(ModConfig.Type.SERVER,BUILDER.build(),"hbm-sedna-quadro.toml");}
}
