package com.hbm.world.structure.dependencies.hadron;
import java.util.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class HadronCoilContent {
 private static final List<String> COILS = List.of(
         "hadron_coil_alloy", "hadron_coil_gold", "hadron_coil_neodymium",
         "hadron_coil_magtung", "hadron_coil_schrabidium", "hadron_coil_schrabidate",
         "hadron_coil_starmetal", "hadron_coil_chlorophyte", "hadron_coil_mese");
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final Map<String,DeferredBlock<HadronCoilBlock>> BLOCK_MAP=new LinkedHashMap<>();private static final List<DeferredItem<BlockItem>> ALL_ITEMS=new ArrayList<>();
 static {
     for (String name : COILS) {
         var block = BLOCKS.register(name, HadronCoilBlock::new);
         BLOCK_MAP.put(name, block);
         ALL_ITEMS.add(ITEMS.registerSimpleBlockItem(name, block));
     }
 }
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
 public static HadronCoilBlock block(String name){return Objects.requireNonNull(BLOCK_MAP.get(name),"Unknown actual coil: "+name).get();}
 public static boolean isCoilName(String name){return BLOCK_MAP.containsKey(name);}
 public static List<ItemStack> allStacks(){return ALL_ITEMS.stream().map(item->new ItemStack(item.get())).toList();}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?allStacks():List.of();}
 private HadronCoilContent(){}
}
