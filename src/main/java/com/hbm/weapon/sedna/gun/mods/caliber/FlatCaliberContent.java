package com.hbm.weapon.sedna.gun.mods.caliber;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Eight fixed identities in exact EnumModCaliber order; source ordinals are internal factory input only. */
public final class FlatCaliberContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final List<String> NAMES=List.of("p9","p45","p22","m357","m44","r556","r762","bmg50");
    public static final List<DeferredItem<FlatCaliberItem>> KITS=NAMES.stream()
            .map(name->ITEMS.register("weapon_mod_caliber_"+name,FlatCaliberItem::new)).toList();
    public static void register(IEventBus bus){ITEMS.register(bus);}
    public static ItemStack stack(int sourceOrdinal,int count){return new ItemStack(KITS.get(sourceOrdinal).get(),count);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("weapon")?KITS.stream().map(item->new ItemStack(item.get())).toList():List.of();}
    private FlatCaliberContent(){}
}
