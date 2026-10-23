package com.hbm.worldgen.dimensionores;

import com.google.gson.Gson;
import com.hbm.worldgen.depthdeposit.DepthRockBlock;
import com.hbm.worldgen.depthdeposit.DepthRockItem;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.*;

/** Only active source Nether/End ore calls, real Nether depth filler, and exposed smoldering netherrack. */
public final class DimensionOres {
    private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,"hbm");
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<DepthRockBlock> STONE_DEPTH_NETHER=BLOCKS.register("stone_depth_nether",DepthRockBlock::new);
    public static final DeferredItem<DepthRockItem> STONE_DEPTH_NETHER_ITEM=ITEMS.register("stone_depth_nether",()->new DepthRockItem(STONE_DEPTH_NETHER.get()));
    public record Profile(String symbol,String dimension,String config,int count,int size,int minY,int variance,boolean plutonium){}
    private record Definitions(List<Profile> profiles){}
    public static final List<Profile> PROFILES=profiles();
    private static final Map<String,ModConfigSpec.IntValue> COUNTS=new LinkedHashMap<>();
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue NETHER,END,PLUTONIUM,SMOLDER,DEPTH,NETHER_LOGICAL,END_BUILD_HEIGHT;
    public static final ModConfigSpec.IntValue NETHER_OFFSET,END_OFFSET,NETHER_HEIGHT_OVERRIDE;
    static {
        var config=new ModConfigSpec.Builder();
        NETHER=config.comment("下界矿物生成总开关，控制普通矿脉和两层深部矿床。").define("netherOre",true);
        END=config.comment("末地矿物生成总开关，控制翠锡矿矿脉。").define("endOre",true);
        PLUTONIUM=config.comment("是否生成钚矿，默认关闭；开启后每个区块尝试生成 8 条矿脉，每条大小为 4。").define("enablePlutoniumOre",false);
        for(var profile:PROFILES)COUNTS.put(profile.config(),config.defineInRange(profile.config(),profile.count(),0,10000));
        SMOLDER=config.comment("每个区块尝试生成 30 次露天硫磺；此项独立于下界矿物生成总开关。").define("netherSmolder",true);
        DEPTH=config.comment("生成上下两层钕矿床，各自概率为 1/16、半径为 7、填充率为 0.6；同时受下界矿物生成总开关控制。").define("netherDepthDeposits",true);
        NETHER_LOGICAL=config.comment("按维度的世界底部与逻辑高度调整矿物生成高度；默认下界高度不变。矿脉半径和生成次数不变。").define("netherAdaptLogicalHeight",true);
        NETHER_HEIGHT_OVERRIDE=config.comment("0 uses actual dimension logicalHeight; otherwise explicit modern geological height. Only active when netherAdaptLogicalHeight is true. Depth deposits leave floor and geological roof layers intact.").defineInRange("netherHeightOverride",0,0,2048);
        NETHER_OFFSET=config.comment("Explicit additional vertical offset for Nether anchors/protected bedrock boundaries.").defineInRange("netherVerticalOffset",0,-2048,2048);
        END_BUILD_HEIGHT=config.comment("按末地实际建造高度调整矿脉生成带，默认关闭；关闭时矿脉中心位于 Y=0～126。生成次数不变。").define("endAdaptBuildHeight",false);
        END_OFFSET=config.defineInRange("endVerticalOffset",0,-2048,2048);
        SPEC=config.build();
    }
    private static List<Profile> profiles(){try(var stream=DimensionOres.class.getResourceAsStream("/hbm/dimension-ores.json")){if(stream==null)throw new IllegalStateException("Missing dimension-ores.json; run tools/port_dimension_ores.py");return List.copyOf(new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),Definitions.class).profiles());}catch(java.io.IOException failure){throw new IllegalStateException(failure);}}
    public static int count(Profile profile){return COUNTS.get(profile.config()).get();}
    public static int netherBase(WorldGenLevel level){return (NETHER_LOGICAL.get()?level.getMinBuildHeight():0)+NETHER_OFFSET.get();}
    public static int netherHeight(WorldGenLevel level){if(!NETHER_LOGICAL.get())return 128;int configured=NETHER_HEIGHT_OVERRIDE.get();return Math.max(1,configured>0?configured:level.dimensionType().logicalHeight());}
    public static int height(WorldGenLevel level,boolean nether,int sourceY){
        if(nether)return netherBase(level)+(int)Math.floor(sourceY*(double)netherHeight(level)/128D);
        return (END_BUILD_HEIGHT.get()?level.getMinBuildHeight()+(int)Math.floor(sourceY*(double)level.getHeight()/128D):sourceY)+END_OFFSET.get();
    }
    public static void register(IEventBus bus,ModContainer container){FEATURES.register("legacy_nether_ores",()->new DimensionOreFeature(true));FEATURES.register("legacy_end_ores",()->new DimensionOreFeature(false));FEATURES.register("legacy_nether_depth_deposits",NetherDepthFeature::new);FEATURES.register("legacy_nether_smolder",NetherSmolderFeature::new);FEATURES.register(bus);BLOCKS.register(bus);ITEMS.register(bus);container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-dimension-ores-common.toml");}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(STONE_DEPTH_NETHER_ITEM.get())):List.of();}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(STONE_DEPTH_NETHER_ITEM.get()));}
    private DimensionOres(){}
}
