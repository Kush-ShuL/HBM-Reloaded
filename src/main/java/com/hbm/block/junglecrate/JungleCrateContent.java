package com.hbm.block.junglecrate;
import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class JungleCrateContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredBlock<JungleCrateBlock> BLOCK=BLOCKS.register("crate_jungle",JungleCrateBlock::new);
 public static final DeferredItem<BlockItem> ITEM=ITEMS.registerSimpleBlockItem("crate_jungle",BLOCK);
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
 public static List<ItemStack> allStacks(){return List.of(new ItemStack(ITEM.get()));}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?allStacks():List.of();}
 private JungleCrateContent(){}
}
