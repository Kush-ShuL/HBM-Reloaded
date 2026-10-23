package com.hbm.item.toolability;

import static com.hbm.item.toolability.AvailableAbilities.Ability.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

/** Five actual source steel registrations, using original vanilla combat/tools tabs and existing unique ability runtime. */
public final class SteelToolContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemSwordAbility> SWORD=ITEMS.register("steel_sword",()->new ItemSwordAbility("steel_sword",6,new AvailableAbilities().add(STUN,0)));
    public static final DeferredItem<ItemToolAbility> PICKAXE=ITEMS.register("steel_pickaxe",()->new ItemToolAbility("steel_pickaxe",4,ItemToolAbility.Type.PICKAXE,new AvailableAbilities().add(RECURSION,0)));
    public static final DeferredItem<ItemToolAbility> AXE=ITEMS.register("steel_axe",()->new ItemToolAbility("steel_axe",5,ItemToolAbility.Type.AXE,new AvailableAbilities().add(RECURSION,0).add(BEHEADER,0)));
    public static final DeferredItem<ItemToolAbility> SHOVEL=ITEMS.register("steel_shovel",()->new ItemToolAbility("steel_shovel",3,ItemToolAbility.Type.SHOVEL,new AvailableAbilities().add(RECURSION,0)));
    public static final DeferredItem<SteelHoeItem> HOE=ITEMS.register("steel_hoe",()->new SteelHoeItem());
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(SteelToolContent::creative);}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.COMBAT))event.accept(SWORD.get());if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)){event.accept(PICKAXE.get());event.accept(AXE.get());event.accept(SHOVEL.get());event.accept(HOE.get());}}
    private SteelToolContent(){}
}
