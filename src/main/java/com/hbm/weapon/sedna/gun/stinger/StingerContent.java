package com.hbm.weapon.sedna.gun.stinger;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.*;
public final class StingerContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemGunStinger> STINGER=ITEMS.register("gun_stinger",StingerFactory::create);
    private StingerContent(){}
    public static void register(IEventBus bus,ModContainer container){StingerSettings.register(container);ITEMS.register(bus);}
    public static List<ItemStack> allStacks(){return List.of(new ItemStack(STINGER.get()));}
    public static List<ItemStack> indexedItems(){return allStacks();}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?allStacks():List.of();}
}
