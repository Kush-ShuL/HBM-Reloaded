package com.hbm.entity.mob.fbi;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Actual source MobConfig F00..F07 values; default raids remain disabled. */
public final class FbiConfig {
    private static final ModConfigSpec.Builder B=new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue ENABLED=B.define("enableFBIRaids",false);
    public static final ModConfigSpec.IntValue DELAY=B.defineInRange("raidDelay",30*60*60,1,Integer.MAX_VALUE),CHANCE=B.defineInRange("raidChance",3,1,Integer.MAX_VALUE),AMOUNT=B.defineInRange("raidAmount",15,0,Integer.MAX_VALUE),ATTACK_DELAY=B.defineInRange("raidAttackDelay",40,1,Integer.MAX_VALUE),ATTACK_REACH=B.defineInRange("raidAttackReach",2,0,Integer.MAX_VALUE),DISTANCE=B.defineInRange("raidAttackDistance",32,0,Integer.MAX_VALUE),DRONES=B.defineInRange("raidDrones",5,0,Integer.MAX_VALUE);
    private static final ModConfigSpec SPEC=B.build();
    public static void register(ModContainer container){container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-fbi-raids.toml");}
    private FbiConfig(){}
}
