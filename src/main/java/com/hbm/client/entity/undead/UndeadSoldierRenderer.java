package com.hbm.client.entity.undead;

import com.hbm.entity.mob.undead.UndeadSoldier;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public final class UndeadSoldierRenderer extends HumanoidMobRenderer<UndeadSoldier,UndeadSoldierModel> {
    private static final ResourceLocation ZOMBIE=ResourceLocation.parse("hbm:textures/entity/undead_soldier_zombie.png"),SKELETON=ResourceLocation.parse("hbm:textures/entity/undead_soldier_skeleton.png");
    public UndeadSoldierRenderer(EntityRendererProvider.Context context){
        super(context,new UndeadSoldierModel(),.5F);
        addLayer(new HumanoidArmorLayer<>(this,armor(.5F),armor(1),context.getModelManager()));
    }
    private static HumanoidModel<UndeadSoldier> armor(float inflation){return new HumanoidModel<>(LayerDefinition.create(HumanoidModel.createMesh(new CubeDeformation(inflation),0),64,32).bakeRoot());}
    @Override public ResourceLocation getTextureLocation(UndeadSoldier mob){return mob.bodyType()==UndeadSoldier.TYPE_SKELETON?SKELETON:ZOMBIE;}
    @Override protected void scale(UndeadSoldier mob,PoseStack poses,float partial){
        // Original RendererLivingEntity translates -24*.0625-.0078125;
        // modern LivingEntityRenderer applies -1.501F immediately after scale().
        poses.translate(0,-1.5078125D-(double)-1.501F,0);
    }
}
