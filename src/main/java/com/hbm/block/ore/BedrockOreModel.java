package com.hbm.block.ore;

import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;

/** Original ten shape overlays over an untinted bedrock host. The inventory renders only its host. */
public final class BedrockOreModel extends BakedModelWrapper<BakedModel> {
    private final BakedModel[] shapes;
    private final BakedModel plain;
    public BedrockOreModel(BakedModel original, BakedModel plain, BakedModel[] shapes) { super(original); this.plain = plain; this.shapes = shapes.clone(); }
    @Override public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data) {
        if (level.getBlockEntity(pos) instanceof BedrockOreEntity ore) return data.derive().with(BedrockOreModelData.SHAPE, ore.shape).build();
        return data;
    }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return getQuads(state, side, random, ModelData.EMPTY, null); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType layer) {
        Integer shape = data.get(BedrockOreModelData.SHAPE);
        return (state == null || shape == null ? plain : shapes[shape % shapes.length]).getQuads(state, side, random, data, layer);
    }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) { return ChunkRenderTypeSet.of(RenderType.cutout()); }
    @Override public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) { return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)); }
    @Override public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) { return List.of(this); }
    @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack pose, boolean leftHand) {
        getTransforms().getTransform(context).apply(leftHand, pose); return this;
    }
}
