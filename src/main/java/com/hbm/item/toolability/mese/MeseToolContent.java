package com.hbm.item.toolability.mese;
import com.hbm.item.toolability.*;
import static com.hbm.item.toolability.AvailableAbilities.Ability.*;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;
/** The actual source item and its ten independently selectable/passive definitions. All execution remains in the sole tool engine. */
public final class MeseToolContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final AvailableAbilities ABILITIES=new AvailableAbilities().add(HAMMER,2).add(HAMMER_FLAT,2).add(RECURSION,2)
     .add(CRYSTALLIZER,0).add(SILK,0).add(LUCK,5).add(EXPLOSION,3).add(STUN,3).add(PHOSPHORUS,0).add(BEHEADER,0);
 public static final DeferredItem<ItemToolAbility> PICKAXE=ITEMS.register("mese_pickaxe",()->new ItemToolAbility("mese_pickaxe",35F,0,MeseTier.INSTANCE,ItemToolAbility.Type.MINER,ABILITIES).setDepthRockBreaker());
 public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(MeseToolContent::creative);}
 private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))event.accept(PICKAXE.get());}
 private MeseToolContent(){}
}
