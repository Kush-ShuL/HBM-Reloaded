package com.hbm.item.toolability.titanium;

import com.hbm.item.toolability.AvailableAbilities;
import com.hbm.item.toolability.ItemSwordAbility;
import com.hbm.item.toolability.ItemToolAbility;
import com.hbm.item.toolability.SteelHoeItem;
import java.util.List;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The complete five Titanium source tools; only the axe has BEHEADER0, none has recursion or stun. */
public final class TitaniumToolContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemSwordAbility> SWORD=ITEMS.register("titanium_sword",()->new ItemSwordAbility("titanium_sword",6.5F,0,TitaniumToolTier.INSTANCE,new AvailableAbilities()));
    public static final DeferredItem<ItemToolAbility> PICKAXE=ITEMS.register("titanium_pickaxe",()->new ItemToolAbility("titanium_pickaxe",4.5F,0,TitaniumToolTier.INSTANCE,ItemToolAbility.Type.PICKAXE,new AvailableAbilities()));
    public static final DeferredItem<ItemToolAbility> AXE=ITEMS.register("titanium_axe",()->new ItemToolAbility("titanium_axe",5.5F,0,TitaniumToolTier.INSTANCE,ItemToolAbility.Type.AXE,new AvailableAbilities().add(AvailableAbilities.Ability.BEHEADER,0)));
    public static final DeferredItem<ItemToolAbility> SHOVEL=ITEMS.register("titanium_shovel",()->new ItemToolAbility("titanium_shovel",3.5F,0,TitaniumToolTier.INSTANCE,ItemToolAbility.Type.SHOVEL,new AvailableAbilities()));
    public static final DeferredItem<SteelHoeItem> HOE=ITEMS.register("titanium_hoe",()->new SteelHoeItem(TitaniumToolTier.INSTANCE));
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(TitaniumToolContent::creative);}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))event.accept(SWORD.get());if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))for(var item:List.of(PICKAXE,AXE,SHOVEL,HOE))event.accept(item.get());}
    private TitaniumToolContent(){}
}
