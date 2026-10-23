package com.hbm.client.model.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Minimal source-compatible model owner with the original ModelBase defaults. */
public abstract class ModelBase {
    public float onGround;
    public boolean isRiding;
    public List<ModelRenderer> boxList = new ArrayList<>();
    public boolean isChild = true;
    public int textureWidth = 64, textureHeight = 32;
    private final Map<String, TextureOffset> textureOffsets = new HashMap<>();

    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scaleFactor) { }

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks,
                                  float netHeadYaw, float headPitch, float scaleFactor, Entity entity) { }

    public void setLivingAnimations(LivingEntity entity, float limbSwing, float limbSwingAmount, float partialTick) { }

    public ModelRenderer getRandomModelBox(Random random) {
        return boxList.get(random.nextInt(boxList.size()));
    }

    protected void setTextureOffset(String name, int x, int y) {
        textureOffsets.put(name, new TextureOffset(x, y));
    }

    public TextureOffset getTextureOffset(String name) {
        return textureOffsets.get(name);
    }

    /** Scale is the legacy model-unit scale (commonly 1/16), never an implicit modern conversion. */
    public final void renderToBuffer(PoseStack poses, VertexConsumer vertices, int light, int overlay, int argb, float scale) {
        renderToBuffer(poses, vertices, light, overlay, argb, null, 0, 0, 0, 0, 0, scale);
    }

    public final void renderToBuffer(PoseStack poses, VertexConsumer vertices, int light, int overlay, int argb,
                                     Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                                     float netHeadYaw, float headPitch, float scale) {
        try (var ignored = LegacyModelRenderContext.push(poses, vertices, light, overlay, argb)) {
            render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }
}
