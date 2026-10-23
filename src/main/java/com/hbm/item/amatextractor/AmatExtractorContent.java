package com.hbm.item.amatextractor;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.registries.*;

/** Source ModItems3646/6294 + the one ModEventHandler.onItemToss identity branch. */
public final class AmatExtractorContent {
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredItem<ItemAmatExtractor> EXTRACTOR=ITEMS.register("bismuth_tool",ItemAmatExtractor::new);
    public static void register(IEventBus bus){ITEMS.register(bus);NeoForge.EVENT_BUS.addListener(AmatExtractorContent::toss);}
    private static void toss(ItemTossEvent event){
        // Deliberately only player Q/GUI toss, not all entity joins or death drops.
        if(event.getEntity().getItem().is(EXTRACTOR.get()))event.getEntity().setInvulnerable(true);
    }
    private AmatExtractorContent(){}
}
