package com.hbm.client.entity.dummy;

import com.hbm.entity.mob.dummy.EntityDummy;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public final class TestDummyRenderer extends HumanoidMobRenderer<EntityDummy,TestDummyModel> {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/dummy.png");
    public TestDummyRenderer(EntityRendererProvider.Context context){super(context,new TestDummyModel(),.5F);addLayer(new HumanoidArmorLayer<>(this,armor(.5F),armor(1F),context.getModelManager()));}
    private static HumanoidModel<EntityDummy> armor(float inflation){return new HumanoidModel<>(LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(inflation),0),64,32).bakeRoot());}
    @Override public ResourceLocation getTextureLocation(EntityDummy mob){return TEXTURE;}
    @Override protected boolean shouldShowName(EntityDummy mob){return true;}
    @Override protected void scale(EntityDummy mob,PoseStack poses,float partial){poses.translate(0,-1.5078125D-(double)-1.501F,0);}
}
