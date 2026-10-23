package com.hbm.client.entity.glyphid;

import com.hbm.client.model.*;
import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/** Source RenderGlyphid.ModelGlyphid, with every original pivot/part/walk phase retained. */
public class GlyphidModel<T extends EntityGlyphid> extends EntityModel<T> {
    public static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/mobs/glyphid.obj");
    protected T entity;
    protected double bite;
    protected float limbSwing;
    private MultiBufferSource buffers;
    public GlyphidModel(){super(RenderType::entityCutoutNoCull);}
    @Override public void prepareMobModel(T entity,float limb,float amount,float partial){super.prepareMobModel(entity,limb,amount,partial);bite=entity.getAttackAnim(partial);}
    @Override public void setupAnim(T entity,float limb,float amount,float age,float yaw,float pitch){this.entity=entity;limbSwing=limb;}
    void beginRender(MultiBufferSource source){buffers=source;}
    void endRender(){buffers=null;}
    protected final MultiBufferSource renderBuffers(){return java.util.Objects.requireNonNull(buffers,"Source Glyphid render scope");}
    protected static void part(String name,PoseStack poses,VertexConsumer output,int light,int overlay,int color){var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);LegacyObjRenderer.renderOnly(mesh,poses,output,light,overlay,color,LegacyObjRenderer.Convention.HFR,name);}
    @Override public void renderToBuffer(PoseStack p,VertexConsumer out,int light,int overlay,int color){
        p.pushPose();
        // Native LivingEntityRenderer uses -1.501; original RenderLiving used -1.5078125.
        p.translate(0,-.0068125,0);
        p.mulPose(Axis.XP.rotationDegrees(180));
        p.translate(0,-1.5F,0);
        renderSourceBody(p,out,light,overlay,color);
        p.popPose();
    }
    protected void renderSourceBody(PoseStack p,VertexConsumer out,int light,int overlay,int color){
p.pushPose();
			
			double s = entity.sourceScale();
			p.scale((float)s, (float)s, (float)s);
			
			
			byte armor = entity.armorBits();
			
			double walkCycle = limbSwing;

			double cy0 = Math.sin(walkCycle % (Math.PI * 2));
			double cy1 = Math.sin(walkCycle % (Math.PI * 2) - Math.PI * 0.5);
			double cy2 = Math.sin(walkCycle % (Math.PI * 2) - Math.PI);
			double cy3 = Math.sin(walkCycle % (Math.PI * 2) - Math.PI * 0.75);

			double bite = Mth.clamp(Math.sin(this.bite * Math.PI * 2 - Math.PI * 0.5), 0, 1) * 20;
			double headTilt = Math.sin(this.bite * Math.PI) * 30;
			
			part("Body",p,out,light,overlay,color);
			if((armor & (1 << 0)) > 0) part("ArmorFront",p,out,light,overlay,color);
			if((armor & (1 << 1)) > 0) part("ArmorLeft",p,out,light,overlay,color);
			if((armor & (1 << 2)) > 0) part("ArmorRight",p,out,light,overlay,color);

			                
			p.pushPose();
			p.translate(0.25, 0.625, 0.0625);
			p.mulPose(Axis.YP.rotationDegrees((float)(10)));
			p.mulPose(Axis.XP.rotationDegrees((float)(35 + cy1 * 20)));
			p.translate(-0.25, -0.625, -0.0625);
			part("ArmLeftUpper",p,out,light,overlay,color);
			p.translate(0.25, 0.625, 0.4375);
			p.mulPose(Axis.XP.rotationDegrees((float)(-75 - cy1 * 20 + cy0 * 20)));
			p.translate(-0.25, -0.625, -0.4375);
			part("ArmLeftMid",p,out,light,overlay,color);
			p.translate(0.25, 0.625, 0.9375);
			p.mulPose(Axis.XP.rotationDegrees((float)(90 - cy0 * 45)));
			p.translate(-0.25, -0.625, -0.9375);
			part("ArmLeftLower",p,out,light,overlay,color);
			if((armor & (1 << 3)) > 0) part("ArmLeftArmor",p,out,light,overlay,color);
			p.popPose();

			                 
			p.pushPose();
			p.translate(-0.25, 0.625, 0.0625);
			p.mulPose(Axis.YP.rotationDegrees((float)(-10)));
			p.mulPose(Axis.XP.rotationDegrees((float)(35 + cy2 * 20)));
			p.translate(0.25, -0.625, -0.0625);
			part("ArmRightUpper",p,out,light,overlay,color);
			p.translate(-0.25, 0.625, 0.4375);
			p.mulPose(Axis.XP.rotationDegrees((float)(-75 - cy2 * 20 + cy3 * 20)));
			p.translate(0.25, -0.625, -0.4375);
			part("ArmRightMid",p,out,light,overlay,color);
			p.translate(-0.25, 0.625, 0.9375);
			p.mulPose(Axis.XP.rotationDegrees((float)(90 - cy3 * 45)));
			p.translate(0.25, -0.625, -0.9375);
			part("ArmRightLower",p,out,light,overlay,color);
			if((armor & (1 << 4)) > 0) part("ArmRightArmor",p,out,light,overlay,color);
			p.popPose();

			p.pushPose();
			
			p.translate(0, 0.5, 0.25);
			p.mulPose(Axis.ZP.rotationDegrees((float)(headTilt)));
			p.translate(0, -0.5, -0.25);
			
			p.pushPose();
			p.translate(0, 0.5, 0.25);
			p.mulPose(Axis.XP.rotationDegrees((float)(-bite)));
			p.translate(0, -0.5, -0.25);
			part("JawTop",p,out,light,overlay,color);
			p.popPose();

			p.pushPose();
			p.translate(0, 0.5, 0.25);
			p.mulPose(Axis.YP.rotationDegrees((float)(bite)));
			p.mulPose(Axis.XP.rotationDegrees((float)(bite)));
			p.translate(0, -0.5, -0.25);
			part("JawLeft",p,out,light,overlay,color);
			p.popPose();

			p.pushPose();
			p.translate(0, 0.5, 0.25);
			p.mulPose(Axis.YP.rotationDegrees((float)(-bite)));
			p.mulPose(Axis.XP.rotationDegrees((float)(bite)));
			p.translate(0, -0.5, -0.25);
			part("JawRight",p,out,light,overlay,color);
			p.popPose();
			p.popPose();

			double steppy = 15;
			double bend = 60;
			
			for(int i = 0; i < 3; i++) {
				
				double c0 = cy0 * (i == 1 ? -1 : 1);
				double c1 = cy1 * (i == 1 ? -1 : 1);
				
				p.pushPose();
				p.translate(0, 0.25, 0);
				p.mulPose(Axis.YP.rotationDegrees((float)(i * 30 - 15 + c0 * 7.5)));
				p.mulPose(Axis.ZP.rotationDegrees((float)(steppy + c1 * steppy)));
				p.translate(0, -0.25, 0);
				part("LegLeftUpper",p,out,light,overlay,color);
				p.translate(0.5625, 0.25, 0);
				p.mulPose(Axis.ZP.rotationDegrees((float)(-bend - c1 * steppy)));
				p.translate(-0.5625, -0.25, 0);
				part("LegLeftLower",p,out,light,overlay,color);
				p.popPose();
				
				p.pushPose();
				p.translate(0, 0.25, 0);
				p.mulPose(Axis.YP.rotationDegrees((float)(i * 30 - 45 + c0 * 7.5)));
				p.mulPose(Axis.ZP.rotationDegrees((float)(-steppy + c1 * steppy)));
				p.translate(0, -0.25, 0);
				part("LegRightUpper",p,out,light,overlay,color);
				p.translate(-0.5625, 0.25, 0);
				p.mulPose(Axis.ZP.rotationDegrees((float)(bend - c1 * steppy)));
				p.translate(0.5625, -0.25, 0);
				part("LegRightLower",p,out,light,overlay,color);
				p.popPose();
			}
			
			renderExtra(entity,p,renderBuffers(),out,light,overlay,color);
			p.popPose();
    }
    /** Nuclear's original attached MiniNuke uses a second texture, inside this exact source scale scope. */
    protected void renderExtra(T entity,PoseStack p,MultiBufferSource buffers,VertexConsumer output,int light,int overlay,int color){}
}
