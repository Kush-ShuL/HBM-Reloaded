package com.hbm.client.structure.sandbags;

import com.hbm.block.sandbags.SandbagsBlock;
import com.hbm.client.structure.woodfixtures46.LegacyWoodGeometry;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.common.util.TriState;

/** Source RenderBlocksNT UVs shared with wood fixtures, actual runtime neighbor bounds and original side visibility. */
final class SandbagsModel extends BakedModelWrapper<BakedModel> {
    private static final ModelProperty<Integer> KEY = new ModelProperty<>();
    private final boolean inventory;
    private final TextureAtlasSprite sprite;
    private final Map<Integer, List<BakedQuad>> quads = new ConcurrentHashMap<>();
    SandbagsModel(BakedModel original, boolean inventory, Function<Material, TextureAtlasSprite> textures) {
        super(original); this.inventory = inventory;
        sprite = textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.parse("hbm:block/sandbags")));
    }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean useAmbientOcclusion() { return !inventory; }
    @Override public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType layer) { return inventory ? TriState.FALSE : TriState.TRUE; }
    @Override public TextureAtlasSprite getParticleIcon() { return sprite; }
    @Override public TextureAtlasSprite getParticleIcon(ModelData data) { return sprite; }
    @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack poses, boolean left) {
        int hand = context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1 : 1;
        if (context.firstPerson()) { poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand * 45)); poses.scale(.4F, .4F, .4F); }
        else if (context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            poses.translate(-hand / 16D, -.125, .625); poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-180)); poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
            poses.translate(-hand / 16D, .4375, .0625); poses.translate(0, .1875, -.3125); poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(20));
            poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand * 45)); poses.scale(-.375F, -.375F, .375F);
        } else getTransforms().getTransform(context).apply(left, poses);
        return this;
    }
    @Override public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) { return List.of(this); }
    @Override public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) { return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)); }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) { return ChunkRenderTypeSet.of(RenderType.cutout()); }
    @Override public ModelData getModelData(BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData data) {
        int key = SandbagsBlock.joins(world, pos, state);
        // Unlike BlockBarrier/BlockWoodStructure, source Sandbags does not override shouldSideBeRendered.
        // Interior cuboid faces are visible; a boundary face is hidden only by an opaque neighbor.
        for (var direction : Direction.values()) if (SandbagsBlock.opaque(world, pos.relative(direction))) key |= 1 << (4 + direction.get3DDataValue());
        return data.derive().with(KEY, key).build();
    }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return getQuads(state, side, random, ModelData.EMPTY, null); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType layer) {
        if (side != null) return List.of();
        Integer value = data.get(KEY); return quads.computeIfAbsent(inventory ? -1 : value == null ? 0 : value, this::bake);
    }
    private List<BakedQuad> bake(int key) {
        boolean item = key == -1;
        double x0 = item ? .125 : (key & 1) != 0 ? 0 : .25, z0 = item ? .125 : (key & 2) != 0 ? 0 : .25;
        double x1 = item ? .875 : (key & 4) != 0 ? 1 : .75, z1 = item ? .875 : (key & 8) != 0 ? 1 : .75;
        var result = LegacyWoodGeometry.cuboid(x0, 0, z0, x1, 1, z1, item, sprite);
        if (item) return result;
        return result.stream().filter(quad -> {
            var side = quad.getDirection(); boolean boundary = switch (side) { case DOWN, UP -> true; case WEST -> x0 == 0; case NORTH -> z0 == 0; case EAST -> x1 == 1; case SOUTH -> z1 == 1; };
            return !boundary || (key & 1 << (4 + side.get3DDataValue())) == 0;
        }).toList();
    }
}
