package com.hbm.client.toolability;

import com.hbm.item.settings.client.LegacyInfoSystem;
import com.hbm.item.toolability.*;
import com.mojang.blaze3d.platform.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

/** Exact source cycle/Alt key defaults, original info-system ID11 and inverted16px area indicator. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class CobaltToolClient {
    private static final KeyMapping CYCLE=new KeyMapping("hbm.key.ability",InputConstants.Type.MOUSE,GLFW.GLFW_MOUSE_BUTTON_RIGHT,"hbm.key"),ALT=new KeyMapping("hbm.key.abilityAlt",InputConstants.Type.KEYSYM,GLFW.GLFW_KEY_LEFT_ALT,"hbm.key");
    private static boolean wasDown;
    public static void notice(CobaltPresets.Preset preset){LegacyInfoSystem.push(preset.message(),11);}
    @SubscribeEvent public static void tick(ClientTickEvent.Post event){
        var client=Minecraft.getInstance();if(client.player==null||client.level==null||client.screen!=null||!(client.player.getMainHandItem().getItem() instanceof SourceAbilityTool)){wasDown=false;while(ALT.consumeClick()){}return;}
        if(ALT.consumeClick()){client.setScreen(new CobaltToolScreen(client.player.getMainHandItem()));return;}
        boolean down=CYCLE.isDown();if(down&&!wasDown)PacketDistributor.sendToServer(new CobaltPayloads.Cycle());wasDown=down;
    }
    @SubscribeEvent public static void render(RenderGuiLayerEvent.Pre event){
        if(!event.getName().equals(VanillaGuiLayers.CROSSHAIR))return;var client=Minecraft.getInstance();if(client.options.hideGui||client.player==null||client.level==null||!(client.player.getMainHandItem().getItem() instanceof SourceAbilityTool)||CobaltPresets.read(client.player.getMainHandItem()).active().area()==0)return;
        var graphics=event.getGuiGraphics();graphics.flush();RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);
        graphics.blit(CobaltToolScreen.TEXTURE,graphics.guiWidth()/2-24+CobaltToolClientConfig.HUD_X.get(),graphics.guiHeight()/2+8+CobaltToolClientConfig.HUD_Y.get(),(CobaltPresets.read(client.player.getMainHandItem()).active().area()-1)*16,138,16,16,256,256);graphics.flush();RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();
    }
    @EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
    public static final class Registration{@SubscribeEvent public static void keys(RegisterKeyMappingsEvent event){event.register(CYCLE);event.register(ALT);}}
    private CobaltToolClient(){}
}
