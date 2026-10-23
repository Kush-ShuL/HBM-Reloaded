package com.hbm.config;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
/** Unique source ServerConfig runtime wrappers. More original fields can join this one registry; no parallel door-only flag. */
public final class SourceServerConfig {
    public static final ModConfigSpec SPEC;
    public static final BooleanFlag ULTRA_LARP_MODE,STRUCTURE_DEBUG,DAMAGE_COMPATIBILITY_MODE;
    private static boolean registered;
    static {var builder=new ModConfigSpec.Builder();ULTRA_LARP_MODE=new BooleanFlag(builder.define(java.util.List.of("doors","requireRedstoneControl"),false),false);STRUCTURE_DEBUG=new BooleanFlag(builder.define(java.util.List.of("structureAuthoring","previewModeEnabled"),false),false);DAMAGE_COMPATIBILITY_MODE=new BooleanFlag(builder.comment("使用原生伤害计算以改善与其他模组的兼容性，默认关闭。开启后原版护甲按原生规则计算，不应用HBM的百分比护甲穿透修正。").define(java.util.List.of("damage","compatibilityModeEnabled"),false),false);SPEC=builder.build();}
    public static final class BooleanFlag {
        private final ModConfigSpec.BooleanValue value;private final boolean initial;
        private BooleanFlag(ModConfigSpec.BooleanValue value,boolean initial){this.value=value;this.initial=initial;}
        public boolean get(){return SPEC.isLoaded()?value.get():initial;}
        public void set(boolean setting){value.set(setting);}
    }
    public static void register(ModContainer container){if(!registered){registered=true;container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-source-server.toml");}}
    private SourceServerConfig(){}
}
