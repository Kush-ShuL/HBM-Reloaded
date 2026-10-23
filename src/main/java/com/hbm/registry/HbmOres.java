package com.hbm.registry;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.hbm.block.ore.LegacyOreBlock;
import com.hbm.block.ore.LegacyOreItem;
import com.hbm.block.ore.OilSandBlock;
import com.hbm.block.ore.OreSpec;
import com.hbm.block.ore.OutgassingOreBlock;
import com.hbm.block.ore.ResourceOreBlock;
import com.hbm.block.ore.BedrockOreBlock;
import com.hbm.block.ore.BedrockOreEntity;
import com.hbm.block.ore.DrillbitItem;
import com.hbm.block.ore.FissureBlock;
import com.hbm.block.ore.FissureBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Only behavior-complete ore families from tools/port_ores.py are registered. */
public final class HbmOres {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<BedrockOreEntity>> BEDROCK_ENTITY;
    public static DeferredHolder<BlockEntityType<?>, BlockEntityType<FissureBlockEntity>> FISSURE_ENTITY;
    private static final List<DeferredItem<? extends Item>> ALL = new ArrayList<>();
    public static final DeferredItem<Item> ALEXANDRITE = ITEMS.register("gem_alexandrite", () -> new Item(new Item.Properties()));
    public static final DeferredItem<DrillbitItem> DRILLBIT = ITEMS.register("drillbit", DrillbitItem::new);
    private HbmOres() { }

    public static void register(IEventBus bus) {
        try (var stream = HbmOres.class.getResourceAsStream("/hbm/ore-blocks.json")) {
            if (stream == null) throw new IllegalStateException("Run tools/port_ores.py to generate original ore registrations");
            Content content = new Gson().fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Content.class);
            for (OreSpec spec : content.blocks()) {
                if (spec.originalClass().equals("BlockFalling")) {
                    var block = BLOCKS.register(spec.id(), () -> new OilSandBlock(spec));
                    ALL.add(ITEMS.registerSimpleBlockItem(spec.id(), block));
                } else {
                    var block = BLOCKS.register(spec.id(), () -> switch (spec.originalClass()) {
                        case "BlockOutgas", "BlockNetherCoal" -> new OutgassingOreBlock(spec);
                        case "BlockOreBasalt", "BlockResourceStone" -> new ResourceOreBlock(spec);
                        case "BlockBedrockOreTE" -> new BedrockOreBlock(spec);
                        case "BlockFissure" -> new FissureBlock(spec);
                        default -> new LegacyOreBlock(spec);
                    });
                    ALL.add(ITEMS.register(spec.id(), () -> new LegacyOreItem(block.get())));
                    if (spec.originalClass().equals("BlockBedrockOreTE")) BEDROCK_ENTITY = ENTITIES.register("ore_bedrock", () -> BlockEntityType.Builder.of(BedrockOreEntity::new, block.get()).build(null));
                    if (spec.originalClass().equals("BlockFissure")) FISSURE_ENTITY = ENTITIES.register("ore_volcano", () -> BlockEntityType.Builder.of(FissureBlockEntity::new, block.get()).build(null));
                }
            }
        } catch (java.io.IOException exception) { throw new IllegalStateException("Cannot load original ore definitions", exception); }
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus);
    }

    public static List<DeferredItem<? extends Item>> allItems() { return List.copyOf(ALL); }
    public static List<ItemStack> creativeStacks(String tab) {
        if (tab.equals("parts")) return List.of(new ItemStack(ALEXANDRITE.get()));
        if (tab.equals("control")) return DRILLBIT.get().creativeStacks();
        if (!tab.equals("block")) return List.of();
        return ALL.stream().filter(holder -> !(holder.get() instanceof LegacyOreItem item) || !item.spec().id().equals("ore_bedrock"))
                .flatMap(holder -> holder.get() instanceof LegacyOreItem item
                ? item.creativeStacks().stream() : List.of(new ItemStack(holder.get())).stream()).toList();
    }
    private record Content(List<OreSpec> blocks) { }
}
