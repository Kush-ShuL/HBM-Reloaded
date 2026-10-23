package com.hbm.machine.reactor.pile.core;
import com.hbm.api.tool.ToolableBlock.ToolType;import com.hbm.item.tool.ToolingItem;import java.util.List;import net.minecraft.world.item.ItemStack;import net.neoforged.bus.api.IEventBus;import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;import net.neoforged.neoforge.registries.*;
/** Real ItemTooling100/0 hand drills, shared tool behavior and source craft degradation. */
public final class PileHandDrillContent {
 private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
 public static final DeferredItem<ToolingItem> NORMAL=ITEMS.register("hand_drill",()->new ToolingItem(ToolType.HAND_DRILL,100)),DESH=ITEMS.register("hand_drill_desh",()->new ToolingItem(ToolType.HAND_DRILL,0));
 public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(PileHandDrillContent::setup);}
 private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{NORMAL.get().registerSourceType();DESH.get().registerSourceType();});}
 public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?List.of(new ItemStack(NORMAL.get()),new ItemStack(DESH.get())):List.of();}
 private PileHandDrillContent(){}
}
