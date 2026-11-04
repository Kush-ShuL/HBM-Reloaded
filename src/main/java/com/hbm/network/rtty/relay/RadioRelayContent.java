package com.hbm.network.rtty.relay;

import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RadioRelayContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    public static final DeferredBlock<RadioRelayBlock> BLOCK = BLOCKS.register("radio_torch_relay", RadioRelayBlock::new);
    public static final DeferredItem<BasicBlockItem> ITEM = ITEMS.register("radio_torch_relay", () ->
            new BasicBlockItem(BLOCK.get(), new Item.Properties(), "BlockGenericTooltip", "radio_torch_relay"));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RadioRelayEntity>> ENTITY =
            ENTITIES.register("radio_torch_relay", () -> BlockEntityType.Builder.of(RadioRelayEntity::new, BLOCK.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        ENTITIES.register(bus);
        bus.addListener(RadioRelayControl::register);
    }

    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("machine") ? List.of(new ItemStack(ITEM.get())) : List.of();
    }

    private RadioRelayContent() {}
}
