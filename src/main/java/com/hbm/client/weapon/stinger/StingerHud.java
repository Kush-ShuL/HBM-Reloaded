package com.hbm.client.weapon.stinger;
import com.hbm.client.weapon.pepperbox.GunHudComponents;
import com.hbm.weapon.sedna.gun.*;
import com.hbm.weapon.sedna.gun.stinger.ItemGunStinger;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/** Original ItemGunStinger.renderHUD / RenderScreenOverlay.renderStingerLockon.
 * Only this gun's crosshair is intercepted; common ammo/durability panels remain the sole implementation. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class StingerHud {
    private StingerHud(){}
    @SubscribeEvent(priority=EventPriority.HIGHEST) public static void render(RenderGuiLayerEvent.Pre event){
        var mc=Minecraft.getInstance();if(mc.player==null||mc.options.hideGui||!event.getName().equals(VanillaGuiLayers.CROSSHAIR))return;
        var stack=mc.player.getMainHandItem();if(!(stack.getItem() instanceof ItemGunStinger gun))return;
        event.setCanceled(true);if(ItemGunBaseNT.aimingProgress<1F)return;
        var graphics=event.getGuiGraphics();var cross=gun.getConfig(stack,0).getCrosshair(stack);
        if(cross!=Crosshair.NONE){graphics.flush();RenderSystem.enableBlend();RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,GlStateManager.SourceFactor.ONE,GlStateManager.DestFactor.ZERO);
            graphics.blit(GunHudComponents.OVERLAY,graphics.guiWidth()/2-cross.size/2,graphics.guiHeight()/2-cross.size/2,cross.x,cross.y,cross.size,cross.size,256,256);graphics.flush();RenderSystem.defaultBlendFunc();RenderSystem.disableBlend();}
        int progress=(int)(ItemGunStinger.lockon*28);graphics.flush();RenderSystem.disableBlend();
        graphics.blit(GunHudComponents.OVERLAY,graphics.guiWidth()/2-15,graphics.guiHeight()/2+18,146,18,30,10,256,256);
        graphics.blit(GunHudComponents.OVERLAY,graphics.guiWidth()/2-14,graphics.guiHeight()/2+19,147,29,progress,8,256,256);
        graphics.flush();
    }
}
