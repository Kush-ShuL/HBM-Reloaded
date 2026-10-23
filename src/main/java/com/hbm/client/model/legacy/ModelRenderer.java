package com.hbm.client.model.legacy;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

/**
 * Independent 1.7.10 model-part adapter. Box construction snapshots mirror and
 * texture settings in the original call order. Render pivots use the current
 * scale; box vertices retain the first compiled scale, as old display lists did.
 */
public class ModelRenderer {
    public float textureWidth = 64, textureHeight = 32;
    private int textureOffsetX, textureOffsetY;
    public float rotationPointX, rotationPointY, rotationPointZ;
    public float rotateAngleX, rotateAngleY, rotateAngleZ;
    public boolean mirror;
    public boolean showModel = true;
    public boolean isHidden;
    public List<ModelBox> cubeList = new ArrayList<>();
    public List<ModelRenderer> childModels;
    public final String boxName;
    private final ModelBase baseModel;
    public float offsetX, offsetY, offsetZ;
    private List<ModelBox> compiledBoxes;
    private float compiledScale;
    private static final float RADIANS_TO_DEGREES = 180.0F / (float) Math.PI;

    public ModelRenderer(ModelBase model, String name) {
        baseModel = model;
        model.boxList.add(this);
        boxName = name;
        setTextureSize(model.textureWidth, model.textureHeight);
    }

    public ModelRenderer(ModelBase model) { this(model, null); }

    public ModelRenderer(ModelBase model, int x, int y) {
        this(model);
        setTextureOffset(x, y);
    }

    public void addChild(ModelRenderer child) {
        if (childModels == null) childModels = new ArrayList<>();
        childModels.add(child);
    }

    public ModelRenderer setTextureOffset(int x, int y) {
        textureOffsetX = x;
        textureOffsetY = y;
        return this;
    }

    public ModelRenderer addBox(String name, float x, float y, float z, int width, int height, int depth) {
        String fullName = boxName + "." + name;
        TextureOffset offset = baseModel.getTextureOffset(fullName);
        setTextureOffset(offset.textureOffsetX, offset.textureOffsetY);
        cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, x, y, z, width, height, depth, 0).setBoxName(fullName));
        return this;
    }

    public ModelRenderer addBox(float x, float y, float z, int width, int height, int depth) {
        cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, x, y, z, width, height, depth, 0));
        return this;
    }

    public void addBox(float x, float y, float z, int width, int height, int depth, float inflation) {
        cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, x, y, z, width, height, depth, inflation));
    }

    public void setRotationPoint(float x, float y, float z) {
        rotationPointX = x; rotationPointY = y; rotationPointZ = z;
    }

    public ModelRenderer setTextureSize(int width, int height) {
        textureWidth = width;
        textureHeight = height;
        return this;
    }

    public void render(float scale) {
        if (isHidden || !showModel) return;
        compile(scale);
        LegacyModelRenderContext context = LegacyModelRenderContext.current();
        PoseStack poses = context.poses();
        poses.pushPose();
        try {
            poses.translate(offsetX, offsetY, offsetZ);
            poses.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
            rotateZYX(poses);
            drawCompiled(context);
            if (childModels != null) for (ModelRenderer child : childModels) child.render(scale);
        } finally {
            poses.popPose();
        }
    }

    /** Original special path uses Y-X-Z rotation, ignores offsets, and does not draw children. */
    public void renderWithRotation(float scale) {
        if (isHidden || !showModel) return;
        compile(scale);
        LegacyModelRenderContext context = LegacyModelRenderContext.current();
        PoseStack poses = context.poses();
        poses.pushPose();
        try {
            poses.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
            if (rotateAngleY != 0) poses.mulPose(Axis.YP.rotationDegrees(rotateAngleY * RADIANS_TO_DEGREES));
            if (rotateAngleX != 0) poses.mulPose(Axis.XP.rotationDegrees(rotateAngleX * RADIANS_TO_DEGREES));
            if (rotateAngleZ != 0) poses.mulPose(Axis.ZP.rotationDegrees(rotateAngleZ * RADIANS_TO_DEGREES));
            drawCompiled(context);
        } finally {
            poses.popPose();
        }
    }

    /** Leaves the original part transform on the caller's stack for attached equipment. */
    public void postRender(float scale) {
        if (isHidden || !showModel) return;
        compile(scale);
        PoseStack poses = LegacyModelRenderContext.current().poses();
        poses.translate(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
        rotateZYX(poses);
    }

    private void rotateZYX(PoseStack poses) {
        if (rotateAngleZ != 0) poses.mulPose(Axis.ZP.rotationDegrees(rotateAngleZ * RADIANS_TO_DEGREES));
        if (rotateAngleY != 0) poses.mulPose(Axis.YP.rotationDegrees(rotateAngleY * RADIANS_TO_DEGREES));
        if (rotateAngleX != 0) poses.mulPose(Axis.XP.rotationDegrees(rotateAngleX * RADIANS_TO_DEGREES));
    }

    private void compile(float scale) {
        if (compiledBoxes == null) {
            compiledScale = scale;
            compiledBoxes = List.copyOf(cubeList);
        }
    }

    private void drawCompiled(LegacyModelRenderContext context) {
        for (ModelBox box : compiledBoxes) box.render(context, compiledScale);
    }
}
