package com.hbm.item.genericpart;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Seven physical source materials. Source IOrderedEnum affects creative ordering only. No old family Item is registered. */
public final class GenericPartContent {
 public static final List<String> NAMES=List.of("piston_pneumatic","piston_hydraulic","piston_electric","lde","hde","glass_polarized","alclad");
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 private static DeferredItem<GenericPartItem> part(String name,int ordinal){return ITEMS.register("part_generic_"+name,()->new GenericPartItem(name,ordinal));}
 public static final DeferredItem<GenericPartItem> PISTON_PNEUMATIC=part("piston_pneumatic",0),PISTON_HYDRAULIC=part("piston_hydraulic",1),PISTON_ELECTRIC=part("piston_electric",2),LDE=part("lde",3),HDE=part("hde",4),GLASS_POLARIZED=part("glass_polarized",5),ALCLAD=part("alclad",6);
 private static final List<DeferredItem<GenericPartItem>> SOURCE_ORDER=List.of(PISTON_PNEUMATIC,PISTON_HYDRAULIC,PISTON_ELECTRIC,LDE,HDE,GLASS_POLARIZED,ALCLAD);
 private static final List<Integer> CREATIVE_ORDER=List.of(0,1,2,3,4,6,5);
 public static void register(IEventBus bus){ITEMS.register(bus);}
 public static List<ItemStack> indexedItems(){return CREATIVE_ORDER.stream().map(index->new ItemStack(SOURCE_ORDER.get(index).get())).toList();}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("parts")?indexedItems():List.of();}
 private GenericPartContent(){}
}
