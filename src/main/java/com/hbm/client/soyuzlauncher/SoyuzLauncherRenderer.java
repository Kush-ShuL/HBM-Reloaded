package com.hbm.client.soyuzlauncher;

import com.hbm.client.model.LegacyContactShadows;
import com.hbm.machine.soyuzlauncher.SoyuzLauncherBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;

/** Exact fixed orientation of RenderSoyuzLauncher; source towers open45 degrees during final20ticks. */
public final class SoyuzLauncherRenderer implements BlockEntityRenderer<SoyuzLauncherBlockEntity> {
    public SoyuzLauncherRenderer(BlockEntityRendererProvider.Context context){}
    @Override public void render(SoyuzLauncherBlockEntity machine,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        LegacyContactShadows.render(machine,SoyuzModels.mesh("models/launch_table/soyuz_launcher_legs.obj"),new Matrix4f().translation(.5F,-4,.5F),poses,buffers);
        double rotation=machine.rocketType>=0?0:45;if(machine.starting&&machine.countdown<20)rotation=(20-machine.countdown+partial)*45/20D;
        poses.pushPose();try{poses.translate(.5,-4,.5);SoyuzModels.launcher(rotation,poses,buffers,light,overlay);if(machine.rocketType>=0){poses.translate(0,5,0);SoyuzModels.rocket(machine.rocketType,poses,buffers,light,overlay);}}finally{poses.popPose();}
    }
    @Override public boolean shouldRenderOffScreen(SoyuzLauncherBlockEntity machine){return true;}
    @Override public int getViewDistance(){return 256;}
    @Override public AABB getRenderBoundingBox(SoyuzLauncherBlockEntity machine){var p=machine.getBlockPos();return new AABB(p.getX()-10,p.getY()-4,p.getZ()-10,p.getX()+11,p.getY()+55,p.getZ()+11);}
}
