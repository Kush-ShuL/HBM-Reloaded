package com.hbm.weapon.sedna.gun.fatman;
import java.util.function.Supplier;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
/** Original real ConfigWrapper default100, server-authoritative and safe before configuration load. */
public final class FatmanSettings {
 public static final ModConfigSpec SPEC;private static final ModConfigSpec.DoubleValue VALUE;
 static {var builder=new ModConfigSpec.Builder();VALUE=builder.defineInRange(java.util.List.of("damage","fatman"),100D,-Double.MAX_VALUE,Double.MAX_VALUE);SPEC=builder.build();}
 public static final Supplier<Float> DAMAGE=()->SPEC.isLoaded()?VALUE.get().floatValue():100F;
 public static void register(ModContainer container){container.registerConfig(ModConfig.Type.SERVER,SPEC,"hbm-sedna-fatman.toml");}
 private FatmanSettings(){}
}
