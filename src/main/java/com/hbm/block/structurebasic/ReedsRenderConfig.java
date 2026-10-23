package com.hbm.block.structurebasic;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Original ClientConfig.RENDER_REEDS, with its actual Angelica compatibility default. */
public final class ReedsRenderConfig {
 public static final ModConfigSpec SPEC;public static final ModConfigSpec.BooleanValue RENDER_REEDS;
 static{var builder=new ModConfigSpec.Builder();RENDER_REEDS=builder.comment("在水下继续绘制芦苇；关闭后只绘制顶部。").define(java.util.List.of("rendering","underwaterSegmentsEnabled"),!ModList.get().isLoaded("angelica"));SPEC=builder.build();}
 public static boolean full(){return SPEC.isLoaded()?RENDER_REEDS.get():!ModList.get().isLoaded("angelica");}
 private ReedsRenderConfig(){}
}
