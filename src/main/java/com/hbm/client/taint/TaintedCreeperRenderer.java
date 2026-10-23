package com.hbm.client.taint;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Creeper;
/** Original universal creeper scale and tainted power-overlay pixels, reusing the identical native creeper geometry. */
public final class TaintedCreeperRenderer extends CreeperRenderer {
 public TaintedCreeperRenderer(EntityRendererProvider.Context context){super(context);layers.removeIf(layer->layer instanceof CreeperPowerLayer);addLayer(new CreeperPowerLayer(this,context.getModelSet()){@Override protected ResourceLocation getTextureLocation(){return ResourceLocation.parse("hbm:textures/entity/creeper_armor_taint.png");}});}
 @Override public ResourceLocation getTextureLocation(Creeper entity){return ResourceLocation.parse("hbm:textures/entity/creeper_tainted.png");}
 @Override protected void scale(Creeper entity,PoseStack pose,float partial){float swell=entity.getSwelling(partial),flash=1+Mth.sin(swell*100)*swell*.01F;swell=Mth.clamp(swell,0,1);swell*=swell;swell*=swell;float horizontal=(1+swell*.4F)*flash,vertical=(1+swell*.1F)/flash;pose.scale(horizontal,vertical,horizontal);}
}
