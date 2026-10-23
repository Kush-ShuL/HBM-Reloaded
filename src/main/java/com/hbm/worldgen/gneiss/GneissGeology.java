package com.hbm.worldgen.gneiss;

import com.google.gson.Gson;
import com.hbm.worldgen.ore.OreWorldgen;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Source SchistStratum host and only its eight actually generated gneiss ore populations. */
public final class GneissGeology {
    private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,"hbm");
    public record Profile(String symbol,String sourceConfig,int constantCount,int multiplier,int size,int minY,int variance){}
    private record Definitions(List<Profile> profiles){}
    public static final List<Profile> PROFILES=profiles();
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue STRATUM,DEEP_STRATUM;
    public static final ModConfigSpec.IntValue DEEP_STRATUM_OFFSET,LITHIUM,GAS_SHALE;
    static {
        var config=new ModConfigSpec.Builder();
        STRATUM=config.comment("在主世界生成片岩地层；不受普通矿物生成总开关控制。").define("gneissStratum",true);
        DEEP_STRATUM=config.comment("Explicit1.18+ negative-Y mirror of the real host and its ores; obeys existing deepslateOre and deep band/count settings. Positive generation is unchanged.").define("deepGneissStratum",true);
        DEEP_STRATUM_OFFSET=config.comment("Deep stratum center relative to minBuildHeight; default30 maps the oldY30 center to minBuildHeight+30. Ore centers follow the same translation.").defineInRange("deepStratumOffset",30,0,512);
        LITHIUM=config.comment("片麻岩中锂矿脉的生成次数。").defineInRange("lithiumSpawn",6,0,10000);
        GAS_SHALE=config.comment("片麻岩中含气页岩的生成次数参数；实际矿脉次数为此值的 3 倍。").defineInRange("gassshaleSpawn",5,0,10000);
        SPEC=config.build();
    }
    private static List<Profile> profiles(){try(var stream=GneissGeology.class.getResourceAsStream("/hbm/gneiss-geology.json")){if(stream==null)throw new IllegalStateException("Missing gneiss-geology.json; run tools/port_gneiss_geology.py");return List.copyOf(new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),Definitions.class).profiles());}catch(java.io.IOException failure){throw new IllegalStateException(failure);}}
    public static int count(Profile profile){int base=switch(profile.sourceConfig()){case "constant"->profile.constantCount();case "lithiumSpawn"->LITHIUM.get();case "gassshaleSpawn"->GAS_SHALE.get();default->OreWorldgen.configuredCount(profile.sourceConfig());};return base*profile.multiplier();}
    public static int deepBase(net.minecraft.world.level.WorldGenLevel level){return level.getMinBuildHeight()+DEEP_STRATUM_OFFSET.get()-30;}
    public static int deepMin(net.minecraft.world.level.WorldGenLevel level){return level.getMinBuildHeight()+OreWorldgen.DEEP_BOTTOM_OFFSET.get();}
    public static int deepMax(net.minecraft.world.level.WorldGenLevel level){return Math.min(-1,Math.min(level.getMaxBuildHeight()-1,OreWorldgen.DEEP_TOP.get()));}
    public static boolean deep(){return DEEP_STRATUM.get()&&OreWorldgen.DEEP.get();}
    public static void register(IEventBus bus,ModContainer container){FEATURES.register("legacy_gneiss_stratum",GneissStratumFeature::new);FEATURES.register("legacy_gneiss_ores",GneissVeinsFeature::new);FEATURES.register(bus);container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-gneiss-geology-common.toml");}
    private GneissGeology(){}
}
