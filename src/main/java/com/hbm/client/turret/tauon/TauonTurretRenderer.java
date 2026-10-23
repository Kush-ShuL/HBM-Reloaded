package com.hbm.client.turret.tauon;
import com.hbm.client.model.*;
import com.hbm.client.reactor.dfc.components.DfcComponentBeams;
import com.hbm.client.turret.rotary.RotaryTurretRenderer;
import com.hbm.machine.turret.tauon.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
/** Original RenderTurretTauon: ordinary turret yaw/pitch, source RANDOM/LINE beam, separate negative-X rotor. */
public final class TauonTurretRenderer implements BlockEntityRenderer<TauonTurretEntity> {
    static final ResourceLocation COMMON=TauonTurretContent.id("models/turrets/turret_chekhov.obj"),MODEL=TauonTurretContent.id("models/turrets/turret_tauon.obj");
    public TauonTurretRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh mesh(ResourceLocation model){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model);}
    @Override public void render(TauonTurretEntity turret,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        var common=mesh(COMMON);var offset=turret.horizontalOffset();
        LegacyContactShadows.render(turret,RotaryTurretRenderer.shadow(common),new org.joml.Matrix4f().translation((float)offset.x,0,(float)offset.z),poses,buffers);
        poses.pushPose();
        try{
            poses.translate(offset.x,0,offset.z);RotaryTurretRenderer.connectors(turret,common,poses,buffers,light,overlay);
            RotaryTurretRenderer.part(common,poses,buffers,light,overlay,"base","Base");
            poses.mulPose(Axis.YP.rotationDegrees((float)-Math.toDegrees(Mth.lerp(partial,turret.lastRotationYaw,turret.rotationYaw))-90));
            RotaryTurretRenderer.part(common,poses,buffers,light,overlay,"carriage","Carriage");
            poses.translate(0,1.5,0);poses.mulPose(Axis.ZP.rotationDegrees((float)Math.toDegrees(Mth.lerp(partial,turret.lastRotationPitch,turret.rotationPitch))));poses.translate(0,-1.5,0);
            var cannon=mesh(MODEL);RotaryTurretRenderer.part(cannon,poses,buffers,light,overlay,"tauon","Cannon");
            if(turret.beam>0){
                poses.pushPose();poses.translate(0,1.5,0);
                // The shared BeamPronter's positive-Z skeleton is rotated onto the source positive-X skeleton.
                poses.mulPose(Axis.YP.rotationDegrees(90));
                DfcComponentBeams.draw(poses,buffers,turret.lastDist,true,false,0xffa200,0xffd000,(int)turret.getLevel().getGameTime()/5%360,(int)turret.lastDist+1,.1F,0,0);
                poses.popPose();
            }
            float rotation=turret.lastSpin+(turret.spin-turret.lastSpin)*partial;
            poses.translate(0,1.375,0);poses.mulPose(Axis.XN.rotationDegrees(rotation));poses.translate(0,-1.375,0);
            RotaryTurretRenderer.part(cannon,poses,buffers,light,overlay,"tauon","Rotor");
        }finally{poses.popPose();}
    }
    @Override public boolean shouldRenderOffScreen(TauonTurretEntity turret){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(TauonTurretEntity turret){return new AABB(Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY);}
}
