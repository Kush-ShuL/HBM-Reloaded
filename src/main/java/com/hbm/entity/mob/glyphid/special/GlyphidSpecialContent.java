package com.hbm.entity.mob.glyphid.special;

import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.entity.mob.glyphid.GlyphidStats;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Only the complete Digger/Nuclear atomic pair. Scout/Hive/spawn integration stays a later real group. */
public final class GlyphidSpecialContent {
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGlyphidDigger>> DIGGER = ENTITIES.register("entity_glyphid_digger", () ->
            EntityType.Builder.<EntityGlyphidDigger>of(EntityGlyphidDigger::new, MobCategory.MONSTER).sized(1.75F, 1F).eyeHeight(.85F)
                    .clientTrackingRange(5).updateInterval(3).setShouldReceiveVelocityUpdates(true).build("hbm:entity_glyphid_digger"));
    public static final DeferredHolder<EntityType<?>, EntityType<EntityGlyphidNuclear>> NUCLEAR = ENTITIES.register("entity_glyphid_nuclear", () ->
            EntityType.Builder.<EntityGlyphidNuclear>of(EntityGlyphidNuclear::new, MobCategory.MONSTER).sized(2.5F, 1.75F).eyeHeight(1.75F * .85F).fireImmune()
                    .clientTrackingRange(5).updateInterval(3).setShouldReceiveVelocityUpdates(true).build("hbm:entity_glyphid_nuclear"));
    public static final DeferredItem<DeferredSpawnEggItem> DIGGER_EGG = ITEMS.register("glyphid_digger_spawn_egg", () ->
            new DeferredSpawnEggItem(DIGGER, 0x273038, 0x724A21, new Item.Properties()));
    public static final DeferredItem<DeferredSpawnEggItem> NUCLEAR_EGG = ITEMS.register("glyphid_nuclear_spawn_egg", () ->
            new DeferredSpawnEggItem(NUCLEAR, 0x267F00, 0xA0A0A0, new Item.Properties()));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus); ITEMS.register(bus);
        bus.addListener(GlyphidSpecialContent::attributes);
        bus.addListener(GlyphidSpecialContent::creative);
    }
    private static void attributes(EntityAttributeCreationEvent event) {
        event.put(DIGGER.get(), EntityGlyphid.attributes(GlyphidStats.getStats().getDigger()).build());
        event.put(NUCLEAR.get(), EntityGlyphid.attributes(GlyphidStats.getStats().getNuclear()).build());
    }
    private static void creative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) for (ItemStack stack : indexedItems()) event.accept(stack);
    }
    public static List<ItemStack> indexedItems() { return List.of(new ItemStack(DIGGER_EGG.get()), new ItemStack(NUCLEAR_EGG.get())); }
    private GlyphidSpecialContent() { }
}
