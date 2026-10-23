package com.hbm.world.structure.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;

/** One vanilla singleton prevents an extra nextInt(total) before the original pool's draw. */
public final class StructurePoolEntry extends LootPoolSingletonContainer {
    public static final MapCodec<StructurePoolEntry> CODEC=Codec.STRING.fieldOf("pool").xmap(StructurePoolEntry::new,value->value.pool);
    private final String pool;
    public StructurePoolEntry(String pool){super(1,0,List.of(),List.of());this.pool=pool;}
    @Override public LootPoolEntryType getType(){return StructureLootContent.POOL.get();}
    @Override protected void createItemStack(Consumer<ItemStack> output,LootContext context){output.accept(LegacyStructureLoot.sample(context.getLevel(),pool,context.getRandom()));}
}
