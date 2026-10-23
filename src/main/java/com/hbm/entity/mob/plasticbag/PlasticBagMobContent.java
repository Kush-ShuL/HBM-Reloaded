package com.hbm.entity.mob.plasticbag;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.registries.*;
/** Actual source addMob194/ocean202. Bag item+held inventory215 and buoyant item180 remain unique separate modules. */
public final class PlasticBagMobContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<EntityPlasticBag>> BAG_MOB=ENTITIES.register("entity_plastic_bag",()->EntityType.Builder.<EntityPlasticBag>of(EntityPlasticBag::new,MobCategory.WATER_CREATURE).sized(.45F,.45F).clientTrackingRange(5).updateInterval(3).setShouldReceiveVelocityUpdates(true).build("hbm:entity_plastic_bag"));
    public static final DeferredItem<DeferredSpawnEggItem> EGG=ITEMS.register("plastic_bag_spawn_egg",()->new DeferredSpawnEggItem(BAG_MOB,0xD0D0D0,0x808080,new Item.Properties()));
    private PlasticBagMobContent(){}
    public static void register(IEventBus bus){ENTITIES.register(bus);ITEMS.register(bus);bus.addListener(PlasticBagMobContent::attributes);bus.addListener(PlasticBagMobContent::placements);bus.addListener(PlasticBagMobContent::creative);}
    private static void attributes(EntityAttributeCreationEvent event){event.put(BAG_MOB.get(),Mob.createMobAttributes().add(Attributes.MAX_HEALTH,20D).add(Attributes.MOVEMENT_SPEED,.7D).build());}
    private static void placements(RegisterSpawnPlacementsEvent event){event.register(BAG_MOB.get(),SpawnPlacementTypes.IN_WATER,Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,PlasticBagMobContent::canSpawn,RegisterSpawnPlacementsEvent.Operation.REPLACE);}
    private static boolean canSpawn(EntityType<EntityPlasticBag> type,ServerLevelAccessor world,MobSpawnType reason,BlockPos pos,RandomSource random){return pos.getY()>45D&&pos.getY()<63D&&random.nextInt(10)==0;}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey()==CreativeModeTabs.SPAWN_EGGS)event.accept(EGG.get());}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(EGG.get()));}
}
