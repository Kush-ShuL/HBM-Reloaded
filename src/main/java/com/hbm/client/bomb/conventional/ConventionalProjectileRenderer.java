package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.*;
import com.hbm.client.model.legacy.ModelBase;
import com.hbm.client.model.legacy.generated.ModelRubble;
import com.hbm.client.model.legacy.generated.ModelShrapnel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import org.joml.Quaternionf;

/** Original generated Java models, diagonal-axis spins, and the actual rubble block's bottom texture. */
public final class ConventionalProjectileRenderer<T extends Entity> extends EntityRenderer<T> {
    private final boolean rubble;private final ModelBase model;
    public ConventionalProjectileRenderer(EntityRendererProvider.Context context,boolean rubble){super(context);this.rubble=rubble;model=rubble?new ModelRubble():new ModelShrapnel();shadowRadius=0;}
    @Override public ResourceLocation getTextureLocation(T entity){if(!rubble)return ResourceLocation.parse("hbm:textures/entity/shrapnel.png");var state=((RubbleProjectile)entity).material();var baked=Minecraft.getInstance().getBlockRenderer().getBlockModel(state);var quads=baked.getQuads(state,Direction.DOWN,RandomSource.create(0));var sprite=quads.isEmpty()?baked.getParticleIcon():quads.getFirst().getSprite();var id=sprite.contents().name();return ResourceLocation.fromNamespaceAndPath(id.getNamespace(),"textures/"+id.getPath()+".png");}
    @Override public void render(T entity,float yaw,float partial,PoseStack pose,MultiBufferSource buffers,int light){pose.pushPose();pose.mulPose(Axis.XP.rotationDegrees(180));float angle=rubble?((entity.tickCount+partial)%360)*10:(entity.tickCount%360)*10+partial;pose.mulPose(new Quaternionf().rotationAxis((float)Math.toRadians(angle),.57735026F,.57735026F,.57735026F));if(entity instanceof ShrapnelProjectile shrapnel&&shrapnel.style()>=2)pose.scale(3,3,3);model.renderToBuffer(pose,buffers.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity))),light,OverlayTexture.NO_OVERLAY,-1,.0625F);pose.popPose();}
}
