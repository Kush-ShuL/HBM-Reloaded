package com.hbm.item.food;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Genuine source ItemEnergy.makeCan branches; unresolved conserve variants are not registered. */
public final class CrateFoodContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> CANS=List.of("can_smart","can_creature","can_redbomb","can_mrsugar","can_overcharge","can_luna","can_bepis","can_breen","can_mug");
    public static final DeferredItem<PuddingItem> PUDDING=ITEMS.register("pudding",PuddingItem::new);
    private static final Map<String,DeferredItem<EnergyCanItem>> ENERGY=new LinkedHashMap<>();
    static{for(var name:CANS)ENERGY.put(name,ITEMS.register(name,()->new EnergyCanItem(name)));}
    public static void register(IEventBus bus){ITEMS.register(bus);FoodIngredients.register(bus);}
    public static List<ItemStack> creativeStacks(String tab){
        if(!tab.equals("consumable"))return List.of();
        var stacks=new java.util.ArrayList<ItemStack>();ENERGY.values().forEach(item->stacks.add(new ItemStack(item.get())));stacks.add(new ItemStack(PUDDING.get()));return stacks;
    }
    private CrateFoodContent(){}
}
