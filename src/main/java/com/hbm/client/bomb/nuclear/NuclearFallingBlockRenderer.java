package com.hbm.client.bomb.nuclear;

import com.hbm.bomb.nuclear.NuclearFallingBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RenderShape;
import net.neoforged.neoforge.client.RenderTypeHelper;
import net.neoforged.neoforge.client.model.data.ModelData;

/** The original falling-block render at logical center, with no rendered duplicate source block. */
public final class NuclearFallingBlockRenderer extends EntityRenderer<NuclearFallingBlock> {
 private final BlockRenderDispatcher dispatcher;
 public NuclearFallingBlockRenderer(EntityRendererProvider.Context context){super(context);dispatcher=context.getBlockRenderDispatcher();shadowRadius=.5F;}
 @Override public void render(NuclearFallingBlock entity,float yaw,float partial,PoseStack poses,MultiBufferSource buffers,int light){
  var state=entity.carried();var pos=BlockPos.containing(entity.getX(),entity.legacyY(),entity.getZ());
  if(state.getRenderShape()==RenderShape.MODEL&&!entity.level().getBlockState(pos).is(state.getBlock())){
   poses.pushPose();poses.translate(-.5,entity.legacyY()-entity.getY()-.5,-.5);var model=dispatcher.getBlockModel(state);var data=model.getModelData(entity.level(),pos,state,ModelData.EMPTY);for(var type:model.getRenderTypes(state,RandomSource.create(0),data))dispatcher.getModelRenderer().tesselateBlock(entity.level(),model,state,pos,poses,buffers.getBuffer(RenderTypeHelper.getMovingBlockRenderType(type)),false,RandomSource.create(),state.getSeed(pos),OverlayTexture.NO_OVERLAY,data,type);poses.popPose();
  }
  super.render(entity,yaw,partial,poses,buffers,light);
 }
 @Override public ResourceLocation getTextureLocation(NuclearFallingBlock entity){return TextureAtlas.LOCATION_BLOCKS;}
}
