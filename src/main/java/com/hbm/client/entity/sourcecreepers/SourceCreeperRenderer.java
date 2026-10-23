package com.hbm.client.entity.sourcecreepers;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;
/** Exact RenderCreeperUniversal scale (including nuclear fourth-power swell times5) over unchanged vanilla Creeper box/UV geometry. */
public final class SourceCreeperRenderer extends CreeperRenderer {
 private final ResourceLocation texture;private final float swellModifier;
 public SourceCreeperRenderer(EntityRendererProvider.Context context,String texture,String armor,float swellModifier){super(context);this.texture=ResourceLocation.parse("hbm:textures/entity/"+texture);this.swellModifier=swellModifier;layers.removeIf(layer->layer instanceof CreeperPowerLayer);addLayer(new SourceCreeperPowerLayer(this,context.getModelSet(),ResourceLocation.parse("hbm:textures/entity/"+armor)));}
 @Override public ResourceLocation getTextureLocation(Creeper entity){return texture;}
 /** Original1.7 RenderCreeper white-flash alpha, not modern clamp(swell,.5,1). */
 @Override protected float getWhiteOverlayProgress(Creeper entity,float partial){float swell=entity.getSwelling(partial);if((int)(swell*10)%2==0)return 0;return Mth.clamp((int)(swell*.2F*255F),0,255)/255F;}
 @Override protected void scale(Creeper entity,PoseStack poses,float partial){float swell=entity.getSwelling(partial),flash=1+Mth.sin(swell*100)*swell*.01F;swell=Mth.clamp(swell,0,1);swell*=swell;swell*=swell;swell*=swellModifier;float horizontal=(1+swell*.4F)*flash,vertical=(1+swell*.1F)/flash;poses.scale(horizontal,vertical,horizontal);}
}
