package com.hbm.conveyor;
import net.minecraft.core.registries.Registries;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.entity.*;import net.neoforged.bus.api.IEventBus;import net.neoforged.fml.ModContainer;import net.neoforged.fml.config.ModConfig;import net.neoforged.neoforge.common.ModConfigSpec;import net.neoforged.neoforge.registries.*;
/** Shared true source conveyor-object execution; all later actual belts reuse this identity. */
public final class ConveyorObjects {
 private static final DeferredRegister<EntityType<?>> TYPES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredHolder<EntityType<?>,EntityType<MovingItem>> ITEM=TYPES.register("entity_c_item",()->EntityType.Builder.<MovingItem>of(MovingItem::new,MobCategory.MISC).sized(.375F,.375F).clientTrackingRange(63).updateInterval(3).build("hbm:entity_c_item"));
 public static final ModConfigSpec SPEC;public static final ModConfigSpec.IntValue CRAM_MAX;public static final ModConfigSpec.BooleanValue CRAM_EXPLODE;static{var b=new ModConfigSpec.Builder();CRAM_MAX=b.comment("输送带对象拥堵时的重叠数量阈值，默认 25。按对象计时周期检查，达到阈值时清理拥堵对象。").defineInRange(java.util.List.of("congestion","maxOverlappingObjects"),25,Integer.MIN_VALUE,Integer.MAX_VALUE);CRAM_EXPLODE=b.comment("对象拥堵时是否同时破坏输送带；关闭后仍清理拥堵对象并播放小型爆炸效果。").define(java.util.List.of("congestion","breakBeltEnabled"),true);SPEC=b.build();}
 private static boolean registered;public static void register(IEventBus bus,ModContainer container){if(registered)return;registered=true;TYPES.register(bus);container.registerConfig(ModConfig.Type.SERVER,SPEC,"hbm-conveyors.toml");}
 private ConveyorObjects(){}
}
