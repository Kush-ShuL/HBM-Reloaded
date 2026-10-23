package com.hbm.worldgen.depthdeposit;

import com.google.gson.Gson;
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
import net.neoforged.neoforge.registries.*;

/** Six original overworld DepthDeposit calls and their previously missing real BlockDepth filler. */
public final class DepthDeposits {
    private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,"hbm");
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<DepthRockBlock> STONE_DEPTH=BLOCKS.register("stone_depth",DepthRockBlock::new);
    public static final DeferredItem<DepthRockItem> STONE_DEPTH_ITEM=ITEMS.register("stone_depth",()->new DepthRockItem(STONE_DEPTH.get()));
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue LEGACY_POSITIVE,NEGATIVE;
    public static final ModConfigSpec.IntValue NEGATIVE_BASE_OFFSET;
    public static final ModConfigSpec.DoubleValue NEGATIVE_CHANCE_MULTIPLIER;
    public record Profile(String symbol,int minY,int variance,int size,double fill,int chance){}
    public static final List<Profile> PROFILES=profiles();
    private record Definitions(List<Profile> profiles){}
    static {
        var config=new ModConfigSpec.Builder();
        LEGACY_POSITIVE=config.comment("在 Y=0～2 附近生成深部矿床；同时受主世界矿物生成总开关控制。").define("legacyPositiveDeposits",true);
        NEGATIVE=config.comment("额外在负高度的基岩附近生成矿床；同时受深层矿物开关控制。正高度矿床不受影响。").define("negativeYDeposits",true);
        NEGATIVE_BASE_OFFSET=config.comment("负高度矿床相对世界底部的高度偏移，单位：方块，默认 0。保留最底层基岩，可替换上方的基岩层。").defineInRange("negativeBaseOffset",0,0,512);
        NEGATIVE_CHANCE_MULTIPLIER=config.comment("仅控制负高度矿床的概率分母倍率：1 为默认概率，越大越稀少。正高度矿床不受影响。").defineInRange("negativeChanceMultiplier",1D,.01D,100D);
        SPEC=config.build();
    }
    private static List<Profile> profiles(){try(var stream=DepthDeposits.class.getResourceAsStream("/hbm/depth-deposits.json")){if(stream==null)throw new IllegalStateException("Missing source-backed depth-deposits.json; run tools/port_depth_deposits.py");return List.copyOf(new Gson().fromJson(new InputStreamReader(stream,StandardCharsets.UTF_8),Definitions.class).profiles());}catch(java.io.IOException failure){throw new IllegalStateException(failure);}}
    public static void register(IEventBus bus,ModContainer container){FEATURES.register("legacy_depth_deposits",DepthDepositsFeature::new);FEATURES.register(bus);BLOCKS.register(bus);ITEMS.register(bus);container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-depth-deposits-common.toml");}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(STONE_DEPTH_ITEM.get())):List.of();}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(STONE_DEPTH_ITEM.get()));}
    private DepthDeposits(){}
}
