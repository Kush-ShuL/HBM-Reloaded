package com.hbm.client.railway.cart;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.railway.cart.SourceMinecartTest;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.MinecartModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.RenderShape;

/** Exact active RenderMinecartTest: original LilBoy payload and old six-part vanilla cart. */
public final class SourceMinecartTestRenderer extends EntityRenderer<SourceMinecartTest> {
    public static final ModelLayerLocation LAYER=new ModelLayerLocation(ResourceLocation.parse("hbm:source_minecart_test"),"main");
    private static final ResourceLocation CART=ResourceLocation.withDefaultNamespace("textures/entity/minecart.png");
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/lilboy1.obj");
    // X5846 itself omits LilBoy2.png. Preserve its reference and native missing-texture fallback.
    private static final ResourceLocation PAYLOAD=ResourceLocation.parse("hbm:textures/models/lilboy2.png");
    private final MinecartModel<SourceMinecartTest> model;
    public SourceMinecartTestRenderer(EntityRendererProvider.Context context){super(context);model=new MinecartModel<>(context.bakeLayer(LAYER));shadowRadius=.7F;}
    @Override public ResourceLocation getTextureLocation(SourceMinecartTest cart){return CART;}

    public static LayerDefinition layer(){
        // Actual 1.7.10 bhq/ModelMinecart: five shell pieces plus the missing modern inner floor.
        var mesh=new MeshDefinition();var root=mesh.getRoot();
        root.addOrReplaceChild("bottom",CubeListBuilder.create().texOffs(0,10).addBox(-10,-8,-1,20,16,2),PartPose.offsetAndRotation(0,4,0,1.5707964F,0,0));
        root.addOrReplaceChild("front",CubeListBuilder.create().texOffs(0,0).addBox(-8,-9,-1,16,8,2),PartPose.offsetAndRotation(-9,4,0,0,4.712389F,0));
        root.addOrReplaceChild("back",CubeListBuilder.create().texOffs(0,0).addBox(-8,-9,-1,16,8,2),PartPose.offsetAndRotation(9,4,0,0,1.5707964F,0));
        root.addOrReplaceChild("left",CubeListBuilder.create().texOffs(0,0).addBox(-8,-9,-1,16,8,2),PartPose.offsetAndRotation(0,4,-7,0,3.1415927F,0));
        root.addOrReplaceChild("right",CubeListBuilder.create().texOffs(0,0).addBox(-8,-9,-1,16,8,2),PartPose.offset(0,4,7));
        // Source model.render(...,-.1F,...) sets inner floor Y to4-(-.1)=4.1, not native five-part body.
        root.addOrReplaceChild("inner",CubeListBuilder.create().texOffs(44,10).addBox(-9,-7,-1,18,14,1),PartPose.offsetAndRotation(0,4.1F,0,-1.5707964F,0,0));
        return LayerDefinition.create(mesh,64,32);
    }
    @Override public void render(SourceMinecartTest cart,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        poses.pushPose();long seed=(long)cart.getId()*493286711L;seed=seed*seed*4392167121L+seed*98761L;
        float jx=(((float)(seed>>16&7L)+.5F)/8F-.5F)*.004F,jy=(((float)(seed>>20&7L)+.5F)/8F-.5F)*.004F,jz=(((float)(seed>>24&7L)+.5F)/8F-.5F)*.004F;poses.translate(jx,jy,jz);
        double x=Mth.lerp((double)partial,cart.xOld,cart.getX()),y=Mth.lerp((double)partial,cart.yOld,cart.getY()),z=Mth.lerp((double)partial,cart.zOld,cart.getZ());
        var center=cart.getPos(x,y,z);float pitch=cart.xRotO+(cart.getXRot()-cart.xRotO)*partial;
        if(center!=null){
            var front=cart.getPosOffs(x,y,z,.30000001192092896D);var back=cart.getPosOffs(x,y,z,-.30000001192092896D);if(front==null)front=center;if(back==null)back=center;
            poses.translate(center.x-x,(front.y+back.y)/2D-y,center.z-z);
            double dx=back.x-front.x,dy=back.y-front.y,dz=back.z-front.z,length=(float)Math.sqrt(dx*dx+dy*dy+dz*dz);
            if(length!=0){if(length<1E-4D){dx=0;dy=0;dz=0;}else{dx/=length;dy/=length;dz/=length;}yaw=(float)(Math.atan2(dz,dx)*180D/Math.PI);pitch=(float)(Math.atan(dy)*73D);}
        }
        poses.mulPose(Axis.YP.rotationDegrees(180F-yaw));
        if(cart.xSign()<0||cart.zSign()<0)poses.mulPose(Axis.YP.rotationDegrees(180));
        poses.mulPose(Axis.ZP.rotationDegrees(-pitch));
        float roll=cart.getHurtTime()-partial,damage=Math.max(0,cart.getDamage()-partial);
        if(roll>0)poses.mulPose(Axis.XP.rotationDegrees(Mth.sin(roll)*roll*damage/10F*cart.getHurtDir()));
        if(cart.getDisplayBlockState().getRenderShape()!=RenderShape.INVISIBLE){
            poses.pushPose();poses.scale(.75F,.75F,.75F);poses.translate(0,cart.getDisplayOffset()/16F,0);
            int fuse=cart.getFuse();float time=fuse-partial+1F;
            if(fuse>-1&&time<10F){float swell=Mth.clamp(1F-time/10F,0,1);swell*=swell;swell*=swell;float scale=1F+swell*.3F;poses.scale(scale,scale,scale);}
            var payload=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);
            LegacyObjRenderer.renderAll(payload,poses,buffers.getBuffer(RenderType.entityCutout(PAYLOAD)),light,OverlayTexture.NO_OVERLAY,0xffffffff,LegacyObjRenderer.Convention.FORGE);
            if(fuse>-1&&fuse/5%2==0){int alpha=Mth.clamp((int)((1F-time/100F)*.8F*255F),0,255);LegacyObjRenderer.renderAll(payload,poses,buffers.getBuffer(SourceMinecartTestTypes.FLASH),light,OverlayTexture.NO_OVERLAY,alpha<<24|0xffffff,LegacyObjRenderer.Convention.FORGE);}
            poses.popPose();
        }
        poses.scale(-1,-1,1);model.setupAnim(cart,0,0,-.1F,0,0);model.renderToBuffer(poses,buffers.getBuffer(model.renderType(CART)),light,OverlayTexture.NO_OVERLAY);
        poses.popPose();
        // The source override never invokes super.doRender: no invented name tag pass.
    }
}
