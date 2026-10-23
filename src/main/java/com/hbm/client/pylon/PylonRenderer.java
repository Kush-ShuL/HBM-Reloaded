package com.hbm.client.pylon;

import com.hbm.HbmReloaded;
import com.hbm.block.pylon.PylonBlockEntity;
import com.hbm.energy.wire.PylonConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** X5846 RenderPylonBase: each endpoint draws its own ten-segment half, including tint split. */
public final class PylonRenderer implements BlockEntityRenderer<PylonBlockEntity> {
    public PylonRenderer(BlockEntityRendererProvider.Context context) {}
    @Override public void render(PylonBlockEntity pylon,float partialTick,PoseStack poses,MultiBufferSource buffers,int light,int overlay) {
        poses.pushPose();PylonModels.worldTransform(poses,pylon.kind(),pylon.facing());PylonModels.draw(pylon.kind(),poses,buffers,light,overlay,false);poses.popPose();
        var level=pylon.getLevel();if(level==null) return;
        var texture=HbmReloaded.id("textures/models/network/"+(pylon.color()==0?"wire":"wire_greyscale")+".png");
        var consumer=buffers.getBuffer(WireRenderTypes.wire(texture));int color=0xFF000000|(pylon.color()==0?0xFFFFFF:pylon.color());
        for(BlockPos target:pylon.connected()) if(level.hasChunkAt(target)&&level.getBlockEntity(target) instanceof PylonBlockEntity other) {
            Vec3[] first=pylon.mounts(),second=other.mounts();int count=Math.min(first.length,second.length);
            for(int line=0;line<count;line++) {
                int index=secondIndex(pylon,other,line,count);Vec3 end=second[index].add(target.getX()-pylon.getBlockPos().getX(),target.getY()-pylon.getBlockPos().getY(),target.getZ()-pylon.getBlockPos().getZ());
                renderHalf(pylon,poses,consumer,first[line],first[line].lerp(end,.5),color,light);
            }
        }
    }
    static int secondIndex(PylonBlockEntity pylon,PylonBlockEntity other,int line,int count) {
        int a=pylon.legacyMetadata()-10,b=other.legacyMetadata()-10;
        return count==4&&((a==5&&b==2)||(a==2&&b==5))?(line+2)%4:line;
    }
    private static void renderHalf(PylonBlockEntity pylon,PoseStack poses,VertexConsumer consumer,Vec3 start,Vec3 end,int color,int light) {
        Vec3 delta=start.subtract(end);double girth=.03125,hyp=Math.sqrt(delta.x*delta.x+delta.z*delta.z),yaw=Math.atan2(delta.x,delta.z),pitch=Math.atan2(delta.y,hyp),rotator=Math.PI*.5;
        double iZ=Math.cos(yaw)*Math.cos(pitch+rotator)*girth,iX=Math.sin(yaw)*Math.cos(pitch+rotator)*girth,iY=Math.sin(pitch+rotator)*girth,jZ=Math.cos(yaw+rotator)*girth,jX=Math.sin(yaw+rotator)*girth;
        if(!PylonConfig.RENDER_CABLE_HANG.get()) { segment(poses,consumer,start,end,iX,iY,iZ,jX,jZ,color,light);return; }
        double hang=Math.min(delta.length()/15,2.5);Vec3 difference=end.subtract(start);
        for(int j=0;j<10;j++) {
            double sagJ=Math.sin(j/10F*Math.PI*.5)*hang,sagK=Math.sin((j+1)/10F*Math.PI*.5)*hang;
            Vec3 sample=start.add(difference.scale((j+.5)/10)).add(0,-(sagJ+sagK)*.5,0).add(pylon.getBlockPos().getX(),pylon.getBlockPos().getY(),pylon.getBlockPos().getZ());
            int sampled=LevelRenderer.getLightColor(pylon.getLevel(),BlockPos.containing(sample));
            segment(poses,consumer,start.add(difference.scale(j/10D)).add(0,-sagJ,0),start.add(difference.scale((j+1)/10D)).add(0,-sagK,0),iX,iY,iZ,jX,jZ,color,sampled);
        }
    }
    private static void segment(PoseStack poses,VertexConsumer consumer,Vec3 start,Vec3 end,double iX,double iY,double iZ,double jX,double jZ,int color,int light) {
        Vec3 delta=end.subtract(start);int wrap=(int)Math.ceil(delta.length()*8);
        if(delta.x+delta.z<0) { wrap=-wrap;jX=-jX;jZ=-jZ; }
        vertex(poses,consumer,start.add(iX,iY,iZ),0,0,color,light);vertex(poses,consumer,start.add(-iX,-iY,-iZ),0,1,color,light);vertex(poses,consumer,end.add(-iX,-iY,-iZ),wrap,1,color,light);vertex(poses,consumer,end.add(iX,iY,iZ),wrap,0,color,light);
        vertex(poses,consumer,start.add(jX,0,jZ),0,0,color,light);vertex(poses,consumer,start.add(-jX,0,-jZ),0,1,color,light);vertex(poses,consumer,end.add(-jX,0,-jZ),wrap,1,color,light);vertex(poses,consumer,end.add(jX,0,jZ),wrap,0,color,light);
    }
    private static void vertex(PoseStack poses,VertexConsumer consumer,Vec3 point,float u,float v,int color,int light) { consumer.addVertex(poses.last(),(float)point.x,(float)point.y,(float)point.z).setColor(color).setUv(u,v).setLight(light).setNormal(poses.last(),0,1,0); }
    @Override public AABB getRenderBoundingBox(PylonBlockEntity pylon) {
        AABB bounds=PylonModels.worldBounds(pylon);var level=pylon.getLevel();if(level==null) return bounds;
        var pos=pylon.getBlockPos();
        for(BlockPos target:pylon.connected()) if(level.hasChunkAt(target)&&level.getBlockEntity(target) instanceof PylonBlockEntity other) {
            Vec3[] first=pylon.mounts(),second=other.mounts();int count=Math.min(first.length,second.length);
            for(int line=0;line<count;line++) {
                Vec3 start=first[line].add(pos.getX(),pos.getY(),pos.getZ()),end=second[secondIndex(pylon,other,line,count)].add(target.getX(),target.getY(),target.getZ());
                Vec3 middle=start.lerp(end,.5);double sag=PylonConfig.RENDER_CABLE_HANG.get()?Math.min(start.distanceTo(middle)/15,2.5):0;
                bounds=bounds.minmax(new AABB(Math.min(start.x,middle.x),Math.min(start.y,middle.y)-sag,Math.min(start.z,middle.z),Math.max(start.x,middle.x),Math.max(start.y,middle.y),Math.max(start.z,middle.z)).inflate(.032));
            }
        }
        return bounds;
    }
    @Override public boolean shouldRenderOffScreen(PylonBlockEntity pylon) { return true; }
    @Override public int getViewDistance() { return 256; }
}
