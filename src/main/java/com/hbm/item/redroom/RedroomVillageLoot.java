package com.hbm.item.redroom;

import com.hbm.item.redroom.mixin.SourceLootItemAccess;
import com.hbm.item.redroom.mixin.SourceLootPoolAccess;
import com.hbm.item.redroom.mixin.SourceLootTableAccess;
import java.util.ArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.neoforged.neoforge.event.LootTableLoadEvent;

/** Original ChestGenHooks entries join the existing weighted pool without rebuilding its registry holders. */
final class RedroomVillageLoot {
    static void loaded(LootTableLoadEvent event) {
        var name = event.getName().toString();
        if (name.equals("minecraft:chests/village/village_weaponsmith")) {
            sourceWeighted(event,"hbm:armor_polish",3);
            sourceWeighted(event,"hbm:bathwater",1);
        } else if (name.equals("minecraft:chests/abandoned_mineshaft")) {
            sourceWeighted(event,"hbm:bathwater",1);
        }
    }

    private static void sourceWeighted(LootTableLoadEvent event, String id, int weight) {
        var table = event.getTable();
        var pools = ((SourceLootTableAccess) table).hbm$sourcePools();
        // An explicitly empty datapack table stays empty.
        if (pools.isEmpty()) return;
        var pool = pools.getFirst();
        var access = (SourceLootPoolAccess) pool;
        var entries = access.hbm$sourceEntries();
        var item = BuiltInRegistries.ITEM.getOptional(ResourceLocation.parse(id)).orElseThrow();
        for (var entry : entries) {
            if (entry instanceof LootItem && ((SourceLootItemAccess) entry).hbm$sourceItem().value() == item) return;
        }
        if (table.isFrozen() || pool.isFrozen()) throw new IllegalStateException("Source weighted loot must be installed during table load");
        var expanded = new ArrayList<>(entries);
        expanded.add(LootItem.lootTableItem(item).setWeight(weight).build());
        access.hbm$sourceEntries(expanded);
    }

    private RedroomVillageLoot() { }
}
