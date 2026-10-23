package com.hbm.client.conveyorsplitter;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;

/** Source RenderSplitter/noSmooth OBJ parts, source ObjUtil.renderPartWithIcon flat shade and both inventory halves. */
final class ConveyorSplitterModel extends BakedModelWrapper<BakedModel> {
    private final List<BakedQuad> quads;private final TextureAtlasSprite particle;
    ConveyorSplitterModel(BakedModel original,int meta,boolean inventory,Function<Material,TextureAtlasSprite> getter){
        super(original);particle=getter.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/crane_side")));var output=new ArrayList<BakedQuad>();var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/blocks/splitter.obj"));
        if(inventory){half(mesh,true,0,true,getter,output);half(mesh,false,0,true,getter,output);}else{float rotation=meta==12||meta==4?90F/180F*(float)Math.PI:meta==13||meta==5?270F/180F*(float)Math.PI:meta==14||meta==3?180F/180F*(float)Math.PI:0;half(mesh,meta>=12,rotation,false,getter,output);}quads=List.copyOf(output);
    }
    private static void half(WavefrontMesh mesh,boolean left,float rotation,boolean inventory,Function<Material,TextureAtlasSprite> getter,List<BakedQuad> out){
        var parts=new LinkedHashMap<String,String>();parts.put("Top",left?"top_left":"top_right");parts.put("Bottom",left?"top_right":"top_left");parts.put(left?"Left":"Right",left?"left":"right");parts.put("Back",left?"back_left":"back_right");parts.put("Front",left?"front_left":"front_right");parts.put("Inner","inner");parts.put("InnerLeft","inner_side");parts.put("InnerRight","inner_side");parts.put("InnerTop","inner_side");parts.put("InnerBottom","belt");
        for(var part:parts.entrySet()){var sprite=getter.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/crane_splitter_"+part.getValue())));for(var face:mesh.faces()){
            if(!WavefrontMesh.legacyPart(part.getKey()).test(face))continue;if(face.corners().size()!=3&&face.corners().size()!=4)throw new IllegalStateException("Source splitter ObjUtil expects triangle/quad at "+face.sourceLine());var n=face.normal();var normal=LegacyVector.createVectorHelper(n.x(),n.y(),n.z());normal.rotateAroundY(rotation);float brightness=inventory?1:Math.max(.45F,(float)normal.yCoord*.3F+.7F-(float)Math.abs(normal.xCoord)*.1F+(float)Math.abs(normal.zCoord)*.1F);if(inventory)normal.rotateAroundY(-.5F*(float)Math.PI);float nx=(float)normal.xCoord,ny=(float)normal.yCoord,nz=(float)normal.zCoord;int gray=Math.clamp((int)(brightness*255),0,255),color=0xff000000|gray<<16|gray<<8|gray,packed=((byte)(nx*127F)&255)|((byte)(ny*127F)&255)<<8|((byte)(nz*127F)&255)<<16;int[] data=new int[32];
            for(int i=0;i<4;i++){var corner=face.corners().get(Math.min(i,face.corners().size()-1));var p=mesh.positions().get(corner.position());var uv=mesh.textureCoordinates().get(corner.texture());var point=LegacyVector.createVectorHelper(p.x(),p.y(),p.z());point.rotateAroundY(rotation);float x=(float)point.xCoord,y=(float)point.yCoord,z=(float)point.zCoord;
                if(inventory){point=LegacyVector.createVectorHelper(x,y-.5F,z+(left?.5F:-.5F));point.rotateAroundY(-.5F*(float)Math.PI);x=(float)(point.xCoord*.625)+.5F;y=(float)(point.yCoord*.625)+.5F;z=(float)(point.zCoord*.625)+.5F;}else{x+=.5F;z+=.5F;}int at=i*8;data[at]=Float.floatToRawIntBits(x);data[at+1]=Float.floatToRawIntBits(y);data[at+2]=Float.floatToRawIntBits(z);data[at+3]=color;data[at+4]=Float.floatToRawIntBits(sprite.getU(uv.u()));data[at+5]=Float.floatToRawIntBits(sprite.getV(1F-uv.v()));data[at+6]=0;data[at+7]=packed;
            }out.add(new BakedQuad(data,-1,Direction.getNearest(nx,ny,nz),sprite,false));
        }}
    }
    @Override public boolean useAmbientOcclusion(){return false;}@Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType type){return TriState.FALSE;}@Override public boolean isGui3d(){return true;}@Override public boolean isCustomRenderer(){return false;}@Override public TextureAtlasSprite getParticleIcon(){return particle;}@Override public TextureAtlasSprite getParticleIcon(ModelData data){return particle;}
    @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){int hand=context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND?-1:1;if(context.firstPerson()){poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand*45));poses.scale(.4F,.4F,.4F);}else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){poses.translate(-hand/16D,-.125,.625);poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-180));poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));poses.translate(-hand/16D,.4375,.0625);poses.translate(0,.1875,-.3125);poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(20));poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand*45));poses.scale(-.375F,-.375F,.375F);}else getTransforms().getTransform(context).apply(left,poses);return this;}
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}@Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}@Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return side==null?quads:List.of();}@Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){return getQuads(state,side,random);}
}
