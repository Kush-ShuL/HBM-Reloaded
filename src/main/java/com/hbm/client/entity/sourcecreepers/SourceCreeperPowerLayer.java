package com.hbm.client.entity.sourcecreepers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Creeper;
/** Source powered overlay uses unlit additive .5gray, original scroll in both axes and depth writes only while visible. */
final class SourceCreeperPowerLayer extends RenderLayer<Creeper,CreeperModel<Creeper>> {
 private final CreeperModel<Creeper> model;private final ResourceLocation texture;
 SourceCreeperPowerLayer(RenderLayerParent<Creeper,CreeperModel<Creeper>> parent,EntityModelSet models,ResourceLocation texture){super(parent);model=new CreeperModel<>(models.bakeLayer(ModelLayers.CREEPER_ARMOR));this.texture=texture;}
 @Override public void render(PoseStack poses,MultiBufferSource buffers,int light,Creeper entity,float limbSwing,float limbAmount,float partial,float age,float headYaw,float headPitch){if(!entity.isPowered())return;model.prepareMobModel(entity,limbSwing,limbAmount,partial);getParentModel().copyPropertiesTo(model);model.setupAnim(entity,limbSwing,limbAmount,age,headYaw,headPitch);float offset=(entity.tickCount+partial)*.01F;var out=buffers.getBuffer(SourceCreeperRenderTypes.power(texture,offset,entity.isInvisible()));model.renderToBuffer(poses,out,light,OverlayTexture.NO_OVERLAY,0xff808080);}
}
