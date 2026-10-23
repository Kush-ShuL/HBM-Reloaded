package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.DetCordBlock;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.WavefrontMesh;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

/** RenderDetCord's original cable_neo mesh, named parts, mirrored Z labels, per-face colors and UVs. */
final class DetCordModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Integer> MASK=new ModelProperty<>();
    private static final ResourceLocation MODEL=ResourceLocation.parse("hbm:models/blocks/cable_neo.obj");
    private final TextureAtlasSprite sprite;
    private final Map<Integer,List<BakedQuad>> cache=new ConcurrentHashMap<>();
    private List<BakedQuad> inventory;
    DetCordModel(BakedModel original,TextureAtlasSprite sprite){super(original);this.sprite=sprite;}
    @Override public boolean useAmbientOcclusion(){return false;}
    @Override public boolean isGui3d(){return true;}
    @Override public BakedModel applyTransform(net.minecraft.world.item.ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack poses,boolean left){getTransforms().getTransform(context).apply(left,poses);return this;}
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
    @Override public ModelData getModelData(BlockAndTintGetter world,BlockPos pos,BlockState state,ModelData data){return data.derive().with(MASK,DetCordBlock.connections(world,pos)).build();}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer){
        if(side!=null)return List.of();
        if(state==null){if(inventory==null)inventory=build(0,true);return inventory;}
        return cache.computeIfAbsent(data.has(MASK)?data.get(MASK):0,mask->build(mask,false));
    }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS));}
    private List<BakedQuad> build(int mask,boolean item){
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),MODEL);var result=new ArrayList<BakedQuad>();
        List<String> groups;
        if(item)groups=List.of("CZ");
        else if(mask==48||mask==32||mask==16)groups=List.of("CX");
        else if(mask==12||mask==8||mask==4)groups=List.of("CY");
        else if(mask==3||mask==2||mask==1)groups=List.of("CZ");
        else {var selected=new ArrayList<String>();selected.add("Core");if((mask&32)!=0)selected.add("posX");if((mask&16)!=0)selected.add("negX");if((mask&8)!=0)selected.add("posY");if((mask&4)!=0)selected.add("negY");if((mask&1)!=0)selected.add("posZ");if((mask&2)!=0)selected.add("negZ");groups=selected;}
        for(int faceIndex=0;faceIndex<mesh.faces().size();faceIndex++){
            var face=mesh.faces().get(faceIndex);if(!groups.contains(face.legacyPart()))continue;
            if(face.corners().size()==4)result.add(quad(mesh,face,new int[]{0,1,2,3},item));
            else for(var triangle:mesh.triangles())if(triangle.face()==faceIndex)result.add(quad(mesh,face,new int[]{triangle.first(),triangle.second(),triangle.third(),triangle.third()},item));
        }
        return List.copyOf(result);
    }
    private BakedQuad quad(WavefrontMesh mesh,WavefrontMesh.Face face,int[] indices,boolean item){
        int stride=DefaultVertexFormat.BLOCK.getVertexSize()/4;int[] data=new int[stride*4];
        float nx=face.normal().x(),ny=face.normal().y(),nz=face.normal().z();if(item){nx=-nx;nz=-nz;}
        float shade=item?1:Math.max(.45F,ny*.3F+.7F-Math.abs(nx)*.1F+Math.abs(nz)*.1F);int c=(int)(shade*255),color=0xFF000000|c<<16|c<<8|c;
        int normal=((int)(nx*127)&255)|(((int)(ny*127)&255)<<8)|(((int)(nz*127)&255)<<16);
        for(int i=0;i<4;i++){
            var corner=face.corners().get(indices[i]);var p=mesh.positions().get(corner.position());float x=p.x(),y=p.y(),z=p.z();if(item){x*=-1.25F;y*=1.25F;z*=-1.25F;}
            var uv=mesh.textureCoordinates().get(corner.texture());int offset=i*stride;
            data[offset]=Float.floatToRawIntBits(x+.5F);data[offset+1]=Float.floatToRawIntBits(y+.5F);data[offset+2]=Float.floatToRawIntBits(z+.5F);data[offset+3]=color;
            data[offset+4]=Float.floatToRawIntBits(sprite.getU(uv.u()));data[offset+5]=Float.floatToRawIntBits(sprite.getV(1-uv.v()));data[offset+6]=0;data[offset+7]=normal;
        }
        return new BakedQuad(data,-1,Direction.getNearest(nx,ny,nz),sprite,false,false);
    }
}
