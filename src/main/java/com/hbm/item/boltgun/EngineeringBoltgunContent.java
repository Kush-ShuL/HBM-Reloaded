package com.hbm.item.boltgun;

import com.hbm.api.tool.ToolableBlock.ToolType;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;

/** The real engineering hbm:boltgun, distinct from the75cal gun_bolter weapon. */
public final class EngineeringBoltgunContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<EngineeringBoltgunItem> ITEM=ITEMS.register("boltgun",EngineeringBoltgunItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(EngineeringBoltgunPackets::register);bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(()->ToolType.BOLT.register(new ItemStack(ITEM.get()))));}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?List.of(new ItemStack(ITEM.get())):List.of();}
    private EngineeringBoltgunContent(){}
}
