package com.hbm.machine.reactor.rbmk.residue;

import java.util.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** The original four pribris blocks, two cover items and six-type physical debris entity. */
public final class RbmkResidueContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "hbm");
    public static final EnumMap<RbmkResidueBlock.Kind, DeferredBlock<RbmkResidueBlock>> RESIDUES = new EnumMap<>(RbmkResidueBlock.Kind.class);
    public static final EnumMap<RbmkResidueBlock.Kind, DeferredItem<BlockItem>> RESIDUE_ITEMS = new EnumMap<>(RbmkResidueBlock.Kind.class);
    static {
        for (var kind : RbmkResidueBlock.Kind.values()) {
            var block = BLOCKS.register(kind.id, () -> new RbmkResidueBlock(kind));
            RESIDUES.put(kind, block);
            RESIDUE_ITEMS.put(kind, ITEMS.register(kind.id, () -> new BlockItem(block.get(), new Item.Properties())));
        }
    }
    public static final DeferredItem<RbmkLidItem> LID = ITEMS.register("rbmk_lid", () -> new RbmkLidItem(false));
    public static final DeferredItem<RbmkLidItem> GLASS_LID = ITEMS.register("rbmk_lid_glass", () -> new RbmkLidItem(true));
    public static final DeferredHolder<EntityType<?>, EntityType<RbmkDebrisEntity>> DEBRIS = ENTITIES.register("entity_rbmk_debris", () ->
            EntityType.Builder.<RbmkDebrisEntity>of(RbmkDebrisEntity::new, MobCategory.MISC).sized(.5F, .5F)
                    .clientTrackingRange(63).updateInterval(1).setShouldReceiveVelocityUpdates(true).build("hbm:entity_rbmk_debris"));
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) {
        if (tab.equals("machine")) return RESIDUE_ITEMS.values().stream().map(item -> new ItemStack(item.get())).toList();
        return tab.equals("control") ? List.of(new ItemStack(LID.get()), new ItemStack(GLASS_LID.get())) : List.of();
    }
    private RbmkResidueContent() { }
}
