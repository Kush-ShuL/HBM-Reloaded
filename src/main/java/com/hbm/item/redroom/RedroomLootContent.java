package com.hbm.item.redroom;
import java.util.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
/** Unique actual identities needed by the original red-room weighted reward pool. */
public final class RedroomLootContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final Map<RedroomArmorModItem.Kind,DeferredItem<RedroomArmorModItem>> MODS=new LinkedHashMap<>();
 static {for(var kind:RedroomArmorModItem.Kind.values())MODS.put(kind,ITEMS.register(kind.id,()->new RedroomArmorModItem(kind)));}
 public static final DeferredItem<InfusionFlaskItem> FLASK=ITEMS.register("flask_infusion_shield",InfusionFlaskItem::new);
 public static void register(IEventBus bus){ITEMS.register(bus);ShieldInfusion.register(bus);RedroomLootRuntime.register();}
 public static List<ItemStack> allStacks(){var result=new ArrayList<ItemStack>();for(var item:MODS.values())result.add(new ItemStack(item.get()));result.add(new ItemStack(FLASK.get()));return List.copyOf(result);}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?allStacks():List.of();}
 private RedroomLootContent(){}
}
