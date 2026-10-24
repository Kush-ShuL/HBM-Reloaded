package com.hbm.client.loadingtips;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Client presentation options for the native world-loading and progress screens. */
public final class LoadingTipsConfig {
    public static final String FILE_NAME="hbm-loading-screen-client.toml";
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue ENABLED,REMOTE_TIPS;

    static {
        var builder=new ModConfigSpec.Builder();
        builder.push("loadingScreen");
        ENABLED=builder.comment("使用带每日提示的世界加载和保存进度画面。默认true；关闭后使用原生画面，不改变加载进度或完成条件。")
            .define("enabled",true);
        REMOTE_TIPS=builder.comment("客户端启动时异步读取一次公开的每日提示列表。默认true；连接或读取失败时显示内置提示，不阻塞加载，也不重试。",
                "更改后重启客户端生效；不会读取版本或轨道舱消息，也不上传游戏或玩家数据。")
            .define("remoteTipsEnabled",true);
        builder.pop();
        SPEC=builder.build();
    }

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT,SPEC,FILE_NAME);
    }

    public static boolean enabled() {return !SPEC.isLoaded()||ENABLED.get();}
    public static boolean remoteTipsEnabled() {return !SPEC.isLoaded()||REMOTE_TIPS.get();}
    private LoadingTipsConfig(){}
}
