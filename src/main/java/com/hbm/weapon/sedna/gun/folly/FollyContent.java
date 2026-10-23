package com.hbm.weapon.sedna.gun.folly;

import com.hbm.entity.projectile.EntityBulletBaseMK4CL;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.GunProjectiles;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.registries.*;

/** Only the actual source Folly gun plus the actual shared source MK4CL projectile type. */
public final class FollyContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredItem<ItemGunBaseNT> GUN=ITEMS.register("gun_folly",FollyFactory::gun_folly);
    public static final DeferredHolder<EntityType<?>,EntityType<EntityBulletBaseMK4CL>> BULLET_CL=ENTITIES.register("entity_bullet_mk4_cl",()->EntityType.Builder.<EntityBulletBaseMK4CL>of(EntityBulletBaseMK4CL::new,MobCategory.MISC).sized(.5F,.5F).fireImmune().clientTrackingRange(16).updateInterval(1).setShouldReceiveVelocityUpdates(false).build("hbm:entity_bullet_mk4_cl"));
    public static void register(IEventBus bus){FollyFactory.init();ITEMS.register(bus);ENTITIES.register(bus);bus.addListener((RegisterTicketControllersEvent event)->event.register(EntityBulletBaseMK4CL.TICKETS));bus.addListener(FollyContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->GunProjectiles.register(BulletConfig.ProjectileType.BULLET_CHUNKLOADING,EntityBulletBaseMK4CL::new));}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(GUN.get()));}
    public static List<ItemStack> allStacks(){return indexedItems();}
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    private FollyContent(){}
}
