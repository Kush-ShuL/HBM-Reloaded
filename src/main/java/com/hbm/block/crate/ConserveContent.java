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
    private static final List<DeferredItem<ConserveItem>> CANS=registerCans();
    private static List<DeferredItem<ConserveItem>> registerCans(){var cans=new java.util.ArrayList<DeferredItem<ConserveItem>>();for(var food:com.hbm.item.food.ConserveFoodType.values())cans.add(ITEMS.register("canned_"+food.id(),()->new ConserveItem(food)));return List.copyOf(cans);}
    public static ItemStack stack(com.hbm.item.food.ConserveFoodType food){return new ItemStack(CANS.get(food.ordinal()).get());}
    public static List<ItemStack> foodStacks(){return CANS.stream().map(item->new ItemStack(item.get())).toList();}
    public static final DeferredBlock<ConserveCrateBlock> CRATE=BLOCKS.register("crate_can",ConserveCrateBlock::new);
    public static final DeferredItem<BlockItem> CRATE_ITEM=ITEMS.registerSimpleBlockItem("crate_can",CRATE);
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("consumable"))return List.of();var stacks=new java.util.ArrayList<>(foodStacks());stacks.add(new ItemStack(CRATE_ITEM.get()));return stacks;}
    private ConserveContent(){}
}
