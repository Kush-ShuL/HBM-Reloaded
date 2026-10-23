package com.hbm.client.entity.ghost;

import com.hbm.entity.mob.ghost.EntityGhost;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public final class GhostRenderer extends HumanoidMobRenderer<EntityGhost,GhostModel> {
    static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/ghost.png");
    public GhostRenderer(EntityRendererProvider.Context context){super(context,new GhostModel(),.5F);addLayer(new HumanoidArmorLayer<>(this,armor(.5F),armor(1),context.getModelManager()));}
    private static HumanoidModel<EntityGhost> armor(float scale){return new HumanoidModel<>(LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(scale),0),64,32).bakeRoot());}
    @Override public ResourceLocation getTextureLocation(EntityGhost ghost){return TEXTURE;}
    @Override protected void scale(EntityGhost ghost,PoseStack poses,float partial){poses.translate(0,-1.5078125D-(double)-1.501F,0);}
}
