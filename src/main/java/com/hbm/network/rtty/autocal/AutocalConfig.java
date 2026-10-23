package com.hbm.network.rtty.autocal;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Exact three source ServerConfig integer limits/defaults; no new scripting language or global command. */
public final class AutocalConfig {
 public static final ModConfigSpec SPEC;public static final ModConfigSpec.ConfigValue<Integer> AUTOCAL_MAX_CLOCK,MAX_BUFFER_LENGTH,MAX_STACK_SIZE;private static boolean registered;
 static{var builder=new ModConfigSpec.Builder();AUTOCAL_MAX_CLOCK=builder.define(java.util.List.of("execution","maxClock"),20);MAX_BUFFER_LENGTH=builder.define(java.util.List.of("memory","maxBufferLength"),256);MAX_STACK_SIZE=builder.define(java.util.List.of("memory","maxStackSize"),256);SPEC=builder.build();}
 public static void register(){if(!registered){registered=true;ModLoadingContext.get().getActiveContainer().registerConfig(ModConfig.Type.SERVER,SPEC,"hbm-autocal-server.toml");}}private AutocalConfig(){}
}
