package com.hbm.block.oilplant;
import com.hbm.item.EnumMultiItem;import com.hbm.hazard.HbmHazards;import java.util.*;import net.minecraft.world.item.*;import net.neoforged.bus.api.IEventBus;import net.neoforged.neoforge.registries.*;
/** The actual two source plant classes and full three-variant ItemEnumMulti plant material family. */
public final class OilBotanyContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredBlock<OilFlowerBlock> FLOWER=BLOCKS.register("plant_flower",OilFlowerBlock::new);public static final DeferredBlock<OilTallPlantBlock> TALL=BLOCKS.register("plant_tall",OilTallPlantBlock::new);
 public static final DeferredItem<OilPlantItem> FLOWER_ITEM=ITEMS.register("plant_flower",()->new OilPlantItem(FLOWER.get(),false));public static final DeferredItem<OilPlantItem> TALL_ITEM=ITEMS.register("plant_tall",()->new OilPlantItem(TALL.get(),true));
 public static final DeferredItem<EnumMultiItem> MATERIAL=ITEMS.register("plant_item",()->new EnumMultiItem("plant_item",64,true,List.of("tobacco","rope","mustardwillow"),List.of(0,1,2)));
 public static ItemStack flowerStack(int meta){var stack=new ItemStack(FLOWER_ITEM.get());stack.set(HbmHazards.LEGACY_METADATA.get(),meta);return stack;}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){var stacks=new ArrayList<ItemStack>();if(tab.equals("block")){for(int i=0;i<6;i++)stacks.add(flowerStack(i));for(int i=0;i<4;i++){var stack=new ItemStack(TALL_ITEM.get());stack.set(HbmHazards.LEGACY_METADATA.get(),i);stacks.add(stack);}}if(tab.equals("parts"))stacks.addAll(MATERIAL.get().creativeStacks());return stacks;}
}
