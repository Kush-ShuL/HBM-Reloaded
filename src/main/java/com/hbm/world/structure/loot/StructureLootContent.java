package com.hbm.world.structure.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.*;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Modern loot tables delegate exactly one source weighted draw to this real entry type. */
public final class StructureLootContent {
    private static final DeferredRegister<LootPoolEntryType> ENTRIES=DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE,"hbm");
    public static final DeferredHolder<LootPoolEntryType,LootPoolEntryType> POOL=ENTRIES.register("structure_pool",()->new LootPoolEntryType(StructurePoolEntry.CODEC));
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES=DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE,"hbm");
    public static final DeferredBlock<SupplyCrateBlock> SUPPLY=BLOCKS.register("crate_supply",SupplyCrateBlock::new);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> SUPPLY_ITEM=ITEMS.registerSimpleBlockItem("crate_supply",SUPPLY);
    public static final DeferredBlock<FallingSupplyCrateBlock> CRATE=BLOCKS.register("crate",FallingSupplyCrateBlock::new);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> CRATE_ITEM=ITEMS.registerSimpleBlockItem("crate",CRATE);
    public static final DeferredItem<AmmoContainerItem> AMMO_STANDARD=ITEMS.register("ammo_container_standard",()->new AmmoContainerItem(AmmoContainerItem.Kind.STANDARD));
    public static final DeferredItem<AmmoContainerItem> AMMO_MAKESHIFT=ITEMS.register("ammo_container_makeshift",()->new AmmoContainerItem(AmmoContainerItem.Kind.MAKESHIFT));
    public static final DeferredBlock<LootPileBlock> PILE=BLOCKS.register("deco_loot",LootPileBlock::new);
    public static final DeferredItem<MorningGloryItem> MORNING=ITEMS.register("morning_glory",MorningGloryItem::new);
    private static final DeferredRegister<net.minecraft.world.item.crafting.RecipeSerializer<?>> RECIPES=DeferredRegister.create(Registries.RECIPE_SERIALIZER,"hbm");
    public static final DeferredHolder<net.minecraft.world.item.crafting.RecipeSerializer<?>,MkuRecipe.Serializer> MKU_RECIPE=RECIPES.register("mku_seed",MkuRecipe.Serializer::new);
    public static final DeferredItem<net.minecraft.world.item.BlockItem> PILE_ITEM=ITEMS.registerSimpleBlockItem("deco_loot",PILE);
    public static final DeferredItem<LoreBookItem> BOOK=ITEMS.register("book_lore",LoreBookItem::new);
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<SupplyCrateBlockEntity>> SUPPLY_ENTITY=ENTITIES.register("crate_supply",()->BlockEntityType.Builder.of(SupplyCrateBlockEntity::new,SUPPLY.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>,BlockEntityType<LootPileBlockEntity>> PILE_ENTITY=ENTITIES.register("deco_loot",()->BlockEntityType.Builder.of(LootPileBlockEntity::new,PILE.get()).build(null));
    private static final DeferredRegister<net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType<?>> PROCESSORS=DeferredRegister.create(Registries.STRUCTURE_PROCESSOR,"hbm");
    public static final DeferredHolder<net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType<?>,net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType<StructureLootProcessor>> PROCESSOR=PROCESSORS.register("legacy_structure_loot",()->()->StructureLootProcessor.CODEC);
    public static void register(IEventBus bus){ENTRIES.register(bus);BLOCKS.register(bus);ITEMS.register(bus);ENTITIES.register(bus);RECIPES.register(bus);PROCESSORS.register(bus);LegacyStructureLoot.register();MorningGloryItem.register();MkuStructureLoot.register();}
    public static java.util.List<net.minecraft.world.item.ItemStack> creativeStacks(String tab){if(tab.equals("consumable"))return java.util.List.of(new net.minecraft.world.item.ItemStack(MORNING.get()),new net.minecraft.world.item.ItemStack(CRATE_ITEM.get()));if(tab.equals("missile"))return java.util.List.of(new net.minecraft.world.item.ItemStack(SUPPLY_ITEM.get()));if(tab.equals("weapon"))return java.util.List.of(new net.minecraft.world.item.ItemStack(AMMO_STANDARD.get()),new net.minecraft.world.item.ItemStack(AMMO_MAKESHIFT.get()));return java.util.List.of();}
    private StructureLootContent(){}
}
