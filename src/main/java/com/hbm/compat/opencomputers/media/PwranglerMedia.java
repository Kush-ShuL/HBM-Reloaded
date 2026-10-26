package com.hbm.compat.opencomputers.media;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Physical registration for the original optional OC PWRangler disk. */
public final class PwranglerMedia {
    public static boolean available(){return ModList.get().isLoaded("opencomputers");}
    public static void register(IEventBus bus){if(available())Loaded.register(bus);}
    public static Item item(){return Loaded.DISK.get();}

    /** Physical content has no optional computer API linkage. */
    private static final class Loaded {
        private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
        private static final net.neoforged.neoforge.registries.DeferredItem<Item> DISK=ITEMS.register("pwrangler_disk",()->new Item(new Item.Properties().stacksTo(1)));
        private static void register(IEventBus bus){
            ITEMS.register(bus);
            bus.addListener((BuildCreativeModeTabContentsEvent event)->{
                if(event.getTabKey().location().equals(ResourceLocation.parse("hbm:control")))event.accept(DISK.get());
            });
        }
    }
    private PwranglerMedia(){}
}
