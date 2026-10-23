package com.hbm.client.entity.glyphid;
import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/** Original shadowOpaque0 and single infected blend/no-alpha-test pass. */
public class GlyphidRenderer<T extends EntityGlyphid> extends MobRenderer<T,GlyphidModel<T>> {
    public static final ResourceLocation INFESTED=ResourceLocation.parse("hbm:textures/entity/glyphid_infestation.png");
    public GlyphidRenderer(EntityRendererProvider.Context context){this(context,new GlyphidModel<>(),true);}
    public GlyphidRenderer(EntityRendererProvider.Context context,GlyphidModel<T> model){this(context,model,true);}
    public GlyphidRenderer(EntityRendererProvider.Context context,GlyphidModel<T> model,boolean sourceInfestationPass){
        super(context,model,1);shadowStrength=0;
        if(sourceInfestationPass)addLayer(new RenderLayer<T,GlyphidModel<T>>(this){
            @Override public void render(PoseStack poses,MultiBufferSource buffers,int light,T entity,float limb,float amount,float partial,float age,float yaw,float pitch){
                if(entity.subtype()!=EntityGlyphid.TYPE_INFECTED||entity.isInvisible())return;
                // Original pass disables alpha testing entirely, including zero-alpha depth writes.
                var output=buffers.getBuffer(GlyphidInfestationRenderType.TYPE);
                getParentModel().renderToBuffer(poses,output,light,OverlayTexture.NO_OVERLAY,-1);
            }
        });
    }
    @Override public ResourceLocation getTextureLocation(T entity){return entity.getSkin();}
    @Override public void render(T entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){model.beginRender(buffers);try{super.render(entity,yaw,partial,poses,buffers,light);}finally{model.endRender();}}
}
