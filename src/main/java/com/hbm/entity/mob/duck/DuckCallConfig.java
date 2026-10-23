package com.hbm.entity.mob.duck;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Reads the loaded server switch at login and at the actual summon request. */
public final class DuckCallConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue ENABLED;
    static {
        var builder = new ModConfigSpec.Builder();
        builder.push("duck");
        ENABLED = builder.comment("允许玩家按召鸭键召唤一只鸭子，每名玩家只能使用一次，默认开启。关闭后不显示登录提示，服务器也拒绝召唤请求。")
                .define("enabled", true);
        builder.pop();
        SPEC = builder.build();
    }
    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, SPEC, "hbm-player-duck-server.toml");
    }
    public static boolean enabled() { return !SPEC.isLoaded() || ENABLED.get(); }
    private DuckCallConfig() {}
}
