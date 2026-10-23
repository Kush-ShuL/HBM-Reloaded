package com.hbm.block.crate;

import com.hbm.item.food.ConserveItem;
import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Source food crate is stationary and is not registered until its full food pool is available. */
public final class ConserveContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ConserveItem> CONSERVE=ITEMS.register("canned_conserve",ConserveItem::new);
    public static final DeferredBlock<ConserveCrateBlock> CRATE=BLOCKS.register("crate_can",ConserveCrateBlock::new);
    public static final DeferredItem<BlockItem> CRATE_ITEM=ITEMS.registerSimpleBlockItem("crate_can",CRATE);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("consumable"))return List.of();var stacks=new java.util.ArrayList<>(CONSERVE.get().creativeStacks());stacks.add(new ItemStack(CRATE_ITEM.get()));return stacks;}
    private ConserveContent(){}
}
