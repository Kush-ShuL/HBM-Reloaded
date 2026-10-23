package com.hbm.client.food;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.WavefrontMesh;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;

/** Original noSmooth conservecrate.obj and ObjUtil.renderWithIcon, cached as real chunk mesh. */
public final class ConserveCrateModel extends BakedModelWrapper<BakedModel> {
    private final boolean inventory;private final TextureAtlasSprite sprite;private List<BakedQuad> quads;
    public ConserveCrateModel(BakedModel original,boolean inventory,Function<Material,TextureAtlasSprite> textures){
        super(original);this.inventory=inventory;this.sprite=textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/crate_can")));
    }
    @Override public boolean useAmbientOcclusion(){return false;}
    @Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType type){return TriState.FALSE;}
    @Override public boolean isGui3d(){return true;}
    @Override public boolean isCustomRenderer(){return false;}
    @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack pose,boolean left){getTransforms().getTransform(context).apply(left,pose);return this;}
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
    @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data){return ChunkRenderTypeSet.of(RenderType.cutout());}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return getQuads(state,side,random,ModelData.EMPTY,null);}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type){if(side!=null)return List.of();if(quads==null)quads=build();return quads;}
    private List<BakedQuad> build(){
        var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.parse("hbm:models/blocks/conservecrate.obj"));var result=new ArrayList<BakedQuad>();
        for(var face:mesh.faces()){
            //The authored mesh has 60 triangles; source ObjUtil repeats each triangle's last vertex.
            if(face.corners().size()!=3)throw new IllegalStateException("Unexpected authored conservecrate face at "+face.sourceLine());
            var normal=face.normal();float brightness=inventory?1:Math.max(.45F,(normal.y()+.7F)*.9F-Math.abs(normal.x())*.1F+Math.abs(normal.z())*.1F);
            int gray=Math.clamp((int)(brightness*255),0,255),color=0xff000000|gray<<16|gray<<8|gray;
            int packed=((byte)(normal.x()*127)&255)|((byte)(normal.y()*127)&255)<<8|((byte)(normal.z()*127)&255)<<16;var vertices=new int[32];
            for(int i=0;i<4;i++){
                var corner=face.corners().get(Math.min(i,2));var pos=mesh.positions().get(corner.position());var uv=corner.texture()>=0?mesh.textureCoordinates().get(corner.texture()):new WavefrontMesh.TexCoord(0,0,0);int offset=i*8;
                //Inventory source translates y=-.5 before vanilla centers the block; target ItemRenderer already centers by -.5.
                vertices[offset]=Float.floatToRawIntBits(pos.x()+.5F);vertices[offset+1]=Float.floatToRawIntBits(pos.y());vertices[offset+2]=Float.floatToRawIntBits(pos.z()+.5F);vertices[offset+3]=color;
                vertices[offset+4]=Float.floatToRawIntBits(sprite.getU(uv.u()));vertices[offset+5]=Float.floatToRawIntBits(sprite.getV(1-uv.v()));vertices[offset+6]=0;vertices[offset+7]=packed;
            }
            result.add(new BakedQuad(vertices,-1,Direction.getNearest(normal.x(),normal.y(),normal.z()),sprite,false));
        }
        return List.copyOf(result);
    }
}
