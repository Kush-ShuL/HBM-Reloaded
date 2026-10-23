package com.hbm.world.structure.dependencies.hadron;
import java.util.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class HadronCoilContent {
 public record Spec(String name,int factor){}
 public static final List<Spec> SPECS=List.of(
  new Spec("hadron_coil_alloy",10),
  new Spec("hadron_coil_gold",25),
  new Spec("hadron_coil_neodymium",50),
  new Spec("hadron_coil_magtung",100),
  new Spec("hadron_coil_schrabidium",250),
  new Spec("hadron_coil_schrabidate",500),
  new Spec("hadron_coil_starmetal",1000),
  new Spec("hadron_coil_chlorophyte",2500),
  new Spec("hadron_coil_mese",10000));
 private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static final Map<String,DeferredBlock<HadronCoilBlock>> BLOCK_MAP=new LinkedHashMap<>();private static final List<DeferredItem<HadronCoilItem>> ALL_ITEMS=new ArrayList<>();
 static {for(Spec spec:SPECS){var block=BLOCKS.register(spec.name(),()->new HadronCoilBlock(spec.factor()));BLOCK_MAP.put(spec.name(),block);ALL_ITEMS.add(ITEMS.register(spec.name(),()->new HadronCoilItem(block.get())));}}
 public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);}
 public static HadronCoilBlock block(String name){return Objects.requireNonNull(BLOCK_MAP.get(name),"Unknown actual coil: "+name).get();}
 public static boolean isCoilName(String name){return BLOCK_MAP.containsKey(name);}
 public static List<ItemStack> allStacks(){return ALL_ITEMS.stream().map(item->new ItemStack(item.get())).toList();}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("machine")?allStacks():List.of();}
 private HadronCoilContent(){}
}
