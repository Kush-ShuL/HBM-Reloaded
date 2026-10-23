package com.hbm.worldgen.redroom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.*;

/** The actual two keyholes and oriented room brick; all remain source-hidden. */
public final class RedRooms {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<Feature<?>> FEATURES=DeferredRegister.create(Registries.FEATURE,"hbm");
    private static final DeferredRegister<MapCodec<? extends BiomeModifier>> MODIFIERS=DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,"hbm");
    public static final DeferredBlock<StrangeStoneBlock> STONE=BLOCKS.register("stone_keyhole",StrangeStoneBlock::new);
    public static final DeferredBlock<RoomKeyholeBlock> INNER=BLOCKS.register("stone_keyhole_meta",RoomKeyholeBlock::new);
    public static final DeferredBlock<RoomBrickBlock> BRICK=BLOCKS.register("brick_red",RoomBrickBlock::new);
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ENABLED,DEEP;
    public static final ModConfigSpec.IntValue CHANCE;
    static {var builder=new ModConfigSpec.Builder();ENABLED=builder.define("redRoomKeyholes",true);DEEP=builder.comment("将红房间生成带移至世界底部附近，并使用深板岩外观；关闭后在 Y=6～18 的石头中生成。").define("adaptToDeepslateDepth",true);CHANCE=builder.comment("每个区块尝试一次，生成概率为 1/4；使用钥匙后才创建内部房间。").defineInRange("chanceDenominator",4,1,1000000);SPEC=builder.build();}
    public static void register(IEventBus bus,ModContainer container){
        ITEMS.registerSimpleBlockItem(STONE);ITEMS.registerSimpleBlockItem(INNER);ITEMS.registerSimpleBlockItem(BRICK);
        FEATURES.register("red_room_keyhole",StrangeStoneFeature::new);MODIFIERS.register("red_room_keyholes",()->RedRoomBiomeModifier.CODEC);
        BLOCKS.register(bus);ITEMS.register(bus);FEATURES.register(bus);MODIFIERS.register(bus);
        container.registerConfig(ModConfig.Type.COMMON,SPEC,"hbm-red-rooms-common.toml");
    }
    private RedRooms(){}
}
