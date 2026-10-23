package com.hbm.client.reactor.rbmk.channel;

import com.google.gson.*;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.*;
import net.neoforged.neoforge.client.model.geometry.*;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.joml.Vector3f;

/** Actual source RenderRBMKRod: culled side walls and flat HFR Cap/Inner, plus static four-level item rods. */
public record RbmkChannelGeometry(boolean inventory) implements IUnbakedGeometry<RbmkChannelGeometry> {
    public static final IGeometryLoader<RbmkChannelGeometry> LOADER = (JsonObject json, JsonDeserializationContext context) -> new RbmkChannelGeometry(json.has("inventory") && json.get("inventory").getAsBoolean());
    @Override public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> sprites, ModelState state, ItemOverrides overrides) {
        var side = sprites.apply(context.getMaterial("side")); var top = sprites.apply(context.getMaterial("top")); var inner = sprites.apply(context.getMaterial("inner"));
        var result = new SimpleBakedModel.Builder(false, true, true, context.getTransforms(), overrides).particle(side);
        var manager = Minecraft.getInstance().getResourceManager(); var element = LegacyObjModels.INSTANCE.get(manager, ResourceLocation.parse("hbm:models/rbmk/rbmk_element.obj"));
        var rods = inventory ? LegacyObjModels.INSTANCE.get(manager, ResourceLocation.parse("hbm:models/rbmk/rbmk_element_rods.obj")) : null;
        var fuel = inventory ? sprites.apply(context.getMaterial("fuel")) : null; var bakery = new FaceBakery();
        for (int floor = 0; floor < (inventory ? 4 : 1); floor++) {
            float scale = inventory ? .35F : 1F, y = inventory ? -.175F + floor * .35F : 0;
            for (var face : element.faces()) {
                if (!face.legacyPart().equals("Cap") && !face.legacyPart().equals("Inner")) continue;
                float shade = inventory ? 1 : Math.max(.45F, face.normal().y() * .3F + .7F - Math.abs(face.normal().x()) * .1F + Math.abs(face.normal().z()) * .1F);
                emit(result, element, face, face.legacyPart().equals("Cap") ? top : inner, scale, y, shade, -1);
            }
            for (var direction : List.of(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)) {
                Vector3f from = inventory ? new Vector3f(5.2F, -2.8F + floor * 5.6F, 5.2F) : new Vector3f(0, 0, 0);
                Vector3f to = inventory ? new Vector3f(10.8F, 2.8F + floor * 5.6F, 10.8F) : new Vector3f(16, 16, 16);
                var quad = bakery.bakeQuad(from, to, new BlockElementFace(direction, -1, "", new BlockFaceUV(new float[] {0,0,16,16},0)), side, direction, BlockModelRotation.X0_Y0, null, !inventory);
                if (inventory) result.addUnculledFace(quad); else result.addCulledFace(direction, quad);
            }
            if (inventory) for (var face : rods.faces()) if (face.legacyPart().equals("Rods")) emit(result, rods, face, fuel, scale, y, 1, 0xFF304825);
        }
        var baked = result.build(context.getRenderType(ResourceLocation.withDefaultNamespace("cutout")));
        return inventory ? baked : new SourceLightModel(baked);
    }
    /** Source RenderRBMKRod supplies one block-position brightness for every Cap/Inner face. */
    private static final class SourceLightModel extends BakedModelWrapper<BakedModel> {
        private static final ModelProperty<Integer> SOURCE_LIGHT = new ModelProperty<>();
        private static final int STRIDE = DefaultVertexFormat.BLOCK.getVertexSize() / Integer.BYTES;
        private static final int LIGHT = DefaultVertexFormat.BLOCK.getOffset(VertexFormatElement.UV2) / Integer.BYTES;
        private SourceLightModel(BakedModel original) { super(original); }
        @Override public ModelData getModelData(BlockAndTintGetter world, BlockPos pos, BlockState state, ModelData data) {
            return originalModel.getModelData(world, pos, state, data).derive().with(SOURCE_LIGHT, LevelRenderer.getLightColor(world, state, pos)).build();
        }
        @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return getQuads(state, side, random, ModelData.EMPTY, null); }
        @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType layer) {
            var quads = originalModel.getQuads(state, side, random, data, layer); var light = data.get(SOURCE_LIGHT);
            if (side != null || light == null || quads.isEmpty()) return quads;
            var result = new ArrayList<BakedQuad>(quads.size());
            // Only world Cap/Inner are unculled here; outer faces and inventory retain native lighting.
            for (var quad : quads) {
                var vertices = quad.getVertices().clone();
                for (int vertex = 0; vertex < 4; vertex++) vertices[vertex * STRIDE + LIGHT] = light;
                result.add(new BakedQuad(vertices, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade(), quad.hasAmbientOcclusion()));
            }
            return result;
        }
    }
    private static void emit(SimpleBakedModel.Builder result, WavefrontMesh mesh, WavefrontMesh.Face face, TextureAtlasSprite sprite, float scale, float y, float shade, int tint) {
        var source = face.corners(); var corners = source.size() == 3 ? List.of(source.get(0), source.get(1), source.get(2), source.get(2)) : source;
        if (corners.size() != 4) throw new IllegalStateException("Unexpected source RBMK polygon " + corners.size());
        var out = new QuadBakingVertexConsumer(); var normal = face.normal(); out.setSprite(sprite); out.setShade(false); out.setHasAmbientOcclusion(false); out.setDirection(Direction.getNearest(normal.x(),normal.y(),normal.z()));
        for (var corner : corners) { var position = mesh.positions().get(corner.position()); var uv = mesh.textureCoordinates().get(corner.texture()); out.addVertex(.5F+position.x()*scale,y+position.y()*scale,.5F+position.z()*scale).setColor((int)((tint>>16&255)*shade),(int)((tint>>8&255)*shade),(int)((tint&255)*shade),255).setUv(sprite.getU(uv.u()),sprite.getV(1-uv.v())).setLight(0).setNormal(normal.x(),normal.y(),normal.z()); }
        result.addUnculledFace(out.bakeQuad());
    }
}
