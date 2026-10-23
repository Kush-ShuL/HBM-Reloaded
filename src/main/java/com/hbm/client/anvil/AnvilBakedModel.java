package com.hbm.client.anvil;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
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
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
/** Original HFR anvil mesh with source per-face icon UVs and ObjUtil's flat world shading. */
final class AnvilBakedModel extends BakedModelWrapper<BakedModel> {
    private final boolean inventory; private final Map<String,TextureAtlasSprite> textures=new HashMap<>(); private final Map<Integer,List<BakedQuad>> cache=new java.util.concurrent.ConcurrentHashMap<>();
    AnvilBakedModel(BakedModel original,boolean inventory,Function<Material,TextureAtlasSprite> sprites,com.hbm.machine.anvil.AnvilContent.Tier tier) {
        super(original); this.inventory=inventory;
        for(var pair:Map.of("top","anvil_"+tier.symbol(),"bottom","anvil_"+tier.texture(),"front","anvil_"+tier.texture(),"back","anvil_"+tier.texture(),"left","anvil_"+tier.texture(),"right","anvil_"+tier.texture()).entrySet()) textures.put(pair.getKey(),sprites.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","block/"+pair.getValue()))));
    }
    @Override public boolean useAmbientOcclusion() { return false; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack pose,boolean left) { getTransforms().getTransform(context).apply(left,pose); return this; }
    @Override public List<BakedModel> getRenderPasses(ItemStack stack,boolean fabulous) { return List.of(this); }
    @Override public List<RenderType> getRenderTypes(ItemStack stack,boolean fabulous) { return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)); }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data) { return ChunkRenderTypeSet.of(RenderType.cutout()); }
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random) { return getQuads(state,side,random,ModelData.EMPTY,null); }
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type) { if(side!=null) return List.of(); int meta=state==null?0:state.getValue(com.hbm.machine.anvil.NtmAnvilBlock.META);return cache.computeIfAbsent(meta,this::build); }
    private List<BakedQuad> build(int meta) {
        double angle=Math.toRadians(inventory?180:meta==2?90:meta==3?270:meta==4?180:0);float cos=(float)Math.cos(angle),sin=(float)Math.sin(angle);
        WavefrontMesh mesh=LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(),ResourceLocation.fromNamespaceAndPath("hbm","models/blocks/anvil.obj")); var result=new ArrayList<BakedQuad>();
        for(var triangle:mesh.triangles()) {
            var face=mesh.faces().get(triangle.face()); var sprite=textures.get(face.legacyPart().toLowerCase(Locale.ROOT)); if(sprite==null) continue;
            var originalNormal=face.normal();var normal=new WavefrontMesh.Normal(originalNormal.x()*cos+originalNormal.z()*sin,originalNormal.y(),originalNormal.z()*cos-originalNormal.x()*sin); float brightness=inventory?1:Math.max(.45F,normal.y()*.3F+.7F-Math.abs(normal.x())*.1F+Math.abs(normal.z())*.1F); // Tessellator.setColorOpaque_F delegates to setColorRGBA, saturating each channel.
            // Source ObjUtil can exceed1 on rotated bevels; packing258 directly would wrap to near-black.
            int gray=Math.clamp((int)(brightness*255),0,255),color=0xff000000|gray<<16|gray<<8|gray;
            int packedNormal=((byte)(normal.x()*127)&255)|((byte)(normal.y()*127)&255)<<8|((byte)(normal.z()*127)&255)<<16; int[] vertices=new int[32];
            int[] corners={triangle.first(),triangle.second(),triangle.third(),triangle.third()};
            for(int i=0;i<4;i++) {
                var corner=face.corners().get(corners[i]); var pos=mesh.positions().get(corner.position()); var uv=corner.texture()>=0?mesh.textureCoordinates().get(corner.texture()):new WavefrontMesh.TexCoord(0,0,0); int offset=i*8;
                vertices[offset]=Float.floatToRawIntBits(pos.x()*cos+pos.z()*sin+.5F); vertices[offset+1]=Float.floatToRawIntBits(pos.y()); vertices[offset+2]=Float.floatToRawIntBits(pos.z()*cos-pos.x()*sin+.5F); vertices[offset+3]=color;
                vertices[offset+4]=Float.floatToRawIntBits(sprite.getU(uv.u())); vertices[offset+5]=Float.floatToRawIntBits(sprite.getV(1-uv.v())); vertices[offset+6]=0; vertices[offset+7]=packedNormal;
            }
            result.add(new BakedQuad(vertices,-1,Direction.getNearest(normal.x(),normal.y(),normal.z()),sprite,false));
        }
        return List.copyOf(result);
    }
}
