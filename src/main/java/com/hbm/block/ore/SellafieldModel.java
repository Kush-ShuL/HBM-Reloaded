package com.hbm.block.ore;

import java.util.List;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
import net.neoforged.neoforge.client.model.data.ModelProperty;

/** Original coordinate hash selects four original hosts; original overlay stays untinted. */
public final class SellafieldModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Integer> HOST = new ModelProperty<>();
    private final BakedModel[] hosts;
    public SellafieldModel(BakedModel original, BakedModel[] hosts) { super(original); this.hosts = hosts.clone(); }

    public static int host(BlockPos pos) {
        long hash = (long) (pos.getX() * 3129871) ^ (long) pos.getY() * 116129781L ^ (long) pos.getZ();
        hash = hash * hash * 42317861L + hash * 11L;
        int index = (int) (hash >> 16 & 3L);
        return Math.abs(index) % 4;
    }

    @Override public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data) {
        return data.derive().with(HOST, host(pos)).build();
    }
    private BakedModel host(ModelData data) { Integer index = data.get(HOST); return hosts[index == null ? 0 : index]; }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return hosts[0].getQuads(state, side, random); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType layer) {
        return host(data).getQuads(state, side, random, data, layer);
    }
    @Override public TextureAtlasSprite getParticleIcon(ModelData data) { return host(data).getParticleIcon(data); }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) { return ChunkRenderTypeSet.of(RenderType.cutout()); }
    @Override public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) { return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)); }
    @Override public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) { return List.of(this); }
    @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack pose, boolean leftHand) {
        getTransforms().getTransform(context).apply(leftHand, pose); return this;
    }
}
