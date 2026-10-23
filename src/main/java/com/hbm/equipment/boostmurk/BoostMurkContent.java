package com.hbm.equipment.boostmurk;

import com.hbm.equipment.jetpack.SourceJetpackItem;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class BoostMurkContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<SourceJetpackItem> BOOST=ITEMS.register("jetpack_boost",()->new SourceJetpackItem(SourceJetpackItem.Kind.BOOST));
    public static final DeferredItem<SourceWingsItem> MURK=ITEMS.register("wings_murk",()->new SourceWingsItem(false));
    public static final DeferredItem<SourceWingsItem> LIMP=ITEMS.register("wings_limp",()->new SourceWingsItem(true));
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(BoostMurkContent::creative);}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))indexedItems().forEach(event::accept);}
    public static List<ItemStack> indexedItems(){return List.of(new ItemStack(BOOST.get()),new ItemStack(MURK.get()),new ItemStack(LIMP.get()));}
    public static List<ItemStack> creativeStacks(String tab){return List.of();}
    private BoostMurkContent(){}
}
