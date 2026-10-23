package com.hbm.machine.capacitors;
import com.hbm.item.BasicBlockItem;
import java.util.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class CapacitorContent {
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final Map<String,DeferredBlock<CapacitorBlock>> CAPACITORS=new LinkedHashMap<>();public static final Map<String,DeferredItem<BlockItem>> ITEM_MAP=new LinkedHashMap<>();
 public static final DeferredBlock<CapacitorBusBlock> BUS=BLOCKS.register("capacitor_bus",CapacitorBusBlock::new);
 static{for(String metal:List.of("copper","gold","niobium","tantalium","schrabidate")){var block=BLOCKS.register("capacitor_"+metal,()->new CapacitorBlock(metal));CAPACITORS.put(metal,block);ITEM_MAP.put("capacitor_"+metal,ITEMS.register("capacitor_"+metal,()->new BlockItem(block.get(),new Item.Properties())));}ITEM_MAP.put("capacitor_bus",ITEMS.register("capacitor_bus",()->new BasicBlockItem(BUS.get(),new Item.Properties(),"BlockGenericTooltip","capacitor_bus")));}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}public static List<ItemStack> allStacks(){return ITEM_MAP.values().stream().map(item->new ItemStack(item.get())).toList();}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?List.of(new ItemStack(ITEM_MAP.get("capacitor_copper").get())):List.of();}
 private CapacitorContent(){}
}
