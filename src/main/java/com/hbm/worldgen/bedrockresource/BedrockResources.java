package com.hbm.worldgen.bedrockresource;

import com.google.gson.Gson;
import com.hbm.item.bedrockresource.*;
import com.hbm.registry.HbmContent;
import com.mojang.serialization.MapCodec;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.*;

/** Actual source automatic bedrock geology and Nether weighted table, sharing existing ore BE and private fluids. */
public final class BedrockResources {
    private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,"hbm");
    private static final DeferredRegister<MapCodec<? extends BiomeModifier>> MODIFIERS=DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,"hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<RawBedrockOreItem> RAW_ORE=ITEMS.register("bedrock_ore_base",RawBedrockOreItem::new);
    public static final DeferredItem<OreDensityScannerItem> SCANNER=ITEMS.register("ore_density_scanner",OreDensityScannerItem::new);
    public record Definition(String item,int count,int tier,int color,String weightOption,int defaultWeight){public ItemStack stack(){return new ItemStack(HbmContent.item(item),count);}}
    private record Definitions(List<Definition> nether){}
    public static final List<Definition> NETHER_DEFINITIONS=definitions();
    private static final Map<String,ModConfigSpec.IntValue> WEIGHTS=new LinkedHashMap<>();
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue OVERWORLD,NETHER,ADAPT_OVERWORLD_FLOOR;
    public static final ModConfigSpec.IntValue OVERWORLD_CHANCE,NETHER_CHANCE,OVERWORLD_OFFSET;
    static{
        var config=new ModConfigSpec.Builder();
        OVERWORLD=config.comment("在主世界生成基岩矿床；同时受主世界矿物生成总开关控制。").define("overworldBedrockResources",true);
        NETHER=config.comment("在下界生成三类基岩矿床；同时受下界矿物生成总开关控制。").define("netherBedrockResources",true);
        OVERWORLD_CHANCE=config.comment("下界基岩矿床的生成概率分母，默认 10，即每次尝试有 1/10 的概率生成。").defineInRange("overworldChance",10,1,1000000);
        NETHER_CHANCE=config.defineInRange("netherChance",10,1,1000000);
        for(var definition:NETHER_DEFINITIONS)WEIGHTS.put(definition.weightOption(),config.comment("矿床选择权重；0 表示不参与选择。").defineInRange(definition.weightOption(),definition.defaultWeight(),0,1000000));
        ADAPT_OVERWORLD_FLOOR=config.comment("让基岩矿床适应世界底部高度，默认开启；关闭后仍以 Y=0 为基准。矿床尺寸和生成次数不变。").define("overworldAdaptBedrockFloor",true);
        OVERWORLD_OFFSET=config.comment("矿床相对所选基岩基准面的高度偏移，单位：方块；不改变厚度和密度。").defineInRange("overworldVerticalOffset",0,-2048,2048);
        SPEC=config.build();
    }
    private static List<Definition> definitions(){try(var stream=BedrockResources.class.getResourceAsStream("/hbm/bedrock-resources.json")){if(stream==null)throw new IllegalStateException("Missing actual bedrock-resources.json; run tools/port_bedrock_resources.py");return List.copyOf(new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),Definitions.class).nether());}catch(java.io.IOException error){throw new IllegalStateException(error);}}
    public static int weight(Definition definition){return WEIGHTS.get(definition.weightOption()).get();}
    public static void register(IEventBus bus,ModContainer container){FEATURES.register("legacy_overworld_bedrock_resources",()->new BedrockResourceFeature(false));FEATURES.register("legacy_nether_bedrock_resources",()->new BedrockResourceFeature(true));MODIFIERS.register("bedrock_resource_features",()->BedrockResourceBiomeModifier.CODEC);FEATURES.register(bus);MODIFIERS.register(bus);ITEMS.register(bus);container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-bedrock-resources-common.toml");}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?List.of(RAW_ORE.get().creativeStack()):tab.equals("consumable")?List.of(new ItemStack(SCANNER.get())):List.of();}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(RAW_ORE.get()),new ItemStack(SCANNER.get()));}
    private BedrockResources(){}
}
