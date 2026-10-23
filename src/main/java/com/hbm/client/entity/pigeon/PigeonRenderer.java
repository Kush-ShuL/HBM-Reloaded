package com.hbm.client.entity.pigeon;

import com.hbm.entity.mob.pigeon.EntityPigeon;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public final class PigeonRenderer extends MobRenderer<EntityPigeon,PigeonModel> {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/pigeon.png");
    public PigeonRenderer(EntityRendererProvider.Context context){super(context,new PigeonModel(),.3F);}
    @Override public ResourceLocation getTextureLocation(EntityPigeon pigeon){return TEXTURE;}
    @Override protected float getBob(EntityPigeon pigeon,float partial){float time=pigeon.prevFallTime+(pigeon.fallTime-pigeon.prevFallTime)*partial,amount=pigeon.prevDest+(pigeon.dest-pigeon.prevDest)*partial;return(Mth.sin(time)+1F)*amount;}
    @Override protected void scale(EntityPigeon pigeon,PoseStack poses,float partial){poses.translate(0,-1.5078125D-(double)-1.501F,0);}
}
