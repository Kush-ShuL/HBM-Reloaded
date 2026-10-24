package com.hbm.client.storage;

import com.hbm.client.gui.font.LegacyFonts;
import com.hbm.machine.storage.StorageCrateBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/** Source BlockStorageCrate.printHook only displays a custom inventory name, at the original crosshair offset. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class StorageCrateOverlay {
    @SubscribeEvent public static void render(RenderGuiLayerEvent.Pre event){if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;
        if(!event.getName().equals(VanillaGuiLayers.CROSSHAIR))return;var client=Minecraft.getInstance();
        if(client.options.hideGui||client.level==null||!(client.hitResult instanceof BlockHitResult hit)||!(client.level.getBlockEntity(hit.getBlockPos()) instanceof StorageCrateBlockEntity crate)||!crate.hasCustomName())return;
        var graphics=event.getGuiGraphics();var font=LegacyFonts.get();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2;var name=crate.getCustomName();graphics.drawString(font,name,x+1,y-9,0x404000,false);graphics.drawString(font,name,x,y-10,0xffff00,false);
    }
    private StorageCrateOverlay(){}
}
