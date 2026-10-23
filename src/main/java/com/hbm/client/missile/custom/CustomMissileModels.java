package com.hbm.client.missile.custom;

import com.google.gson.JsonParser;
import com.hbm.client.model.*;
import com.hbm.missile.custom.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import static com.hbm.missile.custom.CustomMissileParts.PartType;

/** Actual MissilePart registry and MissilePronter order, using original mesh/texture pairs and source heights. */
public final class CustomMissileModels {
    public record Part(PartType type, double height, double guiheight, ResourceLocation model, ResourceLocation texture) {}
    public record Multipart(Part warhead, Part fuselage, Part fins, Part thruster) {
        public double height() { return height(warhead, PartType.WARHEAD) + height(fuselage, PartType.FUSELAGE) + height(thruster, PartType.THRUSTER); }
        private static double height(Part part, PartType type) { return part != null && part.type() == type ? part.height() : 0; }
    }
    private static final Map<ResourceLocation, Part> PARTS = load();
    private static Map<ResourceLocation, Part> load() {
        try (var stream = CustomMissileModels.class.getResourceAsStream("/hbm/custom-missile-visuals.json")) {
            if (stream == null) throw new IllegalStateException("Missing original MissilePart registrations");
            var parts = new LinkedHashMap<ResourceLocation, Part>();
            for (var value : JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonArray()) {
                var row = value.getAsJsonObject(); parts.put(ResourceLocation.parse(row.get("id").getAsString()), new Part(
                        PartType.valueOf(row.get("type").getAsString()), row.get("height").getAsDouble(), row.get("guiheight").getAsDouble(),
                        ResourceLocation.parse(row.get("model").getAsString()), ResourceLocation.parse(row.get("texture").getAsString())));
            }
            return Map.copyOf(parts);
        } catch (java.io.IOException error) { throw new IllegalStateException("Cannot load original MissilePart models", error); }
    }
    public static Part part(Item item) { return item == null ? null : PARTS.get(BuiltInRegistries.ITEM.getKey(item)); }
    public static Multipart fromStruct(CustomMissileStruct missile) { return missile == null ? null : new Multipart(part(missile.warhead()), part(missile.fuselage()), part(missile.fins()), part(missile.thruster())); }
    public static Multipart fromSlots(Container slots) { return new Multipart(part(slots.getItem(1).getItem()), part(slots.getItem(2).getItem()), part(slots.getItem(3).getItem()), part(slots.getItem(4).getItem())); }
    public static void draw(Multipart missile, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        if (missile == null) return;
        poses.pushPose();
        try {
            if (missile.thruster() != null && missile.thruster().type() == PartType.THRUSTER) { draw(missile.thruster(), poses, buffers, light, overlay); poses.translate(0, missile.thruster().height(), 0); }
            if (missile.fuselage() != null && missile.fuselage().type() == PartType.FUSELAGE) {
                if (missile.fins() != null && missile.fins().type() == PartType.FINS) draw(missile.fins(), poses, buffers, light, overlay);
                draw(missile.fuselage(), poses, buffers, light, overlay); poses.translate(0, missile.fuselage().height(), 0);
            }
            if (missile.warhead() != null && missile.warhead().type() == PartType.WARHEAD) draw(missile.warhead(), poses, buffers, light, overlay);
        } finally { poses.popPose(); }
    }
    public static void draw(Part part, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        mesh(part.model(), part.texture(), false, poses, buffers, light, overlay);
    }
    public static void mesh(ResourceLocation model, ResourceLocation texture, boolean noCull, PoseStack poses, MultiBufferSource buffers, int light, int overlay) {
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), model);
        LegacyObjRenderer.renderAll(mesh, poses, buffers.getBuffer(noCull ? RenderType.entityCutoutNoCull(texture) : RenderType.entityCutout(texture)), light, overlay, -1, LegacyObjRenderer.Convention.FORGE);
    }
    private CustomMissileModels() {}
}
