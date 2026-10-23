package com.hbm.item.toolability;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Original loaded ToolConfig defaults for the three abilities actually owned by the cobalt pickaxe. */
public final class CobaltToolConfig {
    private static final ModConfigSpec.Builder BUILDER=new ModConfigSpec.Builder();
    public static final ModConfigSpec.IntValue DEPTH=BUILDER.defineInRange(java.util.List.of("veinMining","maxBlocks"),1000,0,Integer.MAX_VALUE);
    public static final ModConfigSpec.BooleanValue STONE=BUILDER.define(java.util.List.of("veinMining","includeStone"),false),NETHERRACK=BUILDER.define(java.util.List.of("veinMining","includeNetherrack"),false),VEIN=BUILDER.define(java.util.List.of("abilities","veinMiningEnabled"),true),LUCK=BUILDER.define(java.util.List.of("abilities","fortuneEnabled"),true),SILK=BUILDER.define(java.util.List.of("abilities","silkTouchEnabled"),true);
    public static final ModConfigSpec.BooleanValue HAMMER=BUILDER.define(java.util.List.of("abilities","hammerEnabled"),true),CRYSTALLIZER=BUILDER.define(java.util.List.of("abilities","crystallizerEnabled"),true),EXPLOSION=BUILDER.define(java.util.List.of("abilities","explosionEnabled"),true);
    public static final ModConfigSpec.BooleanValue FURNACE=BUILDER.define(java.util.List.of("abilities","furnaceEnabled"),true),SHREDDER=BUILDER.define(java.util.List.of("abilities","shredderEnabled"),true),CENTRIFUGE=BUILDER.define(java.util.List.of("abilities","centrifugeEnabled"),true),MERCURY=BUILDER.define(java.util.List.of("abilities","mercuryEnabled"),true);
    public static final ModConfigSpec SPEC=BUILDER.build();
    public static void register(ModContainer container){container.registerConfig(ModConfig.Type.STARTUP,SPEC,"hbm-tool-abilities.toml");}
    private CobaltToolConfig(){}
}
