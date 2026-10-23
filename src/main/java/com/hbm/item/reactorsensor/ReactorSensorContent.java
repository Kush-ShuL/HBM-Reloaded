package com.hbm.item.reactorsensor;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
public final class ReactorSensorContent{
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");public static final DeferredItem<ReactorSensorItem> SENSOR=ITEMS.register("reactor_sensor",ReactorSensorItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);}public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(SENSOR.get())):List.of();}private ReactorSensorContent(){}
}
