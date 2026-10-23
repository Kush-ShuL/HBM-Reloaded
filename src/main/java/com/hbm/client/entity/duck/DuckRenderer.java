package com.hbm.client.entity.duck;

import com.hbm.entity.mob.duck.Quackos;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Chicken;

/** The actual registered RenderDuck/RenderQuacc both use the unchanged vanilla ModelChicken. */
public final class DuckRenderer extends ChickenRenderer {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/duck.png");
    public DuckRenderer(EntityRendererProvider.Context context,boolean boss){super(context);shadowRadius=boss?7.5F:.3F;}
    @Override public ResourceLocation getTextureLocation(Chicken entity){return TEXTURE;}
    @Override protected void scale(Chicken entity,PoseStack pose,float partial){if(entity instanceof Quackos)pose.scale(25,25,25);}
}
