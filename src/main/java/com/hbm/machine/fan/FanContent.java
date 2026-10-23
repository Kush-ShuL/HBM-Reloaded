package com.hbm.machine.fan;

import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** The single active X5846 ModBlocks.fan identity; no invented motor, HE buffer or fluid machine. */
public final class FanContent {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("hbm");
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "hbm");
    public static final DeferredBlock<FanBlock> BLOCK = BLOCKS.register("fan", FanBlock::new);
    public static final DeferredItem<FanItem> ITEM = ITEMS.register("fan", () -> new FanItem(BLOCK.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FanEntity>> ENTITY = ENTITIES.register("fan", () -> BlockEntityType.Builder.of(FanEntity::new, BLOCK.get()).build(null));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); ENTITIES.register(bus);
        bus.addListener(FanModePacket::register);
    }
    public static List<ItemStack> creativeStacks(String tab) { return tab.equals("machine") ? List.of(new ItemStack(ITEM.get())) : List.of(); }
    private FanContent() {}
}
