package com.hbm.item.toolability;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.*;

public final class CobaltToolContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<CobaltPickaxeItem> PICKAXE=ITEMS.register("cobalt_pickaxe",CobaltPickaxeItem::new);
    public static void register(IEventBus bus,ModContainer container){CobaltToolConfig.register(container);container.registerConfig(net.neoforged.fml.config.ModConfig.Type.CLIENT,CobaltToolClientConfig.SPEC,"hbm-tool-abilities-client.toml");ITEMS.register(bus);bus.addListener(CobaltPayloads::register);bus.addListener(CobaltToolContent::creative);CobaltBreaks.register();}
    private static void creative(BuildCreativeModeTabContentsEvent event){if(event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES))event.accept(PICKAXE.get());}
    private CobaltToolContent(){}
}
