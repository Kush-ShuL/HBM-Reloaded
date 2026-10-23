package com.hbm.item.structureloot;

import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

/** Fully implemented small source families which unblock whole natural-structure pools. */
public final class StructureLootEssentials {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<StructureFoodItem> TWINKIE=ITEMS.register("twinkie",()->new StructureFoodItem("twinkie",3,.25F));
    public static final DeferredItem<StructureFoodItem> DEFINITELY_FOOD=ITEMS.register("definitelyfood",()->new StructureFoodItem("definitelyfood",3,.5F));
    public static final DeferredItem<StructureFoodItem> CEMENT=ITEMS.register("powder_cement",()->new StructureFoodItem("powder_cement",2,.5F));
    public static final DeferredItem<LeadPipeItem> LEAD_PIPE=ITEMS.register("weapon_pipe_lead",LeadPipeItem::new);
    public static final DeferredItem<ChemistrySetItem> CHEMISTRY=ITEMS.register("chemistry_set",ChemistrySetItem::new);
    public static final DeferredItem<InkArmorModItem> INK=ITEMS.register("ink",InkArmorModItem::new);
    public static void register(IEventBus bus){ITEMS.register(bus);InkArmorModItem.register();bus.addListener(StructureLootEssentials::vanillaCreative);}
    private static void vanillaCreative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))event.accept(LEAD_PIPE.get());}
    public static List<ItemStack> creativeStacks(String tab){return switch(tab){case "consumable"->List.of(new ItemStack(TWINKIE.get()),new ItemStack(DEFINITELY_FOOD.get()),new ItemStack(INK.get()));case "parts"->List.of(new ItemStack(CEMENT.get()));case "control"->List.of(new ItemStack(CHEMISTRY.get()));default->List.of();};}
    private StructureLootEssentials(){}
}
