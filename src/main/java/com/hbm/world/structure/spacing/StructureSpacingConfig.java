package com.hbm.world.structure.spacing;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** A world-session snapshot shared by native candidate generation, selection and locate. */
public final class StructureSpacingConfig {
    public static final String FILE_NAME = "hbm-structure-spacing-server.toml";
    private static final DeferredRegister<StructurePlacementType<?>> PLACEMENTS =
            DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, "hbm");
    public static final DeferredHolder<StructurePlacementType<?>, StructurePlacementType<ConfiguredNaturalPlacement>> PLACEMENT =
            PLACEMENTS.register("configured_natural_spread", () -> () -> ConfiguredNaturalPlacement.CODEC);
    public static final ModConfigSpec SPEC;
    private static final ModConfigSpec.IntValue GRID_SPACING, MINIMUM_SEPARATION;
    private static final Settings DEFAULT = new Settings(16, 4);
    private static volatile Settings active = DEFAULT;
    private static boolean registered;

    static {
        var builder = new ModConfigSpec.Builder();
        builder.comment("大型自然建筑的候选网格；只影响新的结构起点，不移动已生成建筑。修改后必须重新启动世界。")
                .push("naturalStructures");
        GRID_SPACING = builder.comment("每个候选网格的边长。默认16；单位区块；范围2至4096。必须大于minimumSeparationChunks。")
                .worldRestart().defineInRange("gridSpacingChunks", 16, 2, 4096);
        MINIMUM_SEPARATION = builder.comment("相邻网格候选点之间保留的间距参数。默认4；单位区块；范围1至4095，且必须小于gridSpacingChunks。两项组合无效时，本次世界均使用默认16和4。")
                .worldRestart().defineInRange("minimumSeparationChunks", 4, 1, 4095);
        builder.pop();
        SPEC = builder.build();
    }

    public record Settings(int spacing, int separation) {
        public Settings {
            if (spacing < 2 || spacing > 4096 || separation < 1 || separation >= spacing)
                throw new IllegalArgumentException("Invalid natural structure spacing");
        }
    }

    public static void register(IEventBus bus, ModContainer container) {
        if (registered) return;
        registered = true;
        PLACEMENTS.register(bus);
        container.registerConfig(ModConfig.Type.SERVER, SPEC, FILE_NAME);
        NeoForge.EVENT_BUS.addListener(StructureSpacingConfig::starting);
        NeoForge.EVENT_BUS.addListener(StructureSpacingConfig::stopped);
    }

    private static void starting(ServerAboutToStartEvent event) {
        // NeoForge loads this world's SERVER configs before this event, before loadLevel().
        int spacing = GRID_SPACING.get(), separation = MINIMUM_SEPARATION.get();
        if (separation >= spacing) {
            LogUtils.getLogger().error("自然结构间距组合无效：minimumSeparationChunks={}，gridSpacingChunks={}。本次世界使用默认16和4；请修正后重新启动世界。", separation, spacing);
            active = new Settings(16, 4);
        } else active = new Settings(spacing, separation);
    }

    private static void stopped(ServerStoppedEvent event) { active = DEFAULT; }
    static Settings current() { return active; }

    private StructureSpacingConfig() {}
}
