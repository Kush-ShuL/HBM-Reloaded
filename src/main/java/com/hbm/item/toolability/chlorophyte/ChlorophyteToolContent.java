package com.hbm.item.toolability.chlorophyte;
import com.hbm.item.toolability.*;
import static com.hbm.item.toolability.AvailableAbilities.Ability.*;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
/** Only the five actual pickaxes required by ToolRecipes70..74/174/179/190. No sibling axes or invented materials. */
public final class ChlorophyteToolContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<ItemToolAbility> DECORATED_COBALT=ITEMS.register("cobalt_decorated_pickaxe",()->new ItemToolAbility("cobalt_decorated_pickaxe",6F,0,UpstreamToolTier.DECORATED_COBALT,ItemToolAbility.Type.PICKAXE,new AvailableAbilities().add(RECURSION,1).add(HAMMER,0).add(HAMMER_FLAT,0).add(SILK,0).add(LUCK,2)));
 public static final DeferredItem<ItemToolAbility> STARMETAL=ITEMS.register("starmetal_pickaxe",()->new ItemToolAbility("starmetal_pickaxe",8F,0,UpstreamToolTier.STARMETAL,ItemToolAbility.Type.PICKAXE,new AvailableAbilities().add(RECURSION,3).add(HAMMER,1).add(HAMMER_FLAT,1).add(SILK,0).add(LUCK,4).add(STUN,1)));
 public static final DeferredItem<ItemToolAbility> BISMUTH=ITEMS.register("bismuth_pickaxe",()->new ItemToolAbility("bismuth_pickaxe",15F,0,UpstreamToolTier.BISMUTH,ItemToolAbility.Type.MINER,new AvailableAbilities().add(HAMMER,1).add(HAMMER_FLAT,1).add(RECURSION,1).add(SHREDDER,0).add(LUCK,1).add(SILK,0).add(STUN,2).add(VAMPIRE,0).add(BEHEADER,0)).setDepthRockBreaker());
 public static final DeferredItem<ItemToolAbility> VOLCANIC=ITEMS.register("volcanic_pickaxe",()->new ItemToolAbility("volcanic_pickaxe",15F,0,UpstreamToolTier.VOLCANIC,ItemToolAbility.Type.MINER,new AvailableAbilities().add(HAMMER,1).add(HAMMER_FLAT,1).add(RECURSION,1).add(SMELTER,0).add(LUCK,2).add(SILK,0).add(FIRE,0).add(VAMPIRE,0).add(BEHEADER,0)).setDepthRockBreaker());
 public static final DeferredItem<ItemToolAbility> CHLOROPHYTE=ITEMS.register("chlorophyte_pickaxe",()->new ItemToolAbility("chlorophyte_pickaxe",20F,0,UpstreamToolTier.CHLOROPHYTE,ItemToolAbility.Type.MINER,new AvailableAbilities().add(HAMMER,1).add(HAMMER_FLAT,1).add(RECURSION,1).add(LUCK,3).add(CENTRIFUGE,0).add(MERCURY,0).add(STUN,3).add(VAMPIRE,2).add(BEHEADER,0)).setDepthRockBreaker());
 public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(ChlorophyteToolContent::creative);}
 private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))for(var item:ITEMS.getEntries())event.accept(item.get());}
 private ChlorophyteToolContent(){}
}
