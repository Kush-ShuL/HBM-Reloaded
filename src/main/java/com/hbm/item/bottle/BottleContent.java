package com.hbm.item.bottle;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

public final class BottleContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<BottleOpenerItem> OPENER=ITEMS.register("bottle_opener",BottleOpenerItem::new);
    public static final List<DeferredItem<EnergyBottleItem>> DRINKS=List.of(
        bottle("bottle_nuka","bottle_empty","cap_nuka"),bottle("bottle_cherry","bottle_empty","cap_nuka"),
        bottle("bottle_quantum","bottle_empty","cap_quantum"),bottle("bottle_sparkle","bottle_empty","cap_sparkle"),
        bottle("bottle_rad","bottle_empty","cap_rad"),bottle("bottle2_korl","bottle2_empty","cap_korl"),
        bottle("bottle2_fritz","bottle2_empty","cap_fritz"),bottle("chocolate_milk",null,null),bottle("coffee",null,null),bottle("coffee_radium",null,null));
    private static DeferredItem<EnergyBottleItem> bottle(String name,String container,String cap){return ITEMS.register(name,()->new EnergyBottleItem(name,container,cap));}
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("consumable"))return List.of();var stacks=new java.util.ArrayList<ItemStack>();for(var item:DRINKS)stacks.add(new ItemStack(item.get()));stacks.add(new ItemStack(OPENER.get()));return List.copyOf(stacks);}
    private BottleContent(){}
}
