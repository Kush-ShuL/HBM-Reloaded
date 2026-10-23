package com.hbm.item.meteoritesword;
import java.util.List;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
/** Actual twelve-stage source meteorite sword family; one material/behavior/renderer implementation. */
public final class MeteoriteSwordContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<MeteoriteSwordItem> IRRADIATED=ITEMS.register("meteorite_sword_irradiated",()->new MeteoriteSwordItem("meteorite_sword_irradiated",35,.75F,1,0)),FUSED=ITEMS.register("meteorite_sword_fused",()->new MeteoriteSwordItem("meteorite_sword_fused",50,1,0,.5F));
 public static final DeferredItem<MeteoriteSwordItem> BASE=ITEMS.register("meteorite_sword",()->new MeteoriteSwordItem("meteorite_sword",9.0F,0F,0F,0F));
 public static final DeferredItem<MeteoriteSwordItem> SEARED=ITEMS.register("meteorite_sword_seared",()->new MeteoriteSwordItem("meteorite_sword_seared",10.0F,1.0F,0.5F,0.0F));
 public static final DeferredItem<MeteoriteSwordItem> REFORGED=ITEMS.register("meteorite_sword_reforged",()->new MeteoriteSwordItem("meteorite_sword_reforged",12.5F,0.5F,1.0F,1.0F));
 public static final DeferredItem<MeteoriteSwordItem> HARDENED=ITEMS.register("meteorite_sword_hardened",()->new MeteoriteSwordItem("meteorite_sword_hardened",15.0F,0.25F,0.25F,0.25F));
 public static final DeferredItem<MeteoriteSwordItem> ALLOYED=ITEMS.register("meteorite_sword_alloyed",()->new MeteoriteSwordItem("meteorite_sword_alloyed",17.5F,0.0F,0.5F,1.0F));
 public static final DeferredItem<MeteoriteSwordItem> MACHINED=ITEMS.register("meteorite_sword_machined",()->new MeteoriteSwordItem("meteorite_sword_machined",20.0F,1.0F,1.0F,0.0F));
 public static final DeferredItem<MeteoriteSwordItem> TREATED=ITEMS.register("meteorite_sword_treated",()->new MeteoriteSwordItem("meteorite_sword_treated",22.5F,0.5F,1.0F,0.5F));
 public static final DeferredItem<MeteoriteSwordItem> ETCHED=ITEMS.register("meteorite_sword_etched",()->new MeteoriteSwordItem("meteorite_sword_etched",25.0F,1.0F,1.0F,0.5F));
 public static final DeferredItem<MeteoriteSwordItem> BRED=ITEMS.register("meteorite_sword_bred",()->new MeteoriteSwordItem("meteorite_sword_bred",30.0F,0.5F,0.5F,0.0F));
 public static final DeferredItem<MeteoriteSwordItem> BALEFUL=ITEMS.register("meteorite_sword_baleful",()->new MeteoriteSwordItem("meteorite_sword_baleful",75.0F,0.0F,1.0F,0.0F));
 public static List<ItemStack> allItems(){return List.of(new ItemStack(BASE.get()),new ItemStack(SEARED.get()),new ItemStack(REFORGED.get()),new ItemStack(HARDENED.get()),new ItemStack(ALLOYED.get()),new ItemStack(MACHINED.get()),new ItemStack(TREATED.get()),new ItemStack(ETCHED.get()),new ItemStack(BRED.get()),new ItemStack(IRRADIATED.get()),new ItemStack(FUSED.get()),new ItemStack(BALEFUL.get()));}
 public static List<ItemStack> indexedItems(){return List.of(new ItemStack(SEARED.get()),new ItemStack(REFORGED.get()),new ItemStack(HARDENED.get()),new ItemStack(ALLOYED.get()),new ItemStack(MACHINED.get()),new ItemStack(TREATED.get()),new ItemStack(ETCHED.get()),new ItemStack(BRED.get()),new ItemStack(IRRADIATED.get()),new ItemStack(FUSED.get()),new ItemStack(BALEFUL.get()));}
 public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener((BuildCreativeModeTabContentsEvent event)->{if(event.getTabKey()==CreativeModeTabs.COMBAT){for(var stack:allItems())event.accept(stack);}});}
 private MeteoriteSwordContent(){}
}
