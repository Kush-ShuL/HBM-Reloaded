package com.hbm.client.crt;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.WavefrontMesh;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.joml.Vector3f;

/** Original RenderCRT/ObjUtil part draws, sprite UVs, flat normals and per-screen source brightness. */
public record DecoCRTGeometry(int metadata, boolean inventory) implements IUnbakedGeometry<DecoCRTGeometry> {
    public static final IGeometryLoader<DecoCRTGeometry> LOADER = (JsonObject json, JsonDeserializationContext context) ->
            new DecoCRTGeometry(json.get("metadata").getAsInt(), json.has("inventory") && json.get("inventory").getAsBoolean());
    @Override public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
            Function<Material, TextureAtlasSprite> sprites, ModelState state, ItemOverrides overrides) {
        TextureAtlasSprite sprite = sprites.apply(context.getMaterial("particle"));
        var builder = new SimpleBakedModel.Builder(false, true, true, context.getTransforms(), overrides).particle(sprite);
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), ResourceLocation.parse("hbm:models/blocks/crt.obj"));
        float rotation = inventory ? (float)Math.PI * -.5F : switch (metadata % 4) {
            case 1 -> 0F; case 2 -> 1.5F * (float)Math.PI; case 3 -> (float)Math.PI; default -> .5F * (float)Math.PI;
        };
        for (var face : mesh.faces()) {
            String part = face.legacyPart();
            if (!part.equals("Monitor") && !part.equals("Screen")) continue;
            Vector3f normal = rotate(new Vector3f(face.normal().x(), face.normal().y(), face.normal().z()), rotation);
            float brightness = inventory ? 1F : Math.max(.45F, normal.y * .3F + .7F - Math.abs(normal.x) * .1F + Math.abs(normal.z) * .1F);
            int light = !inventory && metadata >= 8 && part.equals("Screen") ? LightTexture.pack(15, 0) : 0;
            // The actual source OBJ is triangulated; duplicate its third corner
            // for the same quad-tessellator convention used by ObjUtil.
            if (face.corners().size() == 3) emit(builder, mesh, List.of(face.corners().get(0), face.corners().get(1), face.corners().get(2), face.corners().get(2)), sprite, normal, brightness, light, rotation);
            else if (face.corners().size() == 4) emit(builder, mesh, face.corners(), sprite, normal, brightness, light, rotation);
            else throw new IllegalStateException("Unexpected source CRT face size " + face.corners().size());
        }
        return builder.build(context.getRenderType(ResourceLocation.withDefaultNamespace("cutout")));
    }
    private static Vector3f rotate(Vector3f value, float rotation) { return value.rotateY(rotation); }
    private static void emit(SimpleBakedModel.Builder builder, WavefrontMesh mesh, List<WavefrontMesh.Corner> corners,
            TextureAtlasSprite sprite, Vector3f normal, float brightness, int light, float rotation) {
        var out = new QuadBakingVertexConsumer();
        out.setSprite(sprite);
        out.setShade(false);
        out.setHasAmbientOcclusion(false);
        out.setDirection(Direction.getNearest(normal.x, normal.y, normal.z));
        int color = (int)(brightness * 255);
        for (var corner : corners) {
            var vertex = mesh.positions().get(corner.position());
            Vector3f position = rotate(new Vector3f(vertex.x(), vertex.y(), vertex.z()), rotation).add(.5F, 0, .5F);
            var uv = mesh.textureCoordinates().get(corner.texture());
            out.addVertex(position.x, position.y, position.z).setColor(color, color, color, 255)
                    .setUv(sprite.getU(uv.u()), sprite.getV(1F - uv.v())).setLight(light).setNormal(normal.x, normal.y, normal.z);
        }
        builder.addUnculledFace(out.bakeQuad());
    }
}
