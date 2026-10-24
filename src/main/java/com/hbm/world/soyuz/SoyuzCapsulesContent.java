package com.hbm.world.soyuz;

import com.hbm.registry.HbmContent;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.*;

/** The actual storage capsule and its descent entity; the orbital-module item is already registered elsewhere. */
public final class SoyuzCapsulesContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, "hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "hbm");
    public static final DeferredBlock<SoyuzCapsuleBlock> CAPSULE = BLOCKS.register("soyuz_capsule", SoyuzCapsuleBlock::new);
    public static final DeferredItem<BlockItem> ITEM = ITEMS.register("soyuz_capsule", () -> new BlockItem(CAPSULE.get(), new Item.Properties()));
    public static final DeferredItem<SoyuzItem> SOYUZ = ITEMS.register("missile_soyuz", SoyuzItem::new);
    public static final DeferredItem<GlassRecordItem> GLASS_RECORD = ITEMS.register("record_glass", ()->new GlassRecordItem());
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SoyuzCapsuleBlockEntity>> BLOCK_ENTITY = TILES.register("soyuz_capsule", () ->
            BlockEntityType.Builder.of(SoyuzCapsuleBlockEntity::new, CAPSULE.get()).build(null));
    public static final DeferredHolder<MenuType<?>, MenuType<SoyuzCapsuleMenu>> MENU = MENUS.register("soyuz_capsule", () ->
            new MenuType<>((IContainerFactory<SoyuzCapsuleMenu>) SoyuzCapsuleMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<EntityType<?>, EntityType<EntitySoyuzCapsule>> DESCENT = ENTITIES.register("entity_soyuz_capsule", () ->
            EntityType.Builder.<EntitySoyuzCapsule>of(EntitySoyuzCapsule::new, MobCategory.MISC).sized(.6F, 1.8F).fireImmune()
                    .clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_soyuz_capsule"));
    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); TILES.register(bus); MENUS.register(bus); ENTITIES.register(bus);
        bus.addListener(SoyuzCapsulesContent::capabilities);
    }
    private static void capabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, BLOCK_ENTITY.get(), (capsule, side) ->
                side == null ? new InvWrapper(capsule) : new SidedInvWrapper(capsule, side));
    }
    /** Reuse the sole actual BasicContent ItemCustomLore, including its original tooltip/stack limit/recipe. */
    public static Item orbitalModule() { return HbmContent.item("missile_soyuz_lander"); }
    public static List<ItemStack> soyuzSkins() { return List.of(SoyuzItem.stack(0), SoyuzItem.stack(1), SoyuzItem.stack(2)); }
    public static List<ItemStack> creativeStacks(String tab) {
        if (!"missile".equals(tab)) return List.of();
        return List.of(new ItemStack(ITEM.get()), SoyuzItem.stack(0), SoyuzItem.stack(1), SoyuzItem.stack(2));
    }
    private SoyuzCapsulesContent() { }
}
