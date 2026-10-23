package com.hbm.client.weapon.legendary;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
/** Original scope UV crop is based on a9:16 smaller viewport dimension, rather than stretching the image. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class GunScopeOverlay {
    @SubscribeEvent(priority=EventPriority.HIGHEST) public static void render(RenderGuiLayerEvent.Pre event){
        var mc=Minecraft.getInstance();if(!event.getName().equals(VanillaGuiLayers.HOTBAR)||mc.player==null||mc.options.hideGui)return;
        var stack=com.hbm.weapon.sedna.gun.GunHands.viewStack(mc.player);if(!(stack.getItem() instanceof ItemGunBaseNT gun)||ItemGunBaseNT.aimingProgress!=1||ItemGunBaseNT.prevAimingProgress!=1)return;
        var texture=gun.getConfig(stack,0).getScopeTexture(stack);if(texture==null)return;
        var graphics=event.getGuiGraphics();double width=graphics.guiWidth(),height=graphics.guiHeight(),smallest=9D/16D,divisor=Math.min(width,height)/smallest,largest=Math.max(width,height)/divisor;
        float v0=(float)(.5-(height<width?smallest:largest)/2),v1=(float)(.5+(height<width?smallest:largest)/2),u0=(float)(.5-(width<height?smallest:largest)/2),u1=(float)(.5+(width<height?smallest:largest)/2);
        graphics.flush();RenderSystem.disableDepthTest();RenderSystem.depthMask(false);RenderSystem.enableBlend();RenderSystem.defaultBlendFunc();RenderSystem.setShader(GameRenderer::getPositionTexShader);RenderSystem.setShaderTexture(0,texture);RenderSystem.setShaderColor(1,1,1,1);
        var matrix=graphics.pose().last().pose();var out=Tesselator.getInstance().begin(VertexFormat.Mode.QUADS,DefaultVertexFormat.POSITION_TEX);out.addVertex(matrix,0,(float)height,-300).setUv(u0,v1);out.addVertex(matrix,(float)width,(float)height,-300).setUv(u1,v1);out.addVertex(matrix,(float)width,0,-300).setUv(u1,v0);out.addVertex(matrix,0,0,-300).setUv(u0,v0);BufferUploader.drawWithShader(out.buildOrThrow());RenderSystem.enableDepthTest();RenderSystem.depthMask(true);RenderSystem.disableBlend();
    }
    private GunScopeOverlay(){}
}
