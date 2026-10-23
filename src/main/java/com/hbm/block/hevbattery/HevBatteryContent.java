package com.hbm.block.hevbattery;

import com.hbm.armor.t51.ItemFusionCore;
import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Keep the actual source item.hev_battery and tile.hev_battery identities distinct. */
public final class HevBatteryContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    public static final DeferredBlock<HevBatteryBlock> BLOCK = BLOCKS.register("hev_battery", HevBatteryBlock::new);
    public static final DeferredItem<BlockItem> BLOCK_ITEM = ITEMS.registerSimpleBlockItem("hev_battery", BLOCK);
    public static final DeferredItem<ItemFusionCore> ITEM = ITEMS.register("hev_battery_item", () -> new ItemFusionCore(150000, 4));
    public static void register(IEventBus bus) { BLOCKS.register(bus); ITEMS.register(bus); }
    public static List<ItemStack> creativeStacks(String tab) {
        return tab.equals("machine") ? List.of(new ItemStack(BLOCK_ITEM.get())) : tab.equals("control") ? List.of(new ItemStack(ITEM.get())) : List.of();
    }
    private HevBatteryContent() {}
}
