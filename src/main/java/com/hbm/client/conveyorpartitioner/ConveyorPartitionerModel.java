package com.hbm.client.conveyorpartitioner;

import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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

/** Source RenderPartitioner and original crane_buffer.noSmooth; the sole HFR OBJ loader supplies flat faces. */
final class ConveyorPartitionerModel extends BakedModelWrapper<BakedModel> {
    private final List<BakedQuad> quads;private final TextureAtlasSprite particle;
    ConveyorPartitionerModel(BakedModel original,int metadata,boolean inventory,Function<Material,TextureAtlasSprite> getter){
        super(original);particle=getter.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/crane_partitioner_side")));
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/blocks/crane_buffer.obj"));
        float rotation=metadata==2?.5F*(float)Math.PI:metadata==4?(float)Math.PI:metadata==3?1.5F*(float)Math.PI:0;
        if(inventory)rotation=0;
        var parts=new LinkedHashMap<String,String>();parts.put("Side","crane_partitioner_side");parts.put("Back","crane_partitioner_back");parts.put("Top_Top.001","crane_top");parts.put("Inner","crane_splitter_inner");parts.put("InnerSide","crane_splitter_inner_side");parts.put("Belt","crane_splitter_belt");
        var output=new ArrayList<BakedQuad>();
        for(var part:parts.entrySet()){
            var sprite=getter.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/"+part.getValue())));
            for(var face:mesh.faces()){
                if(!WavefrontMesh.legacyPart(part.getKey()).test(face))continue;
                if(face.corners().size()!=3&&face.corners().size()!=4)throw new IllegalStateException("Source partitioner ObjUtil expects triangle/quad at "+face.sourceLine());
                var n=face.normal();var normal=LegacyVector.createVectorHelper(n.x(),n.y(),n.z());normal.rotateAroundY(rotation);
                float brightness=inventory?1:Math.max(.45F,(float)normal.yCoord*.3F+.7F-(float)Math.abs(normal.xCoord)*.1F+(float)Math.abs(normal.zCoord)*.1F);
                if(inventory)normal.rotateAroundY(.5F*(float)Math.PI);
                float nx=(float)normal.xCoord,ny=(float)normal.yCoord,nz=(float)normal.zCoord;
                int gray=Math.clamp((int)(brightness*255),0,255),color=0xff000000|gray<<16|gray<<8|gray,packed=((byte)(nx*127F)&255)|((byte)(ny*127F)&255)<<8|((byte)(nz*127F)&255)<<16;
                int[] data=new int[32];
                for(int i=0;i<4;i++){
                    var corner=face.corners().get(Math.min(i,face.corners().size()-1));var p=mesh.positions().get(corner.position());var uv=mesh.textureCoordinates().get(corner.texture());var point=LegacyVector.createVectorHelper(p.x(),p.y(),p.z());point.rotateAroundY(rotation);
                    if(inventory){point.yCoord-=.5F;point.rotateAroundY(.5F*(float)Math.PI);point.yCoord+=.5F;}else{point.xCoord+=.5F;point.zCoord+=.5F;}
                    if(inventory){point.xCoord+=.5F;point.zCoord+=.5F;}
                    int at=i*8;data[at]=Float.floatToRawIntBits((float)point.xCoord);data[at+1]=Float.floatToRawIntBits((float)point.yCoord);data[at+2]=Float.floatToRawIntBits((float)point.zCoord);data[at+3]=color;data[at+4]=Float.floatToRawIntBits(sprite.getU(uv.u()));data[at+5]=Float.floatToRawIntBits(sprite.getV(1F-uv.v()));data[at+6]=0;data[at+7]=packed;
                }
                output.add(new BakedQuad(data,-1,Direction.getNearest(nx,ny,nz),sprite,false));
            }
        }
        quads=List.copyOf(output);
    }
    @Override public boolean useAmbientOcclusion(){return false;}@Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType type){return TriState.FALSE;}
    @Override public boolean isGui3d(){return true;}@Override public boolean isCustomRenderer(){return false;}@Override public TextureAtlasSprite getParticleIcon(){return particle;}@Override public TextureAtlasSprite getParticleIcon(ModelData data){return particle;}
    @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){
        int hand=context==ItemDisplayContext.FIRST_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND?-1:1;
        if(context.firstPerson()){poses.mulPose(Axis.YP.rotationDegrees(hand*45));poses.scale(.4F,.4F,.4F);}
        else if(context==ItemDisplayContext.THIRD_PERSON_LEFT_HAND||context==ItemDisplayContext.THIRD_PERSON_RIGHT_HAND){poses.translate(-hand/16D,-.125,.625);poses.mulPose(Axis.YP.rotationDegrees(-180));poses.mulPose(Axis.XP.rotationDegrees(90));poses.translate(-hand/16D,.4375,.0625);poses.translate(0,.1875,-.3125);poses.mulPose(Axis.XP.rotationDegrees(20));poses.mulPose(Axis.YP.rotationDegrees(hand*45));poses.scale(-.375F,-.375F,.375F);}
        else getTransforms().getTransform(context).apply(left,poses);return this;
    }
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
    @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return side==null?quads:List.of();}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){return getQuads(state,side,random);}
}
