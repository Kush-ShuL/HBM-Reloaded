package com.hbm.item.dye;
import java.util.*;import net.minecraft.resources.ResourceLocation;import net.minecraft.world.item.ItemStack;import net.neoforged.bus.api.IEventBus;import net.neoforged.neoforge.registries.*;
/** Both actual original partsTab items, all16 variants in original enum order. */
public final class ChemicalDyeContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<ChemicalDyeItem> DYE=ITEMS.register("chemical_dye",ChemicalDyeItem::new);public static final DeferredItem<CrayonItem> CRAYON=ITEMS.register("crayon",CrayonItem::new);
 public static void register(IEventBus bus){ITEMS.register(bus);}
 public static ResourceLocation id(String name){return ResourceLocation.fromNamespaceAndPath("hbm",name);}
 public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("parts"))return List.of();var result=new ArrayList<ItemStack>(32);for(var type:ChemicalDyeType.values())result.add(DYE.get().stack(type));for(var type:ChemicalDyeType.values())result.add(CRAYON.get().stack(type));return result;}
 private ChemicalDyeContent(){}
}
