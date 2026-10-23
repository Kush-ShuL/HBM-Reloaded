package com.hbm.client.turret.richard;

import com.hbm.client.model.*;
import com.hbm.client.turret.rotary.RotaryTurretRenderer;
import com.hbm.machine.turret.richard.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

/** Exact source yaw/carriage,1.5-high Z-pivot launcher, then17 original staggered MissileLoaded transforms. */
public final class RichardTurretRenderer implements BlockEntityRenderer<RichardTurretEntity> {
    static final ResourceLocation COMMON=RichardTurretContent.id("models/turrets/turret_chekhov.obj"),MODEL=RichardTurretContent.id("models/turrets/turret_richard.obj");
    public RichardTurretRenderer(BlockEntityRendererProvider.Context context) {}
    static WavefrontMesh mesh(ResourceLocation model) { return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model); }
    @Override public void render(RichardTurretEntity turret,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        var common=mesh(COMMON);var offset=turret.horizontalOffset();
        LegacyContactShadows.render(turret,RotaryTurretRenderer.shadow(common),new org.joml.Matrix4f().translation((float)offset.x,0,(float)offset.z),poses,buffers);
        poses.pushPose();
        try {
            poses.translate(offset.x,0,offset.z);RotaryTurretRenderer.connectors(turret,common,poses,buffers,light,overlay);
            RotaryTurretRenderer.part(common,poses,buffers,light,overlay,"base","Base");
            poses.mulPose(Axis.YP.rotationDegrees((float)-Math.toDegrees(Mth.lerp(partial,turret.lastRotationYaw,turret.rotationYaw))-90));
            RotaryTurretRenderer.part(common,poses,buffers,light,overlay,"carriage","Carriage");
            poses.translate(0,1.5,0);poses.mulPose(Axis.ZP.rotationDegrees((float)Math.toDegrees(Mth.lerp(partial,turret.lastRotationPitch,turret.rotationPitch))));poses.translate(0,-1.5,0);
            var launcher=mesh(MODEL);RotaryTurretRenderer.part(launcher,poses,buffers,light,overlay,"richard","Launcher");
            poses.translate(0,.375,.1875);
            for(int index=0;index<turret.loaded;index++) {
                RotaryTurretRenderer.part(launcher,poses,buffers,light,overlay,"richard","MissileLoaded");
                if(index==2||index==6||index==9||index==13)poses.translate(0,-.1875,.1875*2+.09375);
                else poses.translate(0,0,-.1875);
            }
        } finally { poses.popPose(); }
    }
    @Override public boolean shouldRenderOffScreen(RichardTurretEntity turret) { return true; }
    @Override public int getViewDistance() { return 256; }
    @Override public AABB getRenderBoundingBox(RichardTurretEntity turret) {
        var pos=turret.getBlockPos();var offset=turret.horizontalOffset();return new AABB(pos.getX()+offset.x-4,pos.getY()-1,pos.getZ()+offset.z-4,pos.getX()+offset.x+4,pos.getY()+5,pos.getZ()+offset.z+4);
    }
}
