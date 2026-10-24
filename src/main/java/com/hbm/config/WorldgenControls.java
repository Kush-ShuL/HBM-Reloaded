package com.hbm.config;

import java.util.*;
import net.minecraft.util.RandomSource;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Optional final overrides over the original module configurations; defaults consume no extra random draws. */
public final class WorldgenControls {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue ORES,STRUCTURES,OIL_SURFACE_INDICATORS,MODDED_DIMENSIONS;
    private static final Map<String,ModConfigSpec.BooleanValue> FEATURES=new LinkedHashMap<>();
    private static final Map<String,Rule> RULES=new LinkedHashMap<>();
    static {
        var builder=new ModConfigSpec.Builder();
        ORES=builder.comment("Master switch for every HBM mineral/geological feature. Existing per-module settings still apply.").define("oresEnabled",true);
        MODDED_DIMENSIONS=builder.comment("允许在其他模组维度执行 HBM 的地表生成入口。默认开启；主世界、下界和末地仍按各自生成规则处理。").define("moddedDimensionsEnabled",true);
        STRUCTURES=builder.comment("Master switch for HBM natural buildings and hidden red rooms. Affects new generation only.").define("structuresEnabled",true);
        builder.comment("-1 表示沿用对应生成模块的设置；高度参数用 -2049 表示沿用。高度均为绝对 Y 坐标，正高度与深层分支可分别设置。").push("features");
        rule(builder,"overworld_ores","ore_uranium","vein");
        rule(builder,"overworld_ores","ore_thorium","vein");
        rule(builder,"overworld_ores","ore_titanium","vein");
        rule(builder,"overworld_ores","ore_sulfur","vein");
        rule(builder,"overworld_ores","ore_aluminium","vein");
        rule(builder,"overworld_ores","ore_fluorite","vein");
        rule(builder,"overworld_ores","ore_niter","vein");
        rule(builder,"overworld_ores","ore_tungsten","vein");
        rule(builder,"overworld_ores","ore_lead","vein");
        rule(builder,"overworld_ores","ore_beryllium","vein");
        rule(builder,"overworld_ores","ore_rare","vein");
        rule(builder,"overworld_ores","ore_lignite","vein");
        rule(builder,"overworld_ores","ore_asbestos","vein");
        rule(builder,"overworld_ores","ore_cinnebar","vein");
        rule(builder,"overworld_ores","ore_cobalt","vein");
        rule(builder,"overworld_ores","cluster_iron","vein");
        rule(builder,"overworld_ores","cluster_titanium","vein");
        rule(builder,"overworld_ores","cluster_aluminium","vein");
        rule(builder,"overworld_ores","cluster_copper","vein");
        rule(builder,"overworld_ores","coltan_random","vein");
        rule(builder,"overworld_ores","coltan_deposit","vein");
        rule(builder,"overworld_ores","australium","vein");
        rule(builder,"deepslate_ores","ore_uranium","vein");
        rule(builder,"deepslate_ores","ore_thorium","vein");
        rule(builder,"deepslate_ores","ore_titanium","vein");
        rule(builder,"deepslate_ores","ore_sulfur","vein");
        rule(builder,"deepslate_ores","ore_aluminium","vein");
        rule(builder,"deepslate_ores","ore_fluorite","vein");
        rule(builder,"deepslate_ores","ore_niter","vein");
        rule(builder,"deepslate_ores","ore_tungsten","vein");
        rule(builder,"deepslate_ores","ore_lead","vein");
        rule(builder,"deepslate_ores","ore_beryllium","vein");
        rule(builder,"deepslate_ores","ore_rare","vein");
        rule(builder,"deepslate_ores","ore_lignite","vein");
        rule(builder,"deepslate_ores","ore_asbestos","vein");
        rule(builder,"deepslate_ores","ore_cinnebar","vein");
        rule(builder,"deepslate_ores","ore_cobalt","vein");
        rule(builder,"deepslate_ores","cluster_iron","vein");
        rule(builder,"deepslate_ores","cluster_titanium","vein");
        rule(builder,"deepslate_ores","cluster_aluminium","vein");
        rule(builder,"deepslate_ores","cluster_copper","vein");
        rule(builder,"deepslate_ores","coltan_random","vein");
        rule(builder,"deepslate_ores","coltan_deposit","vein");
        rule(builder,"deepslate_ores","australium","vein");
        rule(builder,"nether_ores","ore_nether_uranium","vein");
        rule(builder,"nether_ores","ore_nether_tungsten","vein");
        rule(builder,"nether_ores","ore_nether_sulfur","vein");
        rule(builder,"nether_ores","ore_nether_fire","vein");
        rule(builder,"nether_ores","ore_nether_coal","vein");
        rule(builder,"nether_ores","ore_nether_cobalt","vein");
        rule(builder,"nether_ores","ore_nether_plutonium","vein");
        rule(builder,"end_ores","ore_tikite","vein");
        rule(builder,"gneiss_ores","ore_gneiss_iron","vein");
        rule(builder,"gneiss_ores","ore_gneiss_gold","vein");
        rule(builder,"gneiss_ores","ore_gneiss_uranium","vein");
        rule(builder,"gneiss_ores","ore_gneiss_copper","vein");
        rule(builder,"gneiss_ores","ore_gneiss_asbestos","vein");
        rule(builder,"gneiss_ores","ore_gneiss_lithium","vein");
        rule(builder,"gneiss_ores","ore_gneiss_rare","vein");
        rule(builder,"gneiss_ores","ore_gneiss_gas","vein");
        rule(builder,"deep_gneiss_ores","ore_gneiss_iron","vein");
        rule(builder,"deep_gneiss_ores","ore_gneiss_gold","vein");
        rule(builder,"deep_gneiss_ores","ore_gneiss_uranium","vein");
        rule(builder,"deep_gneiss_ores","ore_gneiss_copper","vein");
        rule(builder,"deep_gneiss_ores","ore_gneiss_asbestos","vein");
        rule(builder,"deep_gneiss_ores","ore_gneiss_lithium","vein");
        rule(builder,"deep_gneiss_ores","ore_gneiss_rare","vein");
        rule(builder,"deep_gneiss_ores","ore_gneiss_gas","vein");
        rule(builder,"depth_deposits","cluster_depth_iron","deposit");
        rule(builder,"depth_deposits","cluster_depth_titanium","deposit");
        rule(builder,"depth_deposits","cluster_depth_tungsten","deposit");
        rule(builder,"depth_deposits","ore_depth_cinnebar","deposit");
        rule(builder,"depth_deposits","ore_depth_zirconium","deposit");
        rule(builder,"depth_deposits","ore_depth_borax","deposit");
        rule(builder,"deep_depth_deposits","cluster_depth_iron","deposit");
        rule(builder,"deep_depth_deposits","cluster_depth_titanium","deposit");
        rule(builder,"deep_depth_deposits","cluster_depth_tungsten","deposit");
        rule(builder,"deep_depth_deposits","ore_depth_cinnebar","deposit");
        rule(builder,"deep_depth_deposits","ore_depth_zirconium","deposit");
        rule(builder,"deep_depth_deposits","ore_depth_borax","deposit");
        rule(builder,"layer_deposits","hematite","layer");
        rule(builder,"layer_deposits","bauxite","layer");
        rule(builder,"layer_deposits","malachite","layer");
        rule(builder,"deep_layer_deposits","hematite","layer");
        rule(builder,"deep_layer_deposits","bauxite","layer");
        rule(builder,"deep_layer_deposits","malachite","layer");
        rule(builder,"ore_caves","sulfur","cave");
        rule(builder,"ore_caves","asbestos","cave");
        rule(builder,"deep_ore_caves","sulfur","cave");
        rule(builder,"deep_ore_caves","asbestos","cave");
        rule(builder,"gneiss_stratum","stone_gneiss","cave");
        rule(builder,"deep_gneiss_stratum","stone_gneiss","cave");
        rule(builder,"nether_depth","lower","deposit");
        rule(builder,"nether_depth","upper","deposit");
        rule(builder,"nether_smolder","ore_nether_smoldering","surface");
        rule(builder,"nether_geyser","geysir_nether","surface");
        rule(builder,"overworld_bedrock","bedrock_ore_base","chance");
        rule(builder,"nether_bedrock","weighted_resources","chance");
        rule(builder,"red_rooms","stone_strange","chance_height");
        rule(builder,"soyuz_capsules","landing_capsule","chance");
        rule(builder,"old_lantern","lantern_behemoth","chance");
        rule(builder,"glyphid_hives","small_hive","chance");
        rule(builder,"radiation_hotspots","sellafield_crater","chance_height");
        rule(builder,"natural_events","blueprint_chest","grid");
        rule(builder,"pink_trees","pink_log","chance_height");
        rule(builder,"reeds","river","chance_height");
        rule(builder,"reeds","beach","chance_height");
        rule(builder,"wild_flowers","foxglove","chance_height");
        rule(builder,"wild_flowers","nightshade","chance_height");
        rule(builder,"wild_flowers","tobacco","chance_height");
        rule(builder,"wild_flowers","weed","chance_height");
        rule(builder,"gas_bubbles","flammable","chance_vein");
        rule(builder,"gas_bubbles","explosive","chance_vein");
        rule(builder,"oil_terrain","oil_bubbles","chance_height");
        rule(builder,"oil_terrain","bedrock_oil","chance_height");
        rule(builder,"oil_terrain","oil_sand","chance_height");
        builder.push("oil_terrain");
        OIL_SURFACE_INDICATORS=builder.comment("在含油地层上方生成地表油泉、油污地面、枯死植被和含油垂柳。仅影响新生成区块，地下油藏另行配置。").define("surfaceIndicators",true);
        builder.pop();
        builder.pop();SPEC=builder.build();
    }
    private static void rule(ModConfigSpec.Builder builder,String feature,String name,String kind){
        if(!FEATURES.containsKey(feature)) {builder.push(feature);FEATURES.put(feature,builder.define("enabled",true));builder.pop();}
        builder.push(feature).push(name);RULES.put(feature+"/"+name,new Rule(builder,kind));builder.pop().pop();
    }
    public static boolean ores(){return ORES.get();}
    public static boolean moddedDimensions(){return MODDED_DIMENSIONS.get();}
    public static boolean structures(){return STRUCTURES.get();}
    public static boolean oilSurfaceIndicators(){return OIL_SURFACE_INDICATORS.get();}
    public static boolean feature(String name){var value=FEATURES.get(name);if(value==null)throw new IllegalArgumentException("Unknown HBM generation feature "+name);return value.get();}
    public static boolean oreFeature(String name){return ores()&&feature(name);}
    public static Rule rule(String feature,String name){var rule=RULES.get(feature+"/"+name);if(rule==null)throw new IllegalArgumentException("Unknown HBM generation profile "+feature+"/"+name);return rule;}
    public static final class Rule {
        private final ModConfigSpec.BooleanValue enabled;
        private final Map<String,ModConfigSpec.IntValue> integers=new HashMap<>();
        private final Map<String,ModConfigSpec.DoubleValue> decimals=new HashMap<>();
        private Rule(ModConfigSpec.Builder b,String kind){
            enabled=b.define("enabled",true);
            switch(kind){
                case "vein"->{integer(b,"count",-1,-1,10000);integer(b,"size",-1,-1,64);heights(b);}
                case "chance_vein"->{integer(b,"chanceDenominator",-1,-1,1000000);integer(b,"count",-1,-1,10000);integer(b,"size",-1,-1,64);heights(b);}
                case "deposit"->{integer(b,"chanceDenominator",-1,-1,1000000);integer(b,"size",-1,-1,32);heights(b);decimal(b,"fill",-1,-1,1);}
                case "layer"->{heights(b);decimal(b,"threshold",-1,-1,1000000);decimal(b,"horizontalScale",-1,-1,100);decimal(b,"verticalScale",-1,-1,100);}
                case "cave"->{integer(b,"centerY",-2049,-2049,2048);integer(b,"rangeMultiplier",-1,-1,128);integer(b,"maxRange",-1,-1,128);decimal(b,"threshold",-1,-1,1000000);decimal(b,"horizontalScale",-1,-1,100);}
                case "surface"->{integer(b,"count",-1,-1,10000);heights(b);}
                case "grid"->{integer(b,"gridX",-1,-1,30000000);integer(b,"gridZ",-1,-1,30000000);}
                case "chance"->integer(b,"chanceDenominator",-1,-1,1000000);
                case "chance_height"->{integer(b,"chanceDenominator",-1,-1,1000000);heights(b);}
                default->throw new IllegalArgumentException(kind);
            }
        }
        private void integer(ModConfigSpec.Builder b,String key,int def,int min,int max){integers.put(key,b.defineInRange(key,def,min,max));}
        private void decimal(ModConfigSpec.Builder b,String key,double def,double min,double max){decimals.put(key,b.defineInRange(key,def,min,max));}
        private void heights(ModConfigSpec.Builder b){integer(b,"minY",-2049,-2049,2048);integer(b,"maxY",-2049,-2049,2048);}
        public boolean enabled(){return enabled.get();}
        public boolean overrides(String key){var v=integers.get(key);return v!=null&&v.get()!=(key.endsWith("Y")?-2049:-1);}
        public int integer(String key,int original){var v=integers.get(key);if(v==null)return original;int n=v.get();return n==(key.endsWith("Y")?-2049:-1)?original:n;}
        public int count(int original){return integer("count",original);}
        public int size(int original){return Math.max(1,integer("size",original));}
        /** Zero disables this branch without calling nextInt(0). */
        public int chance(int original){return integer("chanceDenominator",original);}
        public double decimal(String key,double original){var v=decimals.get(key);return v==null||v.get()==-1D?original:v.get();}
        public int height(RandomSource random,int originalMin,int originalMax,boolean originalDraw){
            int low=integer("minY",originalMin),high=Math.max(low,integer("maxY",originalMax));
            return low+((originalDraw||overrides("minY")||overrides("maxY"))?random.nextInt(high-low+1):0);
        }
    }
    public static void register(ModContainer container){container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-worldgen-controls-common.toml");}
    private WorldgenControls(){}
}
