package com.hbm.client.satellite.resources;

import com.hbm.entity.mob.fbi.EntityFBI;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public final class FbiRenderer extends HumanoidMobRenderer<EntityFBI,FbiModel> {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/fbi.png");
    public FbiRenderer(EntityRendererProvider.Context context){
        super(context,new FbiModel(),.5F);
        addLayer(new HumanoidArmorLayer<>(this,armor(.5F),armor(1),context.getModelManager()));
    }
    private static HumanoidModel<EntityFBI> armor(float inflation){return new HumanoidModel<>(LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(inflation),0),64,32).bakeRoot());}
    @Override public ResourceLocation getTextureLocation(EntityFBI mob){return TEXTURE;}
    @Override protected void scale(EntityFBI mob,PoseStack poses,float partial){
        // Original RendererLivingEntity translates -24*.0625-.0078125;
        // modern LivingEntityRenderer applies -1.501F immediately after scale().
        poses.translate(0,-1.5078125D-(double)-1.501F,0);
    }
}
