package com.hbm.item.tool;

import com.hbm.api.tool.ToolableBlock.ToolType;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.*;

/** The two real original screwdriver registrations, source control tab, shared tool-type display registration. */
public final class ToolingContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ToolingItem> SCREWDRIVER=ITEMS.register("screwdriver",()->new ToolingItem(ToolType.SCREWDRIVER,100));
    public static final DeferredItem<ToolingItem> DESH=ITEMS.register("screwdriver_desh",()->new ToolingItem(ToolType.SCREWDRIVER,0));
    public static void register(IEventBus bus){ITEMS.register(bus);bus.addListener(ToolingContent::setup);}
    private static void setup(FMLCommonSetupEvent event){event.enqueueWork(()->{SCREWDRIVER.get().registerSourceType();DESH.get().registerSourceType();});}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("control")?List.of(new ItemStack(SCREWDRIVER.get()),new ItemStack(DESH.get())):List.of();}
    private ToolingContent(){}
}
