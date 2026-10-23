package com.hbm.item.redroom.mixin;

import java.util.List;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LootPool.class)
public interface SourceLootPoolAccess {
    @Accessor("entries") List<LootPoolEntryContainer> hbm$sourceEntries();
    @Mutable @Accessor("entries") void hbm$sourceEntries(List<LootPoolEntryContainer> entries);
}
