package com.hbm.client.entity.radbeast;

import com.hbm.client.reactor.dfc.components.DfcComponentBeams;
import com.hbm.entity.mob.radbeast.EntityRADBeast;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

/** Source body + actual M65Blaze pass + exact BeamPronter RANDOM/SOLID through the shared173 material/168 source pipeline. */
public final class RadiationBeastRenderer extends MobRenderer<EntityRADBeast,RadiationBeastModel> {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/radbeast.png"),MASK=ResourceLocation.parse("hbm:textures/models/ModelM65Blaze.png".toLowerCase(java.util.Locale.ROOT));
    public RadiationBeastRenderer(EntityRendererProvider.Context context){
        super(context,new RadiationBeastModel(),.5F);
        addLayer(new RenderLayer<EntityRADBeast,RadiationBeastModel>(this){
            private final ModelM65Blaze mask=new ModelM65Blaze();
            @Override public void render(PoseStack poses,MultiBufferSource buffers,int light,EntityRADBeast entity,float limb,float amount,float partial,float age,float yaw,float pitch){
                if(entity.isInvisible())return;mask.isChild=getParentModel().young;mask.isRiding=getParentModel().riding;mask.onGround=getParentModel().attackTime;
                mask.renderToBuffer(poses,buffers.getBuffer(RenderType.entityCutoutNoCull(MASK)),LightTexture.FULL_BRIGHT,LivingEntityRenderer.getOverlayCoords(entity,0),-1,entity,limb,amount,age,yaw,pitch,1F/16F);
            }
        });
    }
    @Override public ResourceLocation getTextureLocation(EntityRADBeast entity){return TEXTURE;}
    @Override protected int getBlockLightLevel(EntityRADBeast entity,BlockPos pos){return 15;}
    @Override protected int getSkyLightLevel(EntityRADBeast entity,BlockPos pos){return 15;}
    @Override public void render(EntityRADBeast entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
        var victim=entity.getUnfortunateSoul();
        if(victim!=null&&entity.getY()>.1D){
            double x=victim.getX()-entity.getX(),y=victim.getY()+victim.getBbHeight()/2D-(entity.getY()+1.25D),z=victim.getZ()-entity.getZ();if(victim==Minecraft.getInstance().player)y-=1.5D;
            double length=Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2));
            if(length<200){poses.pushPose();try{poses.translate(0,1.25D,0);DfcComponentBeams.draw(poses,buffers,new Vec3(x,y,z),true,true,0x004000,0x004000,(int)(entity.level().getGameTime()%1000+1),(int)(length*5),.125F,2,.03125F);}finally{poses.popPose();}}
        }
        super.render(entity,yaw,partial,poses,buffers,LightTexture.FULL_BRIGHT);
    }
    @Override protected void scale(EntityRADBeast entity,PoseStack poses,float partial){poses.translate(0,-1.5078125D-(double)-1.501F,0);}
}
