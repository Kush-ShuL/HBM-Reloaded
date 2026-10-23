package com.hbm.disease;
import com.hbm.exposure.*;import net.neoforged.bus.api.IEventBus;import net.neoforged.fml.ModContainer;import net.neoforged.fml.config.ModConfig;import net.neoforged.fml.event.config.ModConfigEvent;import net.neoforged.neoforge.common.ModConfigSpec;
/** Original RadiationConfig lung-hazard switches; existing ExposureData remains the only dose state. */
public final class DiseaseSystem {
 public static final ModConfigSpec SPEC;public static final ModConfigSpec.BooleanValue DISABLE_ASBESTOS,DISABLE_COAL;
 static {var builder=new ModConfigSpec.Builder();DISABLE_ASBESTOS=builder.comment("禁用石棉危害；已积累的吸入量仍保留。").translation("config.hbm.disease.disable_asbestos").define("HAZ_00_disableAsbestos",false);DISABLE_COAL=builder.comment("禁用煤尘危害；已积累的吸入量仍保留。").translation("config.hbm.disease.disable_coaldust").define("HAZ_01_disableCoaldust",false);SPEC=builder.build();}
 public static void register(IEventBus bus,ModContainer container){container.registerConfig(ModConfig.Type.SERVER,SPEC,"hbm-diseases.toml");bus.addListener(DiseaseSystem::loading);bus.addListener(DiseaseSystem::reloading);DiseaseVisuals.register(bus);}
 private static void loading(ModConfigEvent.Loading event){if(event.getConfig().getSpec()==SPEC)configure();}private static void reloading(ModConfigEvent.Reloading event){if(event.getConfig().getSpec()==SPEC)configure();}
 private static void configure(){var previous=HbmExposure.settings();HbmExposure.configure(new ExposureSettings(previous.contamination(),!DISABLE_ASBESTOS.get(),!DISABLE_COAL.get(),previous.netherRadiation()));}
 private DiseaseSystem(){}
}
