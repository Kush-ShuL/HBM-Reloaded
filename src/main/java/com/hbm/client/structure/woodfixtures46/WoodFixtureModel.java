package com.hbm.client.structure.woodfixtures46;

import com.hbm.block.woodfixtures46.WoodBarrierBlock;
import com.hbm.block.woodfixtures46.WoodStructureBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
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

/** True universal-ISBRH world and inventory geometry, sampled from actual neighbors even during template placement. */
final class WoodFixtureModel extends BakedModelWrapper<BakedModel> {
    private record Key(int type, int joins) {}
    private static final ModelProperty<Integer> JOINS = new ModelProperty<>();
    private static final Direction[] SIDES = {Direction.WEST, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.UP};
    private final boolean barrier, inventory;
    private final int itemType;
    private final TextureAtlasSprite sprite;
    private final Map<Key, List<BakedQuad>> cache = new ConcurrentHashMap<>();

    WoodFixtureModel(BakedModel original, boolean barrier, boolean inventory, Function<Material, TextureAtlasSprite> sprites) {
        this(original,barrier,inventory,0,sprites);
    }
    WoodFixtureModel(BakedModel original, boolean barrier, boolean inventory, int itemType, Function<Material,TextureAtlasSprite> sprites) {
        this(original,barrier,inventory,itemType,sprites.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:block/wood_barrier"))));
    }
    private WoodFixtureModel(BakedModel original, boolean barrier, boolean inventory, int itemType, TextureAtlasSprite sprite) {
        super(original); this.barrier = barrier; this.inventory = inventory; this.itemType = itemType; this.sprite = sprite;
    }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean useAmbientOcclusion() { return !inventory; }
    @Override public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType layer) { return inventory ? TriState.FALSE : TriState.TRUE; }
    @Override public TextureAtlasSprite getParticleIcon() { return sprite; }
    @Override public ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }
    @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack poses, boolean left) {
        int hand = context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1 : 1;
        if (context.firstPerson()) {
            poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand * 45)); poses.scale(.4F, .4F, .4F);
        } else if (context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
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
        int key = 0;
        if (barrier) key = WoodBarrierBlock.joins(world, pos, state);
        else for (int i = 0; i < SIDES.length; i++) {
            var neighbor = world.getBlockState(pos.relative(SIDES[i]));
            if (neighbor.is(state.getBlock()) && neighbor.getValue(WoodStructureBlock.META).equals(state.getValue(WoodStructureBlock.META))) key |= 1 << i;
        }
        return data.derive().with(JOINS, key).build();
    }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return getQuads(state, side, random, ModelData.EMPTY, null); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType layer) {
        if (side != null) return List.of(); // Original shouldSideBeRendered is always true: all faces are unculled.
        int type = inventory || state == null ? itemType : barrier ? 0 : WoodStructureBlock.type(state.getValue(WoodStructureBlock.META));
        Integer joinData = data.get(JOINS); int key = joinData == null ? 0 : joinData;
        if (barrier && !inventory && state != null) key |= switch (state.getValue(WoodBarrierBlock.META)) { case 5 -> 1; case 3 -> 2; case 4 -> 4; case 2 -> 8; default -> 0; };
        return cache.computeIfAbsent(new Key(type, key), k -> LegacyWoodGeometry.bake(barrier, k.type, k.joins, inventory, sprite));
    }
}
