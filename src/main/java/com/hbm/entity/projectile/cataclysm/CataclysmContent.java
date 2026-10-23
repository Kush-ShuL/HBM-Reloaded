package com.hbm.entity.projectile.cataclysm;

import com.hbm.config.GeneralModes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Actual EntityMappings96 and ModEventHandler301 death trigger; original Cataclysm default is false. */
public final class CataclysmContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityBurningFOEQ>> FOEQ=ENTITIES.register("entity_burning_foeq",()->EntityType.Builder.<EntityBurningFOEQ>of(EntityBurningFOEQ::new,MobCategory.MISC).sized(.25F,.25F).clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_burning_foeq"));
    private CataclysmContent(){}
    public static void register(IEventBus bus){ENTITIES.register(bus);NeoForge.EVENT_BUS.addListener(CataclysmContent::death);}
    private static void death(LivingDeathEvent event){if(event.getEntity().level() instanceof ServerLevel world&&GeneralModes.flag("1.17_enableCataclysm")){var entity=new EntityBurningFOEQ(world);entity.moveTo(event.getEntity().getX(),500,event.getEntity().getZ(),0,0);world.addFreshEntity(entity);}}
}
