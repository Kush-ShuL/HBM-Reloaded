package com.hbm.client.missile.custom.launcher;

import com.google.gson.JsonParser;
import com.hbm.client.missile.custom.CustomMissileModels;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;

/** Exact ResourceManager declarations, including source empty-scaffold models sharing the base texture. */
public final class CustomLauncherModels {
    public record Model(ResourceLocation mesh, ResourceLocation texture) {}
    private static final Map<String, Model> MODELS = load();
    private static Map<String, Model> load() {
        try (var stream = CustomLauncherModels.class.getResourceAsStream("/hbm/custom-launcher-models.json")) {
            if (stream == null) throw new IllegalStateException("Missing actual custom launcher models");
            var models = new HashMap<String, Model>();
            JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject().entrySet().forEach(entry -> {
                var row = entry.getValue().getAsJsonObject(); models.put(entry.getKey(), new Model(ResourceLocation.parse(row.get("model").getAsString()), ResourceLocation.parse(row.get("texture").getAsString())));
            });
            return Map.copyOf(models);
        } catch (java.io.IOException error) { throw new IllegalStateException(error); }
    }
    public static WavefrontMesh mesh(String name) { return LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODELS.get(name).mesh()); }
    public static void draw(String name, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        var model = MODELS.get(name); CustomMissileModels.mesh(model.mesh(), model.texture(), false, poses, buffers, light, overlay);
    }
    private CustomLauncherModels() {}
}
