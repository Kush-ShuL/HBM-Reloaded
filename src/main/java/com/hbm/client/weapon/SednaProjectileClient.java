package com.hbm.client.weapon;

import com.hbm.entity.projectile.*;
import com.hbm.client.model.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.hbm.weapon.sedna.SednaProjectiles;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Original config-driven MK4/Beam rendering. Cluster's null callback still emits no mesh. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class SednaProjectileClient {
    @SubscribeEvent public static void register(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(SednaProjectiles.BULLET.get(),ConfiguredBulletRenderer::new);event.registerEntityRenderer(SednaProjectiles.BEAM.get(),BeamRenderer::new);event.registerEntityRenderer(SednaProjectiles.COIN.get(),CoinRenderer::new);}
    public static final class ConfiguredBulletRenderer extends EntityRenderer<EntityBulletBaseMK4>{
        public ConfiguredBulletRenderer(EntityRendererProvider.Context context){super(context);shadowRadius=0;}
        @Override public void render(EntityBulletBaseMK4 bullet,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
            if(bullet.config==null)bullet.config=bullet.getBulletConfig();if(bullet.config==null)return;
            poses.pushPose();
            if(bullet.config.renderRotations){poses.mulPose(Axis.YP.rotationDegrees(bullet.yRotO+(bullet.getYRot()-bullet.yRotO)*partial-90));poses.mulPose(Axis.ZP.rotationDegrees(bullet.xRotO+(bullet.getXRot()-bullet.xRotO)*partial+180));}
            if(bullet.config.renderer!=null){com.hbm.client.weapon.pepperbox.GunBulletRenderContext.begin(poses,buffers,light);try{bullet.config.renderer.accept(bullet,partial);}finally{com.hbm.client.weapon.pepperbox.GunBulletRenderContext.end();}}
            poses.popPose();
        }
        @Override public ResourceLocation getTextureLocation(EntityBulletBaseMK4 bullet){return ResourceLocation.fromNamespaceAndPath("hbm","textures/misc/universal.png");}
    }
    private static final class BeamRenderer extends EntityRenderer<EntityBulletBeamBase>{
        BeamRenderer(EntityRendererProvider.Context context){super(context);shadowRadius=0;}
        @Override public void render(EntityBulletBeamBase beam,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
            if(beam.config==null)beam.config=beam.getBulletConfig();if(beam.config==null)return;
            poses.pushPose();
            if(beam.config.renderRotations){poses.mulPose(Axis.YP.rotationDegrees(beam.yRotO+(beam.getYRot()-beam.yRotO)*partial-90));poses.mulPose(Axis.ZP.rotationDegrees(beam.xRotO+(beam.getXRot()-beam.xRotO)*partial+180));}
            if(beam.config.rendererBeam!=null){SednaBeamRenderContext.begin(poses,buffers,light);try{beam.config.rendererBeam.accept(beam,partial);}finally{SednaBeamRenderContext.end();}}
            poses.popPose();
        }
        @Override public ResourceLocation getTextureLocation(EntityBulletBeamBase beam){return ResourceLocation.parse("hbm:textures/misc/universal.png");}
    }
    private static final class CoinRenderer extends EntityRenderer<EntityCoin>{
        private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/trinkets/chip.obj"),TEXTURE=ResourceLocation.parse("hbm:textures/models/trinkets/chip_gold.png");
        CoinRenderer(EntityRendererProvider.Context context){super(context);shadowRadius=0;}
        @Override public void render(EntityCoin coin,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
            poses.pushPose();poses.mulPose(Axis.YN.rotationDegrees(coin.yRotO+(coin.getYRot()-coin.yRotO)*partial-90));poses.mulPose(Axis.ZP.rotationDegrees((coin.tickCount+partial)*45));poses.scale(.125F,.125F,.125F);
            LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL),poses,buffers.getBuffer(RenderType.entityCutout(TEXTURE)),light,OverlayTexture.NO_OVERLAY,0xffffffff,LegacyObjRenderer.Convention.HFR);poses.popPose();
        }
        @Override public ResourceLocation getTextureLocation(EntityCoin coin){return TEXTURE;}
    }

}
