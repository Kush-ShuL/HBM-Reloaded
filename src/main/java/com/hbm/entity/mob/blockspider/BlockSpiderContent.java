package com.hbm.entity.mob.blockspider;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;

/** Source addEntity, not addMob: no source egg or natural spawn entry. */
public final class BlockSpiderContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityBlockSpider>> SPIDER=ENTITIES.register("entity_taintcrawler",()->EntityType.Builder.<EntityBlockSpider>of(EntityBlockSpider::new,MobCategory.MONSTER).sized(.95F,1.25F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_taintcrawler"));
    private BlockSpiderContent(){}
    public static void register(IEventBus bus){ENTITIES.register(bus);bus.addListener((EntityAttributeCreationEvent event)->event.put(SPIDER.get(),EntityBlockSpider.attributes().build()));}
}
