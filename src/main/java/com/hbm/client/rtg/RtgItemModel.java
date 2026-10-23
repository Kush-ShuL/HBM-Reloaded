package com.hbm.client.rtg;

import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;

/** Source RenderRTGBlock uses ObjUtil's icon-mapped Gen, not the world renderer's texture. */
public final class RtgItemModel extends BakedModelWrapper<BakedModel> {
    private final TextureAtlasSprite sprite;private List<BakedQuad> quads;
    public RtgItemModel(BakedModel original,Function<Material,TextureAtlasSprite> textures){super(original);sprite=textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/rtg")));}
    @Override public boolean useAmbientOcclusion(){return false;}
    @Override public TriState useAmbientOcclusion(BlockState state,ModelData data,RenderType renderType){return TriState.FALSE;}
    @Override public boolean isGui3d(){return true;}
    @Override public boolean isCustomRenderer(){return false;}
    @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){getTransforms().getTransform(context).apply(left,poses);return this;}
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous){return List.of(this);}
    @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous){return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS));}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){return side==null?quads():List.of();}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type){return getQuads(state,side,random);}
    private List<BakedQuad> quads(){
        if(quads!=null)return quads;var mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),RtgRenderer.MODEL);var result=new ArrayList<BakedQuad>();
        for(var face:mesh.faces())if(face.legacyPart().equalsIgnoreCase("Gen")){
            if(face.corners().size()!=3&&face.corners().size()!=4)throw new IllegalStateException("Unsupported original RTG polygon "+face.sourceLine());
            var normal=face.normal();int packed=((byte)(normal.x()*127)&255)|((byte)(normal.y()*127)&255)<<8|((byte)(normal.z()*127)&255)<<16;var vertices=new int[32];
            for(int i=0;i<4;i++){var corner=face.corners().get(Math.min(i,face.corners().size()-1));var position=mesh.positions().get(corner.position());var uv=mesh.textureCoordinates().get(corner.texture());int offset=i*8;vertices[offset]=Float.floatToRawIntBits(position.x()+.5F);vertices[offset+1]=Float.floatToRawIntBits(position.y());vertices[offset+2]=Float.floatToRawIntBits(position.z()+.5F);vertices[offset+3]=0xffffffff;vertices[offset+4]=Float.floatToRawIntBits(sprite.getU(uv.u()));vertices[offset+5]=Float.floatToRawIntBits(sprite.getV(1-uv.v()));vertices[offset+6]=0;vertices[offset+7]=packed;}
            result.add(new BakedQuad(vertices,-1,Direction.getNearest(normal.x(),normal.y(),normal.z()),sprite,false));
        }
        return quads=List.copyOf(result);
    }
}
