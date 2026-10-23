package com.hbm.client.conveyor;

import com.hbm.conveyor.belt.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;

final class ConveyorBakedModel extends BakedModelWrapper<BakedModel> {
 static final ModelProperty<ConveyorGeometry.Neighbors> NEIGHBORS=new ModelProperty<>();
 private record Key(int meta,ConveyorGeometry.Neighbors neighbors){}
 private final ConveyorBeltBlock.Kind kind;private final boolean wand;private final Function<Material,TextureAtlasSprite> textures;private final Map<String,TextureAtlasSprite> sprites=new HashMap<>();private final Map<Key,List<BakedQuad>> cache=new ConcurrentHashMap<>();private final Map<Integer,ConveyorBakedModel> variants=new HashMap<>();
 ConveyorBakedModel(BakedModel original,ConveyorBeltBlock.Kind kind,boolean wand,Function<Material,TextureAtlasSprite> textures){super(original);this.kind=kind;this.wand=wand;this.textures=textures;for(String name:ConveyorModels.TEXTURES)sprites.put(name,textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,switch(name){case "concrete_smooth"->ConveyorContent.id("block/concrete");case "steel_grate"->ConveyorContent.id("block/grate_top");default->name.contains(":")?ResourceLocation.parse(name).withPrefix("block/"):ConveyorContent.id("block/"+name);})));}
 private TextureAtlasSprite sprite(String key){return sprites.get(key);}
 @Override public boolean useAmbientOcclusion(){return true;}
 @Override public boolean isGui3d(){return kind!=ConveyorBeltBlock.Kind.LIFT;}
 @Override public boolean isCustomRenderer(){return false;}
 @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){
  int hand=context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND?-1:1;
  if(context.firstPerson()){
   // Source ItemRenderBlock accepts EQUIPPED_BLOCK: no ItemRenderBase 1.5/50deg bridge.
   poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand*45));poses.scale(.4F,.4F,.4F);
  }else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){
   // Undo modern ItemInHandLayer and reproduce original RenderPlayer BLOCK_3D caller.
   poses.translate(-hand/16D,-.125,.625);poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-180));poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
   poses.translate(-hand/16D,.4375,.0625);poses.translate(0,.1875,-.3125);poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(20));poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand*45));poses.scale(-.375F,-.375F,.375F);
  }else getTransforms().getTransform(context).apply(left,poses);return this;
 }
 @Override public ItemOverrides getOverrides(){if(!wand)return originalModel.getOverrides();return new ItemOverrides(){@Override public BakedModel resolve(BakedModel original,ItemStack stack,ClientLevel level,LivingEntity entity,int seed){int meta=ConveyorWandItem.type(stack).ordinal();return variants.computeIfAbsent(meta,m->new ConveyorBakedModel(originalModel,ConveyorBeltBlock.Kind.values()[m],false,textures));}};}
 @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
 @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}
 @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
 @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){return data.derive().with(NEIGHBORS,ConveyorGeometry.Neighbors.world(world,pos)).build();}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
 @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){if(side!=null)return List.of();boolean inventory=state==null;int meta=inventory?2:state.getValue(ConveyorBeltBlock.META);var neighbors=data.get(NEIGHBORS);if(neighbors==null)neighbors=ConveyorGeometry.INVENTORY;var key=new Key(inventory?-1:meta,neighbors);return cache.computeIfAbsent(key,k->ConveyorGeometry.build(this::sprite,kind,meta,k.neighbors,inventory));}
}
