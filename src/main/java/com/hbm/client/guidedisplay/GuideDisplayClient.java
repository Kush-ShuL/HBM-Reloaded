package com.hbm.client.guidedisplay;

import com.hbm.block.guidedisplay.GuideDisplayContent;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class GuideDisplayClient {
    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
    public static final class Setup {
        @SubscribeEvent public static void setup(FMLClientSetupEvent event){GuideDisplayContent.openWiki=()->Util.getPlatform().openUri("https://nucleartech.wiki/wiki/Main_Page");}
        private Setup(){}
    }
    @SubscribeEvent public static void overlay(RenderGuiLayerEvent.Pre event){if(!com.hbm.item.settings.client.LegacyInfoSystem.lookOverlayEnabled())return;
        if(!event.getName().equals(VanillaGuiLayers.CROSSHAIR))return;var client=Minecraft.getInstance();
        if(client.options.hideGui||client.level==null||client.player==null||!(client.hitResult instanceof BlockHitResult hit)||!client.level.getBlockState(hit.getBlockPos()).is(GuideDisplayContent.BLOCK.get()))return;
        var graphics=event.getGuiGraphics();int x=graphics.guiWidth()/2+8,y=graphics.guiHeight()/2;var text=Component.translatable("overlay.hbm.guide_block.open_wiki");graphics.drawString(client.font,text,x+1,y-19,0x006000,false);graphics.drawString(client.font,text,x,y-20,0x00ff00,false);
    }
    private GuideDisplayClient(){}
}
