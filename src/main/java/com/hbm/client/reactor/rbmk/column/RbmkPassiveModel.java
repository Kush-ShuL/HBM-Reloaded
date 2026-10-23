package com.hbm.client.reactor.rbmk.column;

import com.hbm.machine.reactor.rbmk.column.RbmkPassiveBlock;
import com.hbm.multiblock.DummyableBlock;
import java.util.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import org.joml.Vector3f;

/** Source RenderRBMKReflector: ordinary opaque cubes plus the actual quarter-block top cover. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class RbmkPassiveModel extends BakedModelWrapper<BakedModel> {
    private record Cover(int type, boolean topVisible) { }
    private static final ModelProperty<Cover> COVER = new ModelProperty<>();
    private final Map<Integer, List<BakedQuad>> covers = new HashMap<>();
    public RbmkPassiveModel(BakedModel original, java.util.function.Function<Material, TextureAtlasSprite> textures, String name) {
        super(original);
        var bakery = new FaceBakery();
        for (int lid = 1; lid <= 2; lid++) for (boolean topVisible : new boolean[] {false, true}) {
            String type = lid == 1 ? "cover" : "glass";
            var quads = new ArrayList<BakedQuad>();
            for (var side : Direction.values()) {
                if (side == Direction.DOWN || side == Direction.UP && !topVisible) continue;
                var sprite = textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.parse("hbm:block/rbmk/" + name + "_" + type + (side == Direction.UP ? "_top" : "_side"))));
                float[] uv = side == Direction.UP ? new float[] {0, 0, 16, 16} : new float[] {0, 12, 16, 16};
                var face = new BlockElementFace(null, -1, "", new BlockFaceUV(uv, 0));
                quads.add(bakery.bakeQuad(new Vector3f(0, 16, 0), new Vector3f(16, 20, 16), face, sprite, side, BlockModelRotation.X0_Y0, null, true));
            }
            covers.put(lid * 2 + (topVisible ? 1 : 0), List.copyOf(quads));
        }
    }
    @SubscribeEvent public static void bake(ModelEvent.ModifyBakingResult event) {
        Map<String, RbmkPassiveModel> cache = new HashMap<>();
        event.getModels().replaceAll((key, model) -> {
            String name = key.id().getPath();
            if (!key.id().getNamespace().equals("hbm") || !Set.of("rbmk_blank", "rbmk_moderator", "rbmk_absorber", "rbmk_reflector").contains(name) || key.getVariant().equals("inventory")) return model;
            return cache.computeIfAbsent(name, ignored -> new RbmkPassiveModel(model, event.getTextureGetter(), name));
        });
    }
    @Override public ModelData getModelData(BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData data) {
        Cover cover = new Cover(0, true);
        int meta = state.getValue(DummyableBlock.LEGACY_META);
        if (meta >= 6 && meta < 12 && state.getBlock() instanceof com.hbm.machine.reactor.rbmk.column.RbmkColumnBlock block) {
            var core = block.core(world, pos);
            if (core != null) {
                int coreMeta = core.getBlockState().getValue(DummyableBlock.LEGACY_META);
                var above = pos.above(2); // Original renderer draws the extra cube at y+1 and tests its upper neighbor.
                cover = new Cover(coreMeta == 15 ? 1 : coreMeta == 13 ? 2 : 0, !world.getBlockState(above).isSolidRender(world, above));
            }
        }
        return originalModel.getModelData(world, pos, state, data).derive().with(COVER, cover).build();
    }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) {
        var original = originalModel.getRenderTypes(state, random, data); var cover = data.get(COVER);
        return cover != null && cover.type == 2 ? ChunkRenderTypeSet.union(original, ChunkRenderTypeSet.of(RenderType.cutout())) : original;
    }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return getQuads(state, side, random, ModelData.EMPTY, null); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType layer) {
        var originalLayers = layer == null ? null : originalModel.getRenderTypes(state, random, data);
        var base = layer == null || originalLayers.contains(layer) ? originalModel.getQuads(state, side, random, data, layer) : List.<BakedQuad>of();
        var cover = data.get(COVER);
        if (side != null || cover == null || cover.type == 0) return base;
        // The source glass sprites contain binary alpha: only the lid belongs to cutout.
        if (layer != null && (cover.type == 2 ? layer != RenderType.cutout() : !originalLayers.contains(layer))) return base;
        var all = new ArrayList<>(base); all.addAll(covers.get(cover.type * 2 + (cover.topVisible ? 1 : 0))); return all;
    }
}
