package com.hbm.world.glyphidhive;
import com.hbm.entity.mob.glyphid.*;
import com.hbm.entity.mob.glyphid.hive.EntityGlyphidScout;
import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.*;
/** The actual final Scout and real spawner; no second Glyphid config or natural feature registration. */
public final class GlyphidHiveContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> TILES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");private static final DeferredRegister<EntityType<?>> ENTITIES=DeferredRegister.create(Registries.ENTITY_TYPE,"hbm");
    public static final DeferredBlock<GlyphidSpawnerBlock> SPAWNER=BLOCKS.register("glyphid_spawner",GlyphidSpawnerBlock::new);
    public static final DeferredItem<GlyphidSpawnerItem> SPAWNER_ITEM=ITEMS.register("glyphid_spawner",()->new GlyphidSpawnerItem(SPAWNER.get()));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<GlyphidSpawnerEntity>> SPAWNER_ENTITY=TILES.register("glyphid_spawner",()->BlockEntityType.Builder.of(GlyphidSpawnerEntity::new,SPAWNER.get()).build(null));
    public static final DeferredHolder<EntityType<?>,EntityType<EntityGlyphidScout>> SCOUT=ENTITIES.register("entity_glyphid_scout",()->EntityType.Builder.<EntityGlyphidScout>of(EntityGlyphidScout::new,MobCategory.MONSTER).sized(1.25F,.75F).eyeHeight(.75F*.85F).clientTrackingRange(5).updateInterval(3).setShouldReceiveVelocityUpdates(true).build("hbm:entity_glyphid_scout"));
    public static final DeferredItem<DeferredSpawnEggItem> SCOUT_EGG=ITEMS.register("glyphid_scout_spawn_egg",()->new DeferredSpawnEggItem(SCOUT,0x273038,0xB9E36B,new Item.Properties()));
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);TILES.register(bus);ENTITIES.register(bus);bus.addListener((EntityAttributeCreationEvent event)->event.put(SCOUT.get(),EntityGlyphid.attributes(GlyphidStats.getStats().getScout()).build()));bus.addListener((BuildCreativeModeTabContentsEvent event)->{if(event.getTabKey()==CreativeModeTabs.SPAWN_EGGS)event.accept(SCOUT_EGG.get());});}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?java.util.stream.IntStream.range(0,3).mapToObj(meta->GlyphidSpawnerItem.stack(meta,1)).toList():List.of();}
    public static List<ItemStack> indexedItems(){var result=new ArrayList<>(creativeStacks("block"));result.add(new ItemStack(SCOUT_EGG.get()));return List.copyOf(result);}
    private GlyphidHiveContent(){}
}
