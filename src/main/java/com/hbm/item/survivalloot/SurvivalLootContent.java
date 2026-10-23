package com.hbm.item.survivalloot;
import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
public final class SurvivalLootContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<M65GasMaskItem> M65=ITEMS.register("gas_mask_m65",M65GasMaskItem::new);
    public static final DeferredItem<SurvivalStarterItem> YELLOW=ITEMS.register("hazmat_kit",()->new SurvivalStarterItem(SurvivalStarterItem.Kind.YELLOW)),RED=ITEMS.register("hazmat_red_kit",()->new SurvivalStarterItem(SurvivalStarterItem.Kind.RED)),GREY=ITEMS.register("hazmat_grey_kit",()->new SurvivalStarterItem(SurvivalStarterItem.Kind.GREY)),STEALTH=ITEMS.register("stealth_boy",()->new SurvivalStarterItem(SurvivalStarterItem.Kind.STEALTH));
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(SurvivalLootContent::combat);}
    private static void combat(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))event.accept(M65.get());}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("consumable")?List.of(new ItemStack(YELLOW.get()),new ItemStack(RED.get()),new ItemStack(GREY.get()),new ItemStack(STEALTH.get())):List.of();}
    private SurvivalLootContent(){}
}
