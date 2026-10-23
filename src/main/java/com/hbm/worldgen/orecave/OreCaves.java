package com.hbm.worldgen.orecave;

import com.google.gson.Gson;
import com.mojang.serialization.MapCodec;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.*;

/** The two active original OreCave registrations and their real common BlockStalagmite dependency. */
public final class OreCaves {
    private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,"hbm");
    private static final DeferredRegister<MapCodec<? extends BiomeModifier>> MODIFIERS=DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,"hbm");
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<CaveFormationBlock> STALAGMITE=BLOCKS.register("stalagmite",()->new CaveFormationBlock(false)),STALACTITE=BLOCKS.register("stalactite",()->new CaveFormationBlock(true));
    public static final DeferredItem<CaveFormationItem> STALAGMITE_ITEM=ITEMS.register("stalagmite",()->new CaveFormationItem(STALAGMITE.get(),"stalagmite")),STALACTITE_ITEM=ITEMS.register("stalactite",()->new CaveFormationItem(STALACTITE.get(),"stalactite"));
    public record Profile(String name,String legacyOreName,int metadata,double threshold,int rangeMult,int centerY,int maxRange,String fluid){}
    private record Definitions(List<Profile> profiles){}
    public static final List<Profile> PROFILES=profiles();
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue SULFUR,ASBESTOS,DEEP;
    public static final ModConfigSpec.IntValue NEGATIVE_OFFSET;
    static {
        var config=new ModConfigSpec.Builder();SULFUR=config.comment("生成硫磺洞穴及硫磺结晶，封闭空间内可生成酸池。").define("enableSulfurCave",true);ASBESTOS=config.comment("生成石棉洞穴及石棉矿物。").define("enableAsbestosCave",true);
        DEEP=config.comment("额外在负高度生成洞穴；同时受深层矿物开关与高度范围控制。正高度洞穴不受影响。").define("negativeYCaves",true);
        NEGATIVE_OFFSET=config.comment("负高度洞穴相对世界底部的附加高度偏移，单位：方块，默认 0。").defineInRange("negativeOffset",0,-512,512);SPEC=config.build();
    }
    private static List<Profile> profiles(){try(var stream=OreCaves.class.getResourceAsStream("/hbm/ore-caves.json")){if(stream==null)throw new IllegalStateException("Missing ore-caves.json; run tools/port_ore_caves.py");return List.copyOf(new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),Definitions.class).profiles());}catch(java.io.IOException failure){throw new IllegalStateException(failure);}}
    public static boolean enabled(Profile profile){return switch(profile.name()){case "sulfur"->SULFUR.get();case "asbestos"->ASBESTOS.get();default->throw new IllegalArgumentException(profile.name());};}
    public static void register(IEventBus bus,ModContainer container){FEATURES.register("legacy_ore_caves",OreCaveFeature::new);MODIFIERS.register("ore_cave_features",()->OreCaveBiomeModifier.CODEC);FEATURES.register(bus);MODIFIERS.register(bus);BLOCKS.register(bus);ITEMS.register(bus);container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-ore-caves-common.toml");}
    public static List<ItemStack> indexedItems(){return java.util.stream.Stream.concat(STALAGMITE_ITEM.get().creativeStacks().stream(),STALACTITE_ITEM.get().creativeStacks().stream()).toList();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?indexedItems():List.of();}
    private OreCaves(){}
}
