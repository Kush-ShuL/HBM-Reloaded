package com.hbm.client.entity.wastepearl;

import com.hbm.entity.grenade.waste.EntityWastePearl;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** The actual1.7 RenderManager Entity.class fallback: untextured white six-face AABB. */
public final class WastePearlRenderer extends EntityRenderer<EntityWastePearl> {
    private static final ResourceLocation WHITE=ResourceLocation.parse("hbm:textures/entity/waste_pearl_fallback_white.png");
    public WastePearlRenderer(EntityRendererProvider.Context context){super(context);shadowRadius=0;}
    @Override public ResourceLocation getTextureLocation(EntityWastePearl entity){return WHITE;}
    @Override public void render(EntityWastePearl entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        var box=entity.getBoundingBox().move(-entity.getX(),-entity.getY(),-entity.getZ());
        float a=(float)box.minX,b=(float)box.minY,c=(float)box.minZ,d=(float)box.maxX,e=(float)box.maxY,f=(float)box.maxZ;
        var out=buffers.getBuffer(RenderType.entitySolid(WHITE));var p=poses.last();
        face(out,p,light,0,0,-1,a,e,c,d,e,c,d,b,c,a,b,c);
        face(out,p,light,0,0,1,a,b,f,d,b,f,d,e,f,a,e,f);
        face(out,p,light,0,-1,0,a,b,c,d,b,c,d,b,f,a,b,f);
        face(out,p,light,0,1,0,a,e,f,d,e,f,d,e,c,a,e,c);
        face(out,p,light,-1,0,0,a,b,f,a,e,f,a,e,c,a,b,c);
        face(out,p,light,1,0,0,d,b,c,d,e,c,d,e,f,d,b,f);
        super.render(entity,yaw,partial,poses,buffers,light);
    }
    private static void face(VertexConsumer out,PoseStack.Pose p,int light,float nx,float ny,float nz,float... xyz){
        for(int i=0;i<12;i+=3)out.addVertex(p.pose(),xyz[i],xyz[i+1],xyz[i+2]).setColor(-1).setUv(0,0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(p,nx,ny,nz);
    }
}
