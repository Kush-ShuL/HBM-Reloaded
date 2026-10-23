package com.hbm.block.digamma;

import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** Three actual original block identities, including hidden source blockitems. */
public final class DigammaBlocks {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredBlock<DigammaMatterBlock> MATTER=BLOCKS.register("digamma_matter",DigammaMatterBlock::new);
 public static final DeferredBlock<DigammaFlameBlock> FLAME=BLOCKS.register("fire_digamma",DigammaFlameBlock::new);
 public static final DeferredBlock<AshDigammaBlock> ASH=BLOCKS.register("ash_digamma",AshDigammaBlock::new);
 public static final DeferredItem<BlockItem> MATTER_ITEM=ITEMS.register("digamma_matter",()->new BlockItem(MATTER.get(),new Item.Properties()));
 public static final DeferredItem<BlockItem> FLAME_ITEM=ITEMS.register("fire_digamma",()->new BlockItem(FLAME.get(),new Item.Properties()));
 public static final DeferredItem<BlockItem> ASH_ITEM=ITEMS.register("ash_digamma",()->new BlockItem(ASH.get(),new Item.Properties()));
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ASH_ITEM.get())):List.of();}
 private DigammaBlocks(){}
}
