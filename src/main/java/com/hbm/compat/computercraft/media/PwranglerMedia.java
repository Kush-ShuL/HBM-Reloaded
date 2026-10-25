package com.hbm.compat.computercraft.media;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The original optional, labelled read-only PWRangler disk, now for CC:Tweaked. */
public final class PwranglerMedia {
    public static boolean available(){return ModList.get().isLoaded("computercraft");}
    public static void register(IEventBus bus){if(available())Loaded.register(bus);}
    public static Item item(){return Loaded.DISK.get();}

    /** No CC class is resolved until the original optional-mod condition succeeds. */
    private static final class Loaded {
        private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
        private static final net.neoforged.neoforge.registries.DeferredItem<Item> DISK=ITEMS.register("pwrangler_disk",()->new Item(new Item.Properties().stacksTo(1)));
        private static final dan200.computercraft.api.media.IMedia MEDIA=new dan200.computercraft.api.media.IMedia(){
            @Override public String getLabel(net.minecraft.core.HolderLookup.Provider registries,ItemStack stack){return "PWRangler";}
            @Override public dan200.computercraft.api.filesystem.Mount createDataMount(ItemStack stack,net.minecraft.server.level.ServerLevel level){return dan200.computercraft.api.ComputerCraftAPI.createResourceMount(level.getServer(),"hbm","lua/pwrangler");}
        };
        private static void register(IEventBus bus){
            ITEMS.register(bus);
            bus.addListener((net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event)->event.registerItem(dan200.computercraft.api.media.MediaCapability.get(),(stack,context)->MEDIA,DISK.get()));
            bus.addListener((BuildCreativeModeTabContentsEvent event)->{
                if(event.getTabKey().location().equals(ResourceLocation.parse("hbm:control")))event.accept(DISK.get());
            });
        }
    }
    private PwranglerMedia(){}
}
