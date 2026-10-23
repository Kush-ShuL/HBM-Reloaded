package com.hbm.machine.rtg;

import com.hbm.hazard.HazardRuntime;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** MachineConfig's original two switches; 528 retains its original forced behavior. */
public final class RtgConfig {
    private static final ModConfigSpec.Builder BUILDER=new ModConfigSpec.Builder();
    private static final ModConfigSpec.BooleanValue SCALE=BUILDER.define(java.util.List.of("isotopeFuel","activityScaledPowerEnabled"),false);
    private static final ModConfigSpec.BooleanValue DECAY=BUILDER.define(java.util.List.of("isotopeFuel","decayEnabled"),true);
    public static final ModConfigSpec SPEC=BUILDER.build();
    public static void register(ModContainer container){container.registerConfig(ModConfig.Type.STARTUP,SPEC,"hbm-rtg.toml");}
    public static boolean decays(){return HazardRuntime.settings().mode528()||DECAY.get();}
    public static boolean scales(){return HazardRuntime.settings().mode528()||SCALE.get();}
    private RtgConfig(){}
}
