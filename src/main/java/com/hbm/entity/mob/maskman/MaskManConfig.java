package com.hbm.entity.mob.maskman;
import net.neoforged.fml.ModContainer;import net.neoforged.fml.config.ModConfig;import net.neoforged.neoforge.common.ModConfigSpec;
/** Original MobConfig12.M00..M04 and GeneralConfig's stat registration condition. */
public final class MaskManConfig {
 public static final ModConfigSpec SPEC;public static final ModConfigSpec.BooleanValue ENABLED,UNDERGROUND,REQUIRE_CRAFT_STAT;public static final ModConfigSpec.IntValue DELAY,MIN_RAD;
 static{var b=new ModConfigSpec.Builder();ENABLED=b.define(java.util.List.of("spawning","enabled"),true);DELAY=b.comment("面具人可生成前的等待时间，单位：秒，默认 1200。").defineInRange(java.util.List.of("spawning","delaySeconds"),1200,1,Integer.MAX_VALUE);MIN_RAD=b.defineInRange(java.util.List.of("spawning","minimumRadiation"),50,0,2500);UNDERGROUND=b.define(java.util.List.of("spawning","undergroundEnabled"),true);REQUIRE_CRAFT_STAT=b.comment("要求玩家先合成或放置结晶器。").define(java.util.List.of("spawning","requireCrystallizerDiscovery"),true);SPEC=b.build();}
 public static void register(ModContainer container){container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-maskman-common.toml");}private MaskManConfig(){}
}
