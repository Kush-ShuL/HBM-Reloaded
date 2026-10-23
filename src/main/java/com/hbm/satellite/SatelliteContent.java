package com.hbm.satellite;
import java.util.*;import com.hbm.satellite.item.*;import com.hbm.machine.radar.RadarSatelliteBridge;import net.minecraft.world.item.ItemStack;import net.neoforged.bus.api.IEventBus;import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;import net.neoforged.neoforge.registries.*;
/** Real frequency chips and source metadata payloads; only real implemented orbital classes are admitted to creative. */
public final class SatelliteContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<SatelliteChipItem> CHIP=ITEMS.register("sat_chip",()->new SatelliteChipItem()),RELAY=ITEMS.register("sat_relay",()->new SatelliteChipItem());
 public static final List<DeferredItem<SatelliteItem>> PAYLOADS=SatelliteItem.register(ITEMS);
 public static final DeferredItem<SatelliteChipItem> MAPPER=ITEMS.register("sat_mapper",()->new SatelliteChipItem(SatelliteKind.SPY)),SCANNER=ITEMS.register("sat_scanner",()->new SatelliteChipItem(SatelliteKind.SCANNER)),RADAR=ITEMS.register("sat_radar",()->new SatelliteChipItem(SatelliteKind.RADAR)),RESONATOR=ITEMS.register("sat_resonator",()->new SatelliteChipItem(SatelliteKind.XENIUM_RESONATOR));
 public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener((FMLCommonSetupEvent event)->event.enqueueWork(()->RadarSatelliteBridge.backend=new RadarSatelliteBridge.Backend(){public boolean isRelay(ItemStack stack){return stack.getItem()==RELAY.get();}public void onCoordinate(net.minecraft.world.level.Level level,net.minecraft.world.entity.player.Player player,ItemStack relay,int x,int y,int z){if(!(level instanceof net.minecraft.server.level.ServerLevel world))return;var data=SatelliteSavedData.getData(world);var sat=data.getSatFromFreq(SatelliteFrequency.getFreqS(relay));if(sat!=null)sat.onCoordAction(world,player,x,y,z);}}));}
 public static List<ItemStack> creativeStacks(String tab){if(!tab.equals("missile"))return List.of();var stacks=new ArrayList<ItemStack>();stacks.add(new ItemStack(CHIP.get()));stacks.add(new ItemStack(RELAY.get()));stacks.addAll(SatelliteItem.creativeStacks());return stacks;}
 private SatelliteContent(){}
}
