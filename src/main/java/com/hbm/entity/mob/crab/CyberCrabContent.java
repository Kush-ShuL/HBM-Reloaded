package com.hbm.entity.mob.crab;
import com.hbm.exposure.*;
import java.util.List;
import net.minecraft.core.registries.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** The real meteor dungeon mobs/spawner. The hidden brick is a technical host, never an extra creative item. */
public final class CyberCrabContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> TILES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredHolder<EntityType<?>,EntityType<CyberCrab>> CYBER=ENTITIES.register("entity_cyber_crab",()->EntityType.Builder.<CyberCrab>of(CyberCrab::new,MobCategory.MONSTER).sized(.75F,.35F).clientTrackingRange(16).updateInterval(1).build("hbm:entity_cyber_crab"));
    public static final DeferredHolder<EntityType<?>,EntityType<TeslaCrab>> TESLA=ENTITIES.register("entity_tesla_crab",()->EntityType.Builder.<TeslaCrab>of(TeslaCrab::new,MobCategory.MONSTER).sized(.75F,1.25F).clientTrackingRange(16).updateInterval(1).build("hbm:entity_tesla_crab"));
    public static final DeferredBlock<MeteorSpawnerBlock> SPAWNER=BLOCKS.register("meteor_spawner",()->new MeteorSpawnerBlock(BlockBehaviour.Properties.of().strength(15,216).sound(SoundType.STONE).requiresCorrectToolForDrops()));
    public static final DeferredBlock<MeteorSpawnerBlock> HIDDEN=BLOCKS.register("meteor_spawner_hidden",()->new MeteorSpawnerBlock(BlockBehaviour.Properties.of().strength(15,216).sound(SoundType.STONE).requiresCorrectToolForDrops(),true));
    public static final DeferredItem<BlockItem> SPAWNER_ITEM=ITEMS.registerSimpleBlockItem("meteor_spawner",SPAWNER);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<MeteorSpawnerBlockEntity>> SPAWNER_ENTITY=TILES.register("meteor_spawner",()->BlockEntityType.Builder.of(MeteorSpawnerBlockEntity::new,SPAWNER.get(),HIDDEN.get()).build(null));
    private CyberCrabContent(){}
    public static void register(IEventBus bus){ENTITIES.register(bus);BLOCKS.register(bus);ITEMS.register(bus);TILES.register(bus);bus.addListener((net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent event)->{event.put(CYBER.get(),CyberCrab.attributes().build());event.put(TESLA.get(),TeslaCrab.attributes().build());});HbmExposure.addProtection(new ExposureProtection(){@Override public boolean radiationImmune(LivingEntity entity){return entity instanceof CyberCrab;}});}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(SPAWNER_ITEM.get())):List.of();}
}
