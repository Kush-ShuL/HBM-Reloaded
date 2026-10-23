package com.hbm.weapon.repairkits;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Actual ModItems2844/2845, no old dead ItemSyringe repair branch or fictional10/50% lore. */
public final class RepairKitContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<ItemRepairKit> OIL=ITEMS.register("gun_kit_1",()->new ItemRepairKit(10,"item.spray"));
 public static final DeferredItem<ItemRepairKit> KIT=ITEMS.register("gun_kit_2",()->new ItemRepairKit(100,"item.repair"));
 public static void register(IEventBus bus){ITEMS.register(bus);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(OIL.get()),new ItemStack(KIT.get())):List.of();}
 private RepairKitContent(){}
}
