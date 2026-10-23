package com.hbm.block.hevbattery.client;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
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
/** Original RenderBattery/ObjUtil noSmooth mesh, icon UVs and authored inventory transform. */
final class HevBatteryModel extends BakedModelWrapper<BakedModel> {
    private final TextureAtlasSprite sprite;private final List<BakedQuad> quads;
    HevBatteryModel(BakedModel parent,boolean inventory,Function<Material,TextureAtlasSprite> textures){super(parent);sprite=textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/hev_battery")));var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/blocks/battery.obj"));var output=new ArrayList<BakedQuad>();float yaw=0;
        for(var face:mesh.faces()){if(face.corners().size()!=3)throw new IllegalStateException("Source HEV battery OBJ must remain triangulated at "+face.sourceLine());var a=mesh.positions().get(face.corners().get(0).position());var b=mesh.positions().get(face.corners().get(1).position());var c=mesh.positions().get(face.corners().get(2).position());double ax=b.x()-a.x(),ay=b.y()-a.y(),az=b.z()-a.z(),bx=c.x()-a.x(),by=c.y()-a.y(),bz=c.z()-a.z();var sourceNormal=LegacyVector.createVectorHelper(ay*bz-az*by,az*bx-ax*bz,ax*by-ay*bx).normalize();var normal=LegacyVector.createVectorHelper((float)sourceNormal.xCoord,(float)sourceNormal.yCoord,(float)sourceNormal.zCoord);normal.rotateAroundY(yaw);float nx=(float)normal.xCoord,ny=(float)normal.yCoord,nz=(float)normal.zCoord;float brightness=inventory?1F:Math.max(.45F,(ny+.7F)*.9F-(float)Math.abs(normal.xCoord)*.1F+(float)Math.abs(normal.zCoord)*.1F);int gray=Math.clamp((int)(brightness*255F),0,255),color=0xff000000|gray<<16|gray<<8|gray;int packedNormal=((byte)(nx*127F)&255)|((byte)(ny*127F)&255)<<8|((byte)(nz*127F)&255)<<16;int[] vertices=new int[32];
            for(int i=0;i<4;i++){var corner=face.corners().get(Math.min(i,2));var p=mesh.positions().get(corner.position());var uv=mesh.textureCoordinates().get(corner.texture());var point=LegacyVector.createVectorHelper(p.x(),p.y(),p.z());point.rotateAroundY(yaw);float x=(float)point.xCoord,y=(float)point.yCoord,z=(float)point.zCoord;
                // Source world T(.5,0,.5); source inventory T(0,-.625,0)*S(3), compensated for modern ItemRenderer centering.
                if(inventory){x=x*3F+.5F;y=y*3F-.125F;z=z*3F+.5F;}else{x+=.5F;z+=.5F;}
                int at=i*8;vertices[at]=Float.floatToRawIntBits(x);vertices[at+1]=Float.floatToRawIntBits(y);vertices[at+2]=Float.floatToRawIntBits(z);vertices[at+3]=color;vertices[at+4]=Float.floatToRawIntBits(sprite.getU(uv.u()));vertices[at+5]=Float.floatToRawIntBits(sprite.getV(1F-uv.v()));vertices[at+6]=0;vertices[at+7]=packedNormal;
            }output.add(new BakedQuad(vertices,-1,Direction.getNearest(nx,ny,nz),sprite,false));
        }quads=List.copyOf(output);
    }
    @Override public boolean useAmbientOcclusion(){return false;}
    @Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType type){return TriState.FALSE;}
    @Override public boolean isGui3d(){return true;}
    @Override public boolean isCustomRenderer(){return false;}
    @Override public TextureAtlasSprite getParticleIcon(){return sprite;}
    @Override public TextureAtlasSprite getParticleIcon(ModelData data){return sprite;}
    @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){getTransforms().getTransform(context).apply(left,poses);return this;}
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
    @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return side==null?quads:List.of();}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type){return getQuads(state,side,random);}
}
