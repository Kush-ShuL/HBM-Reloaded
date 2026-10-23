package com.hbm.client.entity.glyphid;

import com.hbm.entity.mob.glyphid.ParasiteMaggot;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceLocation;

/** Real source RenderMaggot: original dedicated texture, shadow.3 and180-degree death roll. */
public final class MaggotRenderer extends MobRenderer<ParasiteMaggot,MaggotModel> {
    private static final ResourceLocation TEXTURE=ResourceLocation.parse("hbm:textures/entity/parasite_maggot.png");
    public MaggotRenderer(EntityRendererProvider.Context context){super(context,new MaggotModel(),.3F);}
    @Override protected float getFlipDegrees(ParasiteMaggot entity){return 180F;}
    @Override public ResourceLocation getTextureLocation(ParasiteMaggot entity){return TEXTURE;}
}
