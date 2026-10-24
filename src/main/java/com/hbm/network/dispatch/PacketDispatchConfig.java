package com.hbm.network.dispatch;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Configuration for the server-owned, optional clientbound PLAY work pool. */
public final class PacketDispatchConfig {
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.BooleanValue ENABLED, ERROR_BYPASS;
    private static final ModConfigSpec.IntValue CORE, MAXIMUM;

    static {
        var builder=new ModConfigSpec.Builder();
        builder.push("clientboundPlay");
        ENABLED=builder.comment(
                "新建服务端分发池时是否启用工作线程，默认开启。",
                "只调度已接入的 HBM 游戏阶段发送任务，不接管登录、配方定义或其他模组的数据包。",
                "游戏内切换只影响当前服务端，不保存到此配置。")
                .define("workerDispatchEnabled",true);
        CORE=builder.comment(
                "分发池的核心线程数，默认 1。小于 0 时与最大线程数一起回退为 1。",
                "最大线程数大于核心线程数时，两者也回退为 1。",
                "应用时先设置核心线程数，再设置最大线程数；不合法的组合会抛出异常。")
                .defineInRange("coreWorkers",1,Integer.MIN_VALUE,Integer.MAX_VALUE);
        MAXIMUM=builder.comment(
                "分发池的最大线程数，默认 1。小于等于 0 时与核心线程数一起回退为 1。",
                "任务使用无容量上限的等待队列；调整线程数不限制等待任务数量。")
                .defineInRange("maximumWorkers",1,Integer.MIN_VALUE,Integer.MAX_VALUE);
        ERROR_BYPASS=builder.comment(
                "跳过逐任务等待和连续清队后的同步回退，默认关闭。",
                "开启后故障任务仍可能积压或被清除；此选项不保证数据包送达。")
                .define("errorFallbackBypass",false);
        builder.pop();
        SPEC=builder.build();
    }

    public static void register(ModContainer container){container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-packet-dispatch.toml");}
    public static boolean workerDispatchEnabled(){return !SPEC.isLoaded()||ENABLED.get();}
    public static int coreWorkers(){return SPEC.isLoaded()?CORE.get():1;}
    public static int maximumWorkers(){return SPEC.isLoaded()?MAXIMUM.get():1;}
    public static boolean errorFallbackBypass(){return SPEC.isLoaded()&&ERROR_BYPASS.get();}
    private PacketDispatchConfig(){}
}
