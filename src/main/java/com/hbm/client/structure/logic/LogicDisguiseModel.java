package com.hbm.client.structure.logic;

import com.hbm.world.structure.logic.LogicDisguiseState;
import com.hbm.world.structure.logic.LogicRuntime;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

/** Source LogicBlock replaces six icons, not the disguise block's geometry, tint, collision or opacity. */
public final class LogicDisguiseModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<BlockState> DISGUISE = new ModelProperty<>();
    public LogicDisguiseModel(BakedModel original) { super(original); }
    @Override public ModelData getModelData(BlockAndTintGetter level,BlockPos pos,BlockState state,ModelData data) {
        if (level.getBlockEntity(pos) instanceof LogicRuntime tile && tile.disguiseId != null) {
            var disguise = LogicDisguiseState.resolve(tile.disguiseId,tile.disguiseMeta);
            if (disguise != null) return data.derive().with(DISGUISE,disguise).build();
        }
        return data;
    }
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random) {
        return getQuads(state,side,random,ModelData.EMPTY,null);
    }
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType layer) {
        var quads = originalModel.getQuads(state,side,random,ModelData.EMPTY,null);
        var disguise = data.get(DISGUISE);
        if (state == null || disguise == null || quads.isEmpty()) return quads;
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguise);
        var result = new ArrayList<BakedQuad>(quads.size());
        for (var quad : quads) result.add(icon(quad,faceIcon(model,disguise,quad.getDirection())));
        return result;
    }
    private static TextureAtlasSprite faceIcon(BakedModel model,BlockState state,Direction side) {
        // Source getIcon(side,meta) has no RNG. Use a private source-independent seed, never the world tessellation RNG.
        var faces = model.getQuads(state,side,RandomSource.create(0),ModelData.EMPTY,null);
        if (!faces.isEmpty()) return faces.getFirst().getSprite();
        for (var quad : model.getQuads(state,null,RandomSource.create(0),ModelData.EMPTY,null))
            if (quad.getDirection() == side) return quad.getSprite();
        return model.getParticleIcon(ModelData.EMPTY);
    }
    private static BakedQuad icon(BakedQuad original,TextureAtlasSprite icon) {
        var old = original.getSprite();
        int[] vertices = original.getVertices().clone();
        for (int vertex = 0; vertex < 4; vertex++) {
            int offset = vertex * 8;
            float u = (Float.intBitsToFloat(vertices[offset+4])-old.getU0())/(old.getU1()-old.getU0());
            float v = (Float.intBitsToFloat(vertices[offset+5])-old.getV0())/(old.getV1()-old.getV0());
            vertices[offset+4] = Float.floatToRawIntBits(icon.getU(u));
            vertices[offset+5] = Float.floatToRawIntBits(icon.getV(v));
        }
        return new BakedQuad(vertices,-1,original.getDirection(),icon,original.isShade(),original.hasAmbientOcclusion());
    }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state,RandomSource random,ModelData data) {
        // The source is the opaque LogicBlock's pass0 even for glass/ice disguise icons. No transparent copied model pass.
        return ChunkRenderTypeSet.of(RenderType.cutout());
    }
}
