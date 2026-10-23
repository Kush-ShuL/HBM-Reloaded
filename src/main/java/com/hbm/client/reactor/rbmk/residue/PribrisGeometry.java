package com.hbm.client.reactor.rbmk.residue;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.WavefrontMesh;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.*;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;

/** Source RenderPribris/ObjUtil on the original noSmooth HFR mesh, baked into the actual chunk. */
public record PribrisGeometry(boolean inventory) implements IUnbakedGeometry<PribrisGeometry> {
    public static final IGeometryLoader<PribrisGeometry> LOADER = (JsonObject json, JsonDeserializationContext context) ->
            new PribrisGeometry(json.has("inventory") && json.get("inventory").getAsBoolean());
    @Override public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> sprites,
            ModelState state, ItemOverrides overrides) {
        var sprite = sprites.apply(context.getMaterial("particle"));
        var result = new SimpleBakedModel.Builder(false, true, true, context.getTransforms(), overrides).particle(sprite);
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), ResourceLocation.parse("hbm:models/rbmk/debris.obj"));
        for (var face : mesh.faces()) {
            var normal = face.normal();
            float brightness = inventory ? 1 : Math.max(.45F, (normal.y() + .7F) * .9F - Math.abs(normal.x()) * .1F + Math.abs(normal.z()) * .1F);
            var corners = face.corners();
            if (corners.size() == 3) emit(result, mesh, List.of(corners.get(0), corners.get(1), corners.get(2), corners.get(2)), sprite, normal, brightness);
            else if (corners.size() == 4) emit(result, mesh, corners, sprite, normal, brightness);
            else throw new IllegalStateException("Unexpected original pribris polygon size " + corners.size());
        }
        return result.build(context.getRenderType(ResourceLocation.withDefaultNamespace("cutout")));
    }
    private static void emit(SimpleBakedModel.Builder result, WavefrontMesh mesh, List<WavefrontMesh.Corner> corners,
            TextureAtlasSprite sprite, WavefrontMesh.Normal normal, float brightness) {
        var out = new QuadBakingVertexConsumer();
        out.setSprite(sprite); out.setShade(false); out.setHasAmbientOcclusion(false);
        out.setDirection(Direction.getNearest(normal.x(), normal.y(), normal.z()));
        int color = (int)(brightness * 255);
        for (var corner : corners) {
            var position = mesh.positions().get(corner.position()); var uv = mesh.textureCoordinates().get(corner.texture());
            out.addVertex(position.x() + .5F, position.y(), position.z() + .5F).setColor(color, color, color, 255)
                    .setUv(sprite.getU(uv.u()), sprite.getV(1 - uv.v())).setLight(0).setNormal(normal.x(), normal.y(), normal.z());
        }
        result.addUnculledFace(out.bakeQuad());
    }
}
