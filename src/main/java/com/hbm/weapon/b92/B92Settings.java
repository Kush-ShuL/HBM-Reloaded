package com.hbm.weapon.b92;
import net.neoforged.fml.ModContainer;import net.neoforged.fml.config.ModConfig;import net.neoforged.neoforge.common.ModConfigSpec;
/** Original WeaponConfig dropStar setting, independent of per-gun charge and source mandatory overloading. */
public final class B92Settings {
 public static final ModConfigSpec SPEC;private static final ModConfigSpec.BooleanValue DROP_STAR;
 static{var builder=new ModConfigSpec.Builder();DROP_STAR=builder.comment("被动过手脚的星爆枪电池是否在落地 50 秒后或着火时爆炸；关闭后落地物品仍被消耗。").define(java.util.List.of("cells","riggedCellExplosionsEnabled"),true);SPEC=builder.build();}
 public static void register(ModContainer container){container.registerConfig(ModConfig.Type.SERVER,SPEC,"hbm-b92.toml");}
 public static boolean droppedStar(){return !SPEC.isLoaded()||DROP_STAR.get();}
 private B92Settings(){}
}
