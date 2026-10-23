package com.hbm.client.turret.fritz;

import com.hbm.client.model.*;
import com.hbm.client.turret.rotary.RotaryTurretRenderer;
import com.hbm.energy.HbmEnergy;
import com.hbm.fluid.network.FluidNetworks;
import com.hbm.machine.turret.fritz.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;

/** Original separate Base/Carriage/Gun with yaw then1.5-high Z-pivot pitch and HE-or-fluid plugs. */
public final class FritzTurretRenderer implements BlockEntityRenderer<FritzTurretEntity> {
    static final ResourceLocation COMMON=FritzTurretContent.id("models/turrets/turret_chekhov.obj"),MODEL=FritzTurretContent.id("models/turrets/turret_fritz.obj");
    public FritzTurretRenderer(BlockEntityRendererProvider.Context context){}
    static WavefrontMesh mesh(ResourceLocation model){return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model);}
    @Override public void render(FritzTurretEntity turret,float partial,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        var common=mesh(COMMON);var offset=turret.horizontalOffset();
        LegacyContactShadows.render(turret,RotaryTurretRenderer.shadow(common),new org.joml.Matrix4f().translation((float)offset.x,0,(float)offset.z),poses,buffers);
        poses.pushPose();
        try{
            poses.translate(offset.x,0,offset.z);connectors(turret,common,poses,buffers,light,overlay);
            RotaryTurretRenderer.part(common,poses,buffers,light,overlay,"base","Base");
            poses.mulPose(Axis.YP.rotationDegrees((float)-Math.toDegrees(Mth.lerp(partial,turret.lastRotationYaw,turret.rotationYaw))-90));
            RotaryTurretRenderer.part(common,poses,buffers,light,overlay,"carriage","Carriage");
            poses.translate(0,1.5,0);poses.mulPose(Axis.ZP.rotationDegrees((float)Math.toDegrees(Mth.lerp(partial,turret.lastRotationPitch,turret.rotationPitch))));poses.translate(0,-1.5,0);
            RotaryTurretRenderer.part(mesh(MODEL),poses,buffers,light,overlay,"fritz","Gun");
        }finally{poses.popPose();}
    }
    private static void connectors(FritzTurretEntity turret,WavefrontMesh common,PoseStack poses,MultiBufferSource buffers,int light,int overlay){
        var offset=turret.horizontalOffset();int x=(int)(turret.getBlockPos().getX()+offset.x),y=turret.getBlockPos().getY(),z=(int)(turret.getBlockPos().getZ()+offset.z);
        int[][] data={{-2,0,0,0,4},{-2,-1,-1,0,4},{-1,1,-1,90,3},{0,1,0,90,3},{1,0,-1,180,5},{1,-1,0,180,5},{0,-2,-1,270,2},{-1,-2,0,270,2}};
        for(var plug:data){
            var pos=new BlockPos(x+plug[0],y,z+plug[1]);var side=Direction.from3DDataValue(plug[4]).getOpposite();var level=turret.getLevel();if(level==null||!level.hasChunkAt(pos))continue;
            boolean connected=level.getCapability(HbmEnergy.HE,pos,side)!=null;
            if(!connected){var fluid=level.getCapability(FluidNetworks.NATIVE_FLUID,pos,side);connected=fluid!=null&&fluid.canConnect(turret.tank.getTankType(),side);}
            if(!connected)continue;
            poses.pushPose();poses.mulPose(Axis.YP.rotationDegrees(plug[3]));poses.translate(0,0,plug[2]);RotaryTurretRenderer.part(common,poses,buffers,light,overlay,"connector","Connectors");poses.popPose();
        }
    }
    @Override public boolean shouldRenderOffScreen(FritzTurretEntity turret){return true;}
    @Override public int getViewDistance(){return 256;}
    // The rotated gun's furthest vertex is<2.28 from(0,1.5,0); include source base/plugs and contact plane.
    @Override public AABB getRenderBoundingBox(FritzTurretEntity turret){var center=turret.getBlockPos();var offset=turret.horizontalOffset();return new AABB(center.getX()+offset.x-3,center.getY()-1,center.getZ()+offset.z-3,center.getX()+offset.x+3,center.getY()+4,center.getZ()+offset.z+3);}
}
