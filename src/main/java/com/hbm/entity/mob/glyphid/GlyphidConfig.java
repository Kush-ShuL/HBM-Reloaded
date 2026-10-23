package com.hbm.entity.mob.glyphid;
import java.util.*;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Actual source MobConfig Glyphid fields. The already-real chimney/pollution Rampant master remains unique. */
public final class GlyphidConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLE_HIVES,WAYPOINT_DEBUG,INFESTATION,SCOUT_NATURAL,EXTENDED,DIG,GUIDANCE,INITIAL_SCOUT;
    public static final ModConfigSpec.IntValue HIVE_SPAWN,SCOUT_SWARM_CHANCE,LARGE_HIVE_CHANCE,LARGE_HIVE_THRESHOLD,SWARM_SIZE,SOOT_STEP,COOLDOWN_SECONDS,SCOUT_NATURAL_CHANCE;
    public static final ModConfigSpec.DoubleValue SCOUT_THRESHOLD,SPAWN_MAX,TARGET_THRESHOLD,INFEST_CHANCE,SWARM_SCALING,SCOUT_NATURAL_THRESHOLD;
    public static final Map<String,ModConfigSpec.ConfigValue<List<Integer>>> CHANCES=new LinkedHashMap<>();
    static {
        var b=new ModConfigSpec.Builder();
        ENABLE_HIVES=bool(b,"12.G00_enableHives",true);HIVE_SPAWN=integer(b,"12.G01_hiveSpawn",256,1);
        SCOUT_THRESHOLD=decimal(b,"12.G02_scoutThreshold",1);SPAWN_MAX=decimal(b,"12.G07_spawnMax",50);TARGET_THRESHOLD=decimal(b,"12.G08_targetingThreshold",1);
        SCOUT_SWARM_CHANCE=integer(b,"12.G10_scoutSwarmSpawn",3,0);LARGE_HIVE_CHANCE=integer(b,"12.G11_largeHiveChance",5,1);LARGE_HIVE_THRESHOLD=integer(b,"12.G12_largeHiveThreshold",20,0);
        WAYPOINT_DEBUG=bool(b,"12.G13_waypointDebug",false);INFESTATION=bool(b,"12.I01_enableInfestation",true);INFEST_CHANCE=decimal(b,"12.I02_baseInfestChance",5);
        SWARM_SIZE=integer(b,"12.GS01_baseSwarmSize",5,0);SWARM_SCALING=decimal(b,"12.GS02_swarmScalingMult",1.2);SOOT_STEP=integer(b,"12.GS03_sootStep",50,1);
        b.comment("等待时间，单位：秒；每秒按 20 游戏刻计算。");COOLDOWN_SECONDS=integer(b,"12.GS04_swarmCooldown",120,1);
        chance(b,"12.GC01_glyphidChance",50,-45,0);chance(b,"12.GC02_brawlerChance",10,30,1);chance(b,"12.GC03_bombardierChance",20,-15,1);chance(b,"12.GC04_blasterChance",-5,40,5);chance(b,"12.GC05_diggerChance",-15,25,5);chance(b,"12.GC06_behemothChance",-30,45,10);chance(b,"12.GC07_brendaChance",-50,60,20);chance(b,"12.GC08_johnsonChance",-50,60,50);
        SCOUT_NATURAL=bool(b,"12.R02_rampantScoutSpawn",false);SCOUT_NATURAL_THRESHOLD=decimal(b,"12.R02.1_rampantScoutSpawnThresh",13);SCOUT_NATURAL_CHANCE=integer(b,"12.R02.2_rampantScoutSpawnChance",1400,1);
        EXTENDED=bool(b,"12.R03_rampantExtendedTargeting",false);DIG=bool(b,"12.R04_rampantDig",false);GUIDANCE=bool(b,"12.R05_rampantGlyphidGuidance",false);INITIAL_SCOUT=bool(b,"12.R07_scoutInitialSpawn",false);
        SPEC=b.build();
    }
    private static java.util.List<String> path(String key){return switch(key){
        case "12.G00_enableHives" -> java.util.List.of("hives","enabled");
        case "12.G01_hiveSpawn" -> java.util.List.of("hives","spawnChanceDenominator");
        case "12.G02_scoutThreshold" -> java.util.List.of("scouting","pollutionThreshold");
        case "12.G07_spawnMax" -> java.util.List.of("spawning","populationLimit");
        case "12.G08_targetingThreshold" -> java.util.List.of("targeting","pollutionThreshold");
        case "12.G10_scoutSwarmSpawn" -> java.util.List.of("scouting","swarmChanceDenominator");
        case "12.G11_largeHiveChance" -> java.util.List.of("hives","largeHiveChanceDenominator");
        case "12.G12_largeHiveThreshold" -> java.util.List.of("hives","largeHivePollutionThreshold");
        case "12.G13_waypointDebug" -> java.util.List.of("debug","waypointsEnabled");
        case "12.I01_enableInfestation" -> java.util.List.of("infestation","enabled");
        case "12.I02_baseInfestChance" -> java.util.List.of("infestation","baseChance");
        case "12.GS01_baseSwarmSize" -> java.util.List.of("swarms","baseSize");
        case "12.GS02_swarmScalingMult" -> java.util.List.of("swarms","sizeMultiplier");
        case "12.GS03_sootStep" -> java.util.List.of("swarms","sootStep");
        case "12.GS04_swarmCooldown" -> java.util.List.of("swarms","cooldownSeconds");
        case "12.R02_rampantScoutSpawn" -> java.util.List.of("scouting","naturalSpawningEnabled");
        case "12.R02.1_rampantScoutSpawnThresh" -> java.util.List.of("scouting","naturalSpawnPollutionThreshold");
        case "12.R02.2_rampantScoutSpawnChance" -> java.util.List.of("scouting","naturalSpawnChanceDenominator");
        case "12.R03_rampantExtendedTargeting" -> java.util.List.of("behavior","extendedTargetingEnabled");
        case "12.R04_rampantDig" -> java.util.List.of("behavior","diggingEnabled");
        case "12.R05_rampantGlyphidGuidance" -> java.util.List.of("behavior","guidanceEnabled");
        case "12.R07_scoutInitialSpawn" -> java.util.List.of("scouting","initialScoutEnabled");
        case "12.GC01_glyphidChance" -> java.util.List.of("variantWeights","grunt");
        case "12.GC02_brawlerChance" -> java.util.List.of("variantWeights","brawler");
        case "12.GC03_bombardierChance" -> java.util.List.of("variantWeights","bombardier");
        case "12.GC04_blasterChance" -> java.util.List.of("variantWeights","blaster");
        case "12.GC05_diggerChance" -> java.util.List.of("variantWeights","digger");
        case "12.GC06_behemothChance" -> java.util.List.of("variantWeights","behemoth");
        case "12.GC07_brendaChance" -> java.util.List.of("variantWeights","brenda");
        case "12.GC08_johnsonChance" -> java.util.List.of("variantWeights","johnson");
        default -> throw new IllegalArgumentException("Unknown configuration definition "+key);
    };}
    private static ModConfigSpec.BooleanValue bool(ModConfigSpec.Builder b,String key,boolean value){return b.define(path(key),value);}
    private static ModConfigSpec.IntValue integer(ModConfigSpec.Builder b,String key,int value,int minimum){return b.defineInRange(path(key),value,minimum,Integer.MAX_VALUE);}
    private static ModConfigSpec.DoubleValue decimal(ModConfigSpec.Builder b,String key,double value){return b.defineInRange(path(key),value,0,Double.MAX_VALUE);}
    private static void chance(ModConfigSpec.Builder b,String key,int base,int modifier,int threshold){CHANCES.put(key,b.define(path(key),List.of(base,modifier,threshold),value->value instanceof List<?> list&&list.size()>=3&&list.stream().allMatch(element->element instanceof Integer)));}
    public static boolean rampant(){return com.hbm.machine.chimney.ChimneyConfig.RAMPANT.get();}
    public static boolean extended(){return rampant()||EXTENDED.get();}
    public static boolean dig(){return rampant()||DIG.get();}
    public static boolean guidance(){return rampant()||GUIDANCE.get();}
    public static boolean naturalScouts(){return rampant()||SCOUT_NATURAL.get();}
    public static double scoutThreshold(){return rampant()?.1:SCOUT_THRESHOLD.get();}
    public static int scoutChance(){return rampant()?1:SCOUT_SWARM_CHANCE.get();}
    public static int[] chance(String key){var list=CHANCES.get(key).get();var out=new int[]{list.get(0),list.get(1),list.get(2)};if(rampant()&&key.equals("12.GC03_bombardierChance")&&out[2]==1)out[2]=0;return out;}
    private GlyphidConfig(){}
}
