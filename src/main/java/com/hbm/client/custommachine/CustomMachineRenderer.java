package com.hbm.client.custommachine;
import com.hbm.machine.custommachine.*;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
/** Exact SmallBlockPronter geometry, directional icons/UV and its single UP normal on all six faces. */
public final class CustomMachineRenderer implements BlockEntityRenderer<CustomMachineEntity> {
 public CustomMachineRenderer(BlockEntityRendererProvider.Context context){}
 private static TextureAtlasSprite icon(BlockState state,Direction face){var model=Minecraft.getInstance().getBlockRenderer().getBlockModel(state);var quads=model.getQuads(state,face,RandomSource.create(0),ModelData.EMPTY,null);return quads.isEmpty()?model.getParticleIcon(ModelData.EMPTY):quads.getFirst().getSprite();}
 @Override public void render(CustomMachineEntity m,float partial,PoseStack pose,MultiBufferSource buffers,int light,int overlay){if(m.config==null||m.structureOK)return;var out=buffers.getBuffer(CustomMachineGhostTypes.GHOST);for(var component:m.config.components){var pos=component.at(m.getBlockPos(),m.getBlockState().getValue(CMComponentBlock.META));if(pos.equals(m.getBlockPos()))continue;int meta=component.metas.get((int)(System.currentTimeMillis()/1000%component.metas.size())).getAsInt();var state=component.state(meta);pose.pushPose();pose.translate(pos.getX()-m.getBlockPos().getX(),pos.getY()-m.getBlockPos().getY(),pos.getZ()-m.getBlockPos().getZ());float a=11/32F,b=1-a;face(out,pose,light,overlay,icon(state,Direction.SOUTH),new float[][]{{b,b,b},{a,b,b},{a,a,b},{b,a,b}});face(out,pose,light,overlay,icon(state,Direction.EAST),new float[][]{{b,b,a},{b,b,b},{b,a,b},{b,a,a}});face(out,pose,light,overlay,icon(state,Direction.NORTH),new float[][]{{a,b,a},{b,b,a},{b,a,a},{a,a,a}});face(out,pose,light,overlay,icon(state,Direction.WEST),new float[][]{{a,b,b},{a,b,a},{a,a,a},{a,a,b}});face(out,pose,light,overlay,icon(state,Direction.UP),new float[][]{{b,b,b},{b,b,a},{a,b,a},{a,b,b}});face(out,pose,light,overlay,icon(state,Direction.DOWN),new float[][]{{a,a,b},{a,a,a},{b,a,a},{b,a,b}});pose.popPose();}}
 @Override public net.minecraft.world.phys.AABB getRenderBoundingBox(CustomMachineEntity m){var box=new net.minecraft.world.phys.AABB(m.getBlockPos());if(m.config!=null)for(var component:m.config.components)box=box.minmax(new net.minecraft.world.phys.AABB(component.at(m.getBlockPos(),m.getBlockState().getValue(CMComponentBlock.META))));return box;}
 private static void face(VertexConsumer out,PoseStack pose,int light,int overlay,TextureAtlasSprite sprite,float[][] points){for(int i=0;i<4;i++){float[] p=points[i];out.addVertex(pose.last().pose(),p[0],p[1],p[2]).setColor(255,255,255,191).setUv(i==0||i==3?sprite.getU1():sprite.getU0(),i<2?sprite.getV0():sprite.getV1()).setOverlay(overlay).setLight(light).setNormal(pose.last(),0,1,0);}}
}
