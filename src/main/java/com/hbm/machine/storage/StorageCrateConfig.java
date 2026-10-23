package com.hbm.machine.storage;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Original server CRATE_OPEN_HELD and CRATE_KEEP_CONTENTS switches, both enabled by default. */
public final class StorageCrateConfig {
    public static final ModConfigSpec SPEC;private static final ModConfigSpec.BooleanValue OPEN_HELD,KEEP_CONTENTS;
    static{var builder=new ModConfigSpec.Builder();OPEN_HELD=builder.define(java.util.List.of("interaction","openHeldEnabled"),true);KEEP_CONTENTS=builder.define(java.util.List.of("drops","keepContentsEnabled"),true);SPEC=builder.build();}
    public static void register(ModContainer container){container.registerConfig(ModConfig.Type.SERVER,SPEC,"hbm-storage-crates-server.toml");}
    public static boolean openHeld(){return !SPEC.isLoaded()||OPEN_HELD.get();}
    public static boolean keepContents(){return !SPEC.isLoaded()||KEEP_CONTENTS.get();}
    private StorageCrateConfig(){}
}
