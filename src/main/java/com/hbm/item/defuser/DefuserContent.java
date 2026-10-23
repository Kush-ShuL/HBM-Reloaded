package com.hbm.item.defuser;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;
public final class DefuserContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<DefuserItem> DEFUSER=ITEMS.register("defuser",DefuserItem::new);
    public static final DeferredItem<GoldenDefuserItem> GOLD=ITEMS.register("defuser_gold",GoldenDefuserItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(()->{DEFUSER.get().registerSourceType();com.hbm.registry.HbmOreDictionary.registerName("record");}));GoldenDefuserItem.register();}
    public static List<ItemStack> creativeStacks(String tab){return switch(tab){case "nuke"->List.of(new ItemStack(DEFUSER.get()));case "consumable"->List.of(new ItemStack(GOLD.get()));default->List.of();};}
    private DefuserContent(){}
}
