package com.hbm.item.treasure;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;

/** All five original health modifiers and both original classic-revive items. */
public final class TreasureArmorContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<DeferredItem<HealthArmorModItem>> HEALTH=List.of(health("heart_piece",5),health("heart_container",20),health("heart_booster",40),health("heart_fab",60),health("black_diamond",40));
    public static final List<DeferredItem<ReviveArmorModItem>> REVIVES=List.of(ITEMS.register("scrumpy",()->new ReviveArmorModItem("scrumpy",1)),ITEMS.register("wild_p",()->new ReviveArmorModItem("wild_p",3)));
    private static DeferredItem<HealthArmorModItem> health(String name,float health){return ITEMS.register(name,()->new HealthArmorModItem(name,health));}
    public static void register(IEventBus bus){ITEMS.register(bus);TreasureArmorRuntime.register();}
    public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("consumable"))return List.of();var stacks=new java.util.ArrayList<ItemStack>();for(var item:HEALTH)stacks.add(new ItemStack(item.get()));for(var item:REVIVES)stacks.add(new ItemStack(item.get()));return List.copyOf(stacks);}
    private TreasureArmorContent(){}
}
