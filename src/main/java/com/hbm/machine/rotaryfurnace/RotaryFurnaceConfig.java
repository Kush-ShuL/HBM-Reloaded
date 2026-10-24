package com.hbm.machine.rotaryfurnace;

import com.hbm.fuel.ModuleBurnTime;
import com.hbm.machine.foundry.crucible.CrucibleConfig;
import com.hbm.machine.heatingoven.HeatingOvenConfig;
import com.hbm.machine.ashpit.AshpitConfig;
import com.hbm.world.structure.templates.heatboiler.HeatBoilerConfig;
import com.hbm.machine.industrialboiler.IndustrialBoilerConfig;
import com.hbm.machine.generator.steamengine.SteamEngineConfig;
import com.hbm.machine.coolingtower.CoolingTowerConfig;
import com.hbm.machine.poweredcondenser.PoweredCondenserConfig;
import com.hbm.machine.generator.stirling.StirlingConfig;
import com.hbm.machine.generator.diesel.DieselConfig;
import com.hbm.machine.turbine.IndustrialTurbineConfig;
import com.hbm.machine.groundwater.GroundwaterPumpSettings;
import com.hbm.machine.oilextraction.OilDrillConfig;
import com.hbm.machine.oilfracking.FrackingConfig;
import com.hbm.machine.reactor.iter.mhdt.MhdtConfig;
import com.hbm.machine.forcefield.ForceFieldConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Readable rotary-furnace fuel settings, applied to its existing burn module. */
public final class RotaryFurnaceConfig {
    private static final String[] KEYS={"logs","wood","coal","lignite","coke","solidFuel","rocketFuel","balefire"};
    private static final String[] LABELS={"原木","木制品","煤","褐煤","焦炭","固体燃料","火箭燃料","烈焰燃料"};
    private static final double[] DEFAULT_TIME={1.0D,1.0D,1.0D,1.0D,1.25D,1.5D,1.5D,1.5D};
    private static final double[] DEFAULT_HEAT={1.0D,1.0D,1.0D,1.0D,1.0D,1.5D,3.0D,10.0D};
    private static final ModConfigSpec.DoubleValue[] TIME=new ModConfigSpec.DoubleValue[8];
    private static final ModConfigSpec.DoubleValue[] HEAT=new ModConfigSpec.DoubleValue[8];
    public static final ModConfigSpec SPEC;
    public static volatile ModuleBurnTime BURN=defaults();

    static {
        var builder=new ModConfigSpec.Builder();
        builder.comment("回转炉设置。").push("rotaryFurnace");
        builder.comment("按燃料类别调整燃烧时长和热值。").push("burnModule");
        builder.comment("燃烧时长倍率；1为基础时长，0为零燃烧时长。范围：非负有限数。").push("time");
        for(int i=0;i<KEYS.length;i++)TIME[i]=builder.comment(LABELS[i]+"的燃烧时长倍率；单位：倍；范围：非负有限数。").defineInRange(KEYS[i],DEFAULT_TIME[i],0D,Double.MAX_VALUE);
        builder.pop();
        builder.comment("热值倍率用于处理速度，并会影响蒸汽消耗；机器处理速度保底为基础速度。范围：非负有限数。").push("heat");
        for(int i=0;i<KEYS.length;i++)HEAT[i]=builder.comment(LABELS[i]+"的热值倍率；单位：倍；范围：非负有限数。").defineInRange(KEYS[i],DEFAULT_HEAT[i],0D,Double.MAX_VALUE);
        builder.pop();builder.pop();builder.pop();
        CrucibleConfig.define(builder);
        HeatingOvenConfig.define(builder);
        AshpitConfig.define(builder);
        HeatBoilerConfig.define(builder);
        IndustrialBoilerConfig.define(builder);
        SteamEngineConfig.define(builder);
        CoolingTowerConfig.define(builder);
        PoweredCondenserConfig.define(builder);
        StirlingConfig.define(builder);
        DieselConfig.define(builder);
        IndustrialTurbineConfig.define(builder);
        GroundwaterPumpSettings.define(builder);
        OilDrillConfig.define(builder);
        FrackingConfig.define(builder);
        MhdtConfig.define(builder);
        ForceFieldConfig.define(builder);
        SPEC=builder.build();
    }

    public static void register(IEventBus bus,ModContainer container){
        container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-machine-settings.toml");
        bus.addListener(RotaryFurnaceConfig::loading);
        bus.addListener(RotaryFurnaceConfig::reloading);
    }
    private static void loading(ModConfigEvent.Loading event){if(event.getConfig().getSpec()==SPEC)apply();}
    private static void reloading(ModConfigEvent.Reloading event){if(event.getConfig().getSpec()==SPEC)apply();}
    private static void apply(){
        var next=defaults();
        next.setLogTimeMod(TIME[0].get());
        next.setLogHeatMod(HEAT[0].get());
        next.setWoodTimeMod(TIME[1].get());
        next.setWoodHeatMod(HEAT[1].get());
        next.setCoalTimeMod(TIME[2].get());
        next.setCoalHeatMod(HEAT[2].get());
        next.setLigniteTimeMod(TIME[3].get());
        next.setLigniteHeatMod(HEAT[3].get());
        next.setCokeTimeMod(TIME[4].get());
        next.setCokeHeatMod(HEAT[4].get());
        next.setSolidTimeMod(TIME[5].get());
        next.setSolidHeatMod(HEAT[5].get());
        next.setRocketTimeMod(TIME[6].get());
        next.setRocketHeatMod(HEAT[6].get());
        next.setBalefireTimeMod(TIME[7].get());
        next.setBalefireHeatMod(HEAT[7].get());
        BURN=next;
        CrucibleConfig.apply();
        HeatingOvenConfig.apply();
        AshpitConfig.apply();
        HeatBoilerConfig.apply();
        IndustrialBoilerConfig.apply();
        SteamEngineConfig.apply();
        CoolingTowerConfig.apply();
        PoweredCondenserConfig.apply();
        StirlingConfig.apply();
        DieselConfig.apply();
        IndustrialTurbineConfig.apply();
        GroundwaterPumpSettings.apply();
        OilDrillConfig.apply();
        FrackingConfig.apply();
        MhdtConfig.apply();
        ForceFieldConfig.apply();
    }
    private static ModuleBurnTime defaults(){return new ModuleBurnTime().setCokeTimeMod(1.25).setRocketTimeMod(1.5).setSolidTimeMod(1.5).setBalefireTimeMod(1.5).setSolidHeatMod(1.5).setRocketHeatMod(3).setBalefireHeatMod(10);}
    private RotaryFurnaceConfig(){}
}
