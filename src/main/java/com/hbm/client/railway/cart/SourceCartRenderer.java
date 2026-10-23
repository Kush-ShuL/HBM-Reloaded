package com.hbm.client.railway.cart;
import com.hbm.client.model.*;
import com.hbm.railway.cart.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
/** Actual RenderNeoCart: unmoving carriage then independently rolling bucket/load, original Forge OBJ/materials. */
public final class SourceCartRenderer<T extends SourceCartEntity> extends EntityRenderer<T> {
    public SourceCartRenderer(EntityRendererProvider.Context context){super(context);}
    @Override public ResourceLocation getTextureLocation(T cart){return SourceCartContent.id("textures/entity/"+switch(cart.getBase()){case PAINTED->"cart_metal";case WOOD->"cart_wood";default->"cart_metal_naked";}+".png");}
    private void mesh(PoseStack poses,MultiBufferSource buffers,int light,String model,ResourceLocation texture,String... parts){
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),SourceCartContent.id("models/vehicles/"+model+".obj"));var consumer=buffers.getBuffer(RenderType.entityCutout(texture));
        if(parts.length==0)LegacyObjRenderer.renderAll(mesh,poses,consumer,light,OverlayTexture.NO_OVERLAY,0xffffffff,LegacyObjRenderer.Convention.FORGE);
        else LegacyObjRenderer.renderOnly(mesh,poses,consumer,light,OverlayTexture.NO_OVERLAY,0xffffffff,LegacyObjRenderer.Convention.FORGE,parts);
    }
    @Override public void render(T cart,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        poses.pushPose();long seed=(long)cart.getId()*493286711L;seed=seed*seed*4392167121L+seed*98761L;
        float jx=(((float)(seed>>16&7L)+.5F)/8F-.5F)*.004F,jy=(((float)(seed>>20&7L)+.5F)/8F-.5F)*.004F,jz=(((float)(seed>>24&7L)+.5F)/8F-.5F)*.004F;poses.translate(jx,jy,jz);
        double x=Mth.lerp((double)partial,cart.xOld,cart.getX()),y=Mth.lerp((double)partial,cart.yOld,cart.getY()),z=Mth.lerp((double)partial,cart.zOld,cart.getZ());
        var center=cart.getPos(x,y,z);float pitch=cart.xRotO+(cart.getXRot()-cart.xRotO)*partial;
        if(center!=null){var front=cart.getPosOffs(x,y,z,.3D);var back=cart.getPosOffs(x,y,z,-.3D);if(front==null)front=center;if(back==null)back=center;poses.translate(center.x-x,(front.y+back.y)/2D-y,center.z-z);
            double dx=back.x-front.x,dy=back.y-front.y,dz=back.z-front.z,length=(float)Math.sqrt(dx*dx+dy*dy+dz*dz);
            if(length!=0){if(length<1E-4D){dx=0;dy=0;dz=0;}else{dx/=length;dy/=length;dz/=length;}yaw=(float)(Math.atan2(dz,dx)*180D/Math.PI);pitch=(float)(Math.atan(dy)*73D);}
        }
        poses.mulPose(Axis.YP.rotationDegrees(180F-yaw));poses.mulPose(Axis.ZP.rotationDegrees(-pitch));poses.translate(0,-.4375F,0);poses.mulPose(Axis.YP.rotationDegrees(90));
        var texture=getTextureLocation(cart);mesh(poses,buffers,light,"cart",texture,"Carriage");
        float roll=cart.getHurtTime()-partial,damage=Math.max(0,cart.getDamage()-partial);
        if(roll>0){poses.translate(0,.75F,0);poses.mulPose(Axis.ZP.rotationDegrees(Mth.sin(roll)*roll*damage/10F*cart.getHurtDir()));poses.translate(0,-.75F,0);}
        mesh(poses,buffers,light,"cart",texture,"Bucket");
        switch(cart.kind){
            case DESTROYER->mesh(poses,buffers,light,"cart_destroyer",SourceCartContent.id("textures/entity/cart_destroyer.png"));
            case POWDER->mesh(poses,buffers,light,"cart_powder",SourceCartContent.id("textures/block/block_gunpowder.png"),"Powder");
            case SEMTEX->{mesh(poses,buffers,light,"cart_powder",SourceCartContent.id("textures/block/semtex_bottom.png"),"SemtexTop");mesh(poses,buffers,light,"cart_powder",SourceCartContent.id("textures/block/semtex_side.png"),"SemtexSide");}
            default->{}
        }
        poses.popPose();super.render(cart,yaw,partial,poses,buffers,light);
    }
}
