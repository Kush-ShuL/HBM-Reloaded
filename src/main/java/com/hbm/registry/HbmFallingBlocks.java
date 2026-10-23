package com.hbm.registry;

import com.google.gson.Gson;
import com.hbm.block.falling.FallingSpec;
import com.hbm.block.falling.LegacyFallingBlock;
import com.hbm.entity.LegacyFallingBlockEntity;
import com.hbm.item.BasicBlockItem;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The original BlockFalling registrations and the old falling entity algorithm. */
public final class HbmFallingBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, "hbm");
    public static final DeferredHolder<EntityType<?>, EntityType<LegacyFallingBlockEntity>> ENTITY = ENTITIES.register(
            "legacy_falling_block", () -> EntityType.Builder.<LegacyFallingBlockEntity>of(LegacyFallingBlockEntity::new, MobCategory.MISC)
                    .sized(.98F, .98F).clientTrackingRange(10).updateInterval(20).build("hbm:legacy_falling_block"));
    private static final List<DeferredItem<BasicBlockItem>> ALL = new ArrayList<>();
    private HbmFallingBlocks() {}

    public static void register(IEventBus bus) {
        try (var input = HbmFallingBlocks.class.getResourceAsStream("/hbm/falling-blocks.json")) {
            if (input == null) throw new IllegalStateException("Run tools/port_falling_blocks.py");
            var content = new Gson().fromJson(new InputStreamReader(input, StandardCharsets.UTF_8), Content.class);
            for (FallingSpec spec : content.blocks()) {
                var block = BLOCKS.register(spec.id(), () -> new LegacyFallingBlock(spec));
                ALL.add(ITEMS.register(spec.id(), () -> new BasicBlockItem(block.get(), new Item.Properties(), spec.originalClass(), spec.id())));
            }
        } catch (java.io.IOException error) { throw new IllegalStateException(error); }
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus);
    }

    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("block") ? ALL.stream().map(item -> new ItemStack(item.get())).toList() : List.of();
    }
    private record Content(List<FallingSpec> blocks) {}
}
