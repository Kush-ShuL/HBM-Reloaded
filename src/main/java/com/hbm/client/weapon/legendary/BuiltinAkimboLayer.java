package com.hbm.client.weapon.legendary;

import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.weapon.pepperbox.GunRenderOwner;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

/** Source ModEventHandlerRenderer.onRenderAkimbo: original opposite-arm caller and the gun's real separate equipped-Akimbo implementation. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class BuiltinAkimboLayer extends RenderLayer<AbstractClientPlayer,PlayerModel<AbstractClientPlayer>> {
    private BuiltinAkimboLayer(RenderLayerParent<AbstractClientPlayer,PlayerModel<AbstractClientPlayer>> parent){super(parent);}
    @SubscribeEvent public static void layers(EntityRenderersEvent.AddLayers event){for(var skin:event.getSkins()){PlayerRenderer renderer=event.getSkin(skin);if(renderer!=null)renderer.addLayer(new BuiltinAkimboLayer(renderer));}}
    @Override public void render(PoseStack poses,MultiBufferSource buffers,int light,AbstractClientPlayer owner,float walk,float walkAmount,float partial,float age,float headYaw,float headPitch){
        var stack=owner.getMainHandItem();if(!(stack.getItem() instanceof ItemGunBaseNT))return;
        var extension=IClientItemExtensions.of(stack).getCustomRenderer();if(!(extension instanceof LegacyGunRenderer renderer)||!renderer.isAkimbo(owner))return;
        poses.pushPose();GunRenderOwner.push(owner,stack);
        try{
            var arm=owner.getMainArm().getOpposite();getParentModel().translateToHand(arm,poses);
            // These are the original left-arm/Forge caller matrices. Left-handed players mirror that entire authored caller around their actual opposite wrist.
            try(var mirror=LegacyObjRenderer.mirrorX(poses,arm==HumanoidArm.RIGHT);var context=LegacyGunGL.push(poses,buffers,light,OverlayTexture.NO_OVERLAY)){
                poses.translate(-.0625,.4375,.0625);poses.translate(.25,.1875,-.1875);poses.scale(.375F,.375F,.375F);
                poses.mulPose(Axis.ZP.rotationDegrees(60));poses.mulPose(Axis.XP.rotationDegrees(-90));poses.mulPose(Axis.ZP.rotationDegrees(20));
                poses.translate(0,-.3,0);poses.scale(1.5F,1.5F,1.5F);poses.mulPose(Axis.YP.rotationDegrees(50));poses.mulPose(Axis.ZP.rotationDegrees(335));poses.translate(-.9375,-.0625,0);
                renderer.setupThirdPersonAkimbo(stack);renderer.renderEquippedAkimbo(stack,owner);
            }catch(RuntimeException|Error failure){throw failure;}catch(Exception impossible){throw new AssertionError(impossible);}
        }finally{GunRenderOwner.pop();poses.popPose();}
    }
}
