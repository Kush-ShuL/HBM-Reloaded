package com.hbm.worldgen.deposit;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Only the three real source OreLayer3D registrations; no duplicate ordinary ores. */
public final class OreDeposits {
    private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,"hbm");
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue HEMATITE,BAUXITE,MALACHITE,DEEP;
    public static final ModConfigSpec.DoubleValue DEEP_THRESHOLD_MULTIPLIER;
    static {
        var config=new ModConfigSpec.Builder();
        HEMATITE=config.comment("生成赤铁矿矿床，默认高度 Y=6～64。").define("enableHematite",true);
        BAUXITE=config.comment("生成铝土矿矿床，默认高度 Y=6～64。").define("enableBauxite",true);
        MALACHITE=config.comment("生成孔雀石矿床，默认高度 Y=6～64。").define("enableMalachite",true);
        DEEP=config.comment("额外在负高度生成矿床；同时受深层矿物开关与高度范围控制。正高度矿床不受此项影响。").define("deepDeposits",true);
        DEEP_THRESHOLD_MULTIPLIER=config.comment("负高度矿床的稀疏倍率：1 为默认密度，越大越稀少。正高度矿床不受影响。").defineInRange("deepThresholdMultiplier",1D,.01D,100D);
        SPEC=config.build();
    }
    public record Profile(String name,int metadata,double scaleH,double scaleV,double threshold) {}
    public static final List<Profile> PROFILES=List.of(new Profile("hematite",2,.04D,.25D,230),new Profile("bauxite",5,.03D,.15D,300),new Profile("malachite",3,.1D,.15D,275));
    public static int enabledMask(){return (HEMATITE.get()?1:0)|(BAUXITE.get()?2:0)|(MALACHITE.get()?4:0);}
    public static void register(IEventBus bus,ModContainer container){FEATURES.register("legacy_3d_ore_deposits",LegacyOreDepositsFeature::new);FEATURES.register(bus);container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-ore-deposits-common.toml");}
    private OreDeposits(){}
}
