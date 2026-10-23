package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.TimedChargeBlock;
import com.hbm.client.model.LegacyObjModels;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/** Original RenderBlockRotated mesh rendering, retaining ObjUtil's flat normals and independent color formula. */
final class TimedChargeModel extends BakedModelWrapper<BakedModel>{
    private final ResourceLocation model;
    private final TextureAtlasSprite sprite;
    private final boolean inventory;
    private final Map<Integer,List<BakedQuad>> cache=new ConcurrentHashMap<>();
    TimedChargeModel(BakedModel original,TextureAtlasSprite sprite,boolean c4,boolean inventory){super(original);this.sprite=sprite;this.inventory=inventory;this.model=ResourceLocation.parse("hbm:models/blocks/"+(c4?"charge_c4":"charge_dynamite")+".obj");}
    @Override public boolean isCustomRenderer(){return inventory;}
    @Override public boolean useAmbientOcclusion(){return false;}
    @Override public boolean isGui3d(){return true;}
    @Override public BakedModel applyTransform(net.minecraft.world.item.ItemDisplayContext context,com.mojang.blaze3d.vertex.PoseStack poses,boolean left){getTransforms().getTransform(context).apply(left,poses);return this;}
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return side==null?cache.computeIfAbsent(state==null?-1:state.getValue(TimedChargeBlock.FACE).get3DDataValue(),this::build):List.of();}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type){return getQuads(state,side,random);}
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}
    private List<BakedQuad> build(int faceIndex){
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),model);var result=new ArrayList<BakedQuad>();
        boolean item=faceIndex<0;var rotation=new Quaternionf();
        if(item)rotation.rotateY(-(float)Math.PI/2).rotateZ(-(float)Math.PI/2);
        else switch(Direction.from3DDataValue(faceIndex)){case DOWN->rotation.rotateZ((float)Math.PI);case UP->{}case NORTH->rotation.rotateY((float)Math.PI/2).rotateZ(-(float)Math.PI/2);case SOUTH->rotation.rotateY(1.5F*(float)Math.PI).rotateZ(-(float)Math.PI/2);case WEST->rotation.rotateY((float)Math.PI).rotateZ(-(float)Math.PI/2);case EAST->rotation.rotateZ(-(float)Math.PI/2);}
        int stride=DefaultVertexFormat.BLOCK.getVertexSize()/4;
        for(int i=0;i<mesh.faces().size();i++){
            var face=mesh.faces().get(i);var tris=new ArrayList<int[]>();if(face.corners().size()==4)tris.add(new int[]{0,1,2,3});else for(var tri:mesh.triangles())if(tri.face()==i)tris.add(new int[]{tri.first(),tri.second(),tri.third(),tri.third()});
            var normal=new Vector3f(face.normal().x(),face.normal().y(),face.normal().z()).rotate(rotation);
            float brightness=item?1:Math.max(.45F,(normal.y+.7F)*.9F-Math.abs(normal.x)*.1F+Math.abs(normal.z)*.1F);int c=Math.max(0,Math.min((int)(brightness*255),255)),color=0xFF000000|c<<16|c<<8|c;
            int packedNormal=((int)(normal.x*127)&255)|(((int)(normal.y*127)&255)<<8)|(((int)(normal.z*127)&255)<<16);
            for(var corners:tris){int[] data=new int[stride*4];for(int v=0;v<4;v++){
                var corner=face.corners().get(corners[v]);var source=mesh.positions().get(corner.position());var p=new Vector3f(source.x(),source.y()+(item?.375F:0),source.z()).rotate(rotation);var uv=mesh.textureCoordinates().get(corner.texture());int at=v*stride;
                data[at]=Float.floatToRawIntBits(p.x+.5F);data[at+1]=Float.floatToRawIntBits(p.y+.5F);data[at+2]=Float.floatToRawIntBits(p.z+.5F);data[at+3]=color;data[at+4]=Float.floatToRawIntBits(sprite.getU(uv.u()));data[at+5]=Float.floatToRawIntBits(sprite.getV(1-uv.v()));data[at+6]=0;data[at+7]=packedNormal;
            }result.add(new BakedQuad(data,-1,Direction.getNearest(normal.x,normal.y,normal.z),sprite,false,false));}
        }
        return List.copyOf(result);
    }
}
