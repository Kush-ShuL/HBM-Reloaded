package com.hbm.world.taint;

import com.hbm.exposure.*;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Actual taint block, conversion targets and source creature identities; no stand-in corruption blocks. */
public final class TaintContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
 public static final DeferredBlock<TaintBlock> TAINT=BLOCKS.register("taint",TaintBlock::new);
 public static final DeferredItem<BlockItem> TAINT_ITEM=ITEMS.register("taint",()->new BlockItem(TAINT.get(),new Item.Properties()){
  @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("tooltip.hbm.taint.warning"));}
 });
 public static final DeferredHolder<EntityType<?>,EntityType<TaintedCreeper>> CREEPER=ENTITIES.register("entity_mob_tainted_creeper",()->EntityType.Builder.of(TaintedCreeper::new,MobCategory.MONSTER).sized(.6F,1.8F).clientTrackingRange(16).updateInterval(1).build("hbm:entity_mob_tainted_creeper"));
 public static final DeferredHolder<EntityType<?>,EntityType<TaintCrab>> CRAB=ENTITIES.register("entity_taint_crab",()->EntityType.Builder.of(TaintCrab::new,MobCategory.MONSTER).sized(1.25F,1.25F).clientTrackingRange(16).updateInterval(1).build("hbm:entity_taint_crab"));
 public static final DeferredItem<net.neoforged.neoforge.common.DeferredSpawnEggItem> CREEPER_EGG=ITEMS.register("tainted_creeper_spawn_egg",()->new net.neoforged.neoforge.common.DeferredSpawnEggItem(CREEPER,0x813b9b,0xd71fdd,new Item.Properties()));
 public static final DeferredItem<net.neoforged.neoforge.common.DeferredSpawnEggItem> CRAB_EGG=ITEMS.register("taint_crab_spawn_egg",()->new net.neoforged.neoforge.common.DeferredSpawnEggItem(CRAB,0xaaaaaa,0xff00ff,new Item.Properties()));
 public static void register(IEventBus bus){bus.addListener((net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent event)->{if(event.getTabKey()==CreativeModeTabs.SPAWN_EGGS){event.accept(CREEPER_EGG.get());event.accept(CRAB_EGG.get());}});BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);TaintLoot.register(bus);bus.addListener((net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event)->{event.put(CREEPER.get(),TaintedCreeper.attributes().build());event.put(CRAB.get(),TaintCrab.attributes().build());});HbmExposure.addProtection(new ExposureProtection(){@Override public boolean radiationImmune(LivingEntity entity){return entity instanceof TaintedCreeper;}});}
 private TaintContent(){}
}
