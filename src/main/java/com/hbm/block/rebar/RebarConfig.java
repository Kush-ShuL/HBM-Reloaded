package com.hbm.block.rebar;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Actual original ClientConfig options, not a simplification of the default mesh. */
public final class RebarConfig {
 public static final ModConfigSpec SPEC;public static final ModConfigSpec.BooleanValue SIMPLE;public static final ModConfigSpec.IntValue LIMIT;
 static{var builder=new ModConfigSpec.Builder();SIMPLE=builder.comment("简化钢筋渲染：开启时绘制中央 3 根，关闭时绘制全部 12 根。").define(java.util.List.of("geometry","simplifiedModelEnabled"),false);LIMIT=builder.comment("每帧最多绘制的湿混凝土表面数量。").defineInRange(java.util.List.of("rendering","maxWetConcreteSurfacesPerFrame"),250,0,Integer.MAX_VALUE);SPEC=builder.build();}
 public static boolean simple(){return SPEC.isLoaded()&&SIMPLE.get();}public static int limit(){return SPEC.isLoaded()?LIMIT.get():250;}
 private RebarConfig(){}
}
