package com.hbm.machine.fielddisturber;

import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Original hidden creative-tab registration and standard ItemBlock. No BE, HE, menu, configuration or invented machine interfaces. */
public final class FieldDisturberContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<FieldDisturberBlock> BLOCK=BLOCKS.register("field_disturber",FieldDisturberBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem(BLOCK);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    /** Source setCreativeTab(null), even though its real manufacturing recipe is available. */
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(ITEM.get()));}
    private FieldDisturberContent(){}
}
