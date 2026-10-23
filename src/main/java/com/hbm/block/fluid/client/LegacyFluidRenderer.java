package com.hbm.block.fluid.client;

import com.hbm.block.fluid.*;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;

/** Exact Forge 1.7.10 RenderBlockFluid geometry/UV/lighting; King Lemming. */
public final class LegacyFluidRenderer {
    private static final float LIGHT_Y_NEG = .5F, LIGHT_Y_POS = 1F, LIGHT_XZ_NEG = .8F, LIGHT_XZ_POS = .6F;
    private static final double RENDER_OFFSET = 0.0010000000474974513D;
    private final VertexConsumer vertices;
    private final BlockPos origin;
    private final Icon still, flowing;
    private final boolean renderAllFaces = false;
    private double renderMinY, renderMaxY = 1;
    private float red = 1, green = 1, blue = 1, normalX, normalY = 1, normalZ;
    private int light;
    public LegacyFluidRenderer(VertexConsumer vertices, BlockPos origin, WorldFluidSpec spec) {
        this.vertices = vertices; this.origin = origin;
        var atlas = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS);
        still = new Icon(atlas.apply(ResourceLocation.fromNamespaceAndPath("hbm", "block/" + spec.still())));
        flowing = new Icon(atlas.apply(ResourceLocation.fromNamespaceAndPath("hbm", "block/" + spec.flowing())));
    }
    private record Icon(TextureAtlasSprite sprite) {
        public float getInterpolatedU(double pixel) { return sprite.getU((float) pixel / 16F); }
        public float getInterpolatedV(double pixel) { return sprite.getV((float) pixel / 16F); }
    }
    private Icon icon(int side) { return side < 2 ? still : flowing; }
    private void setBrightness(int packedLight) { light = packedLight; }
    private void setColorOpaque_F(float r, float g, float b) { red = r; green = g; blue = b; }
    private void setNormal(float x, float y, float z) { normalX = x; normalY = y; normalZ = z; }
    private void addVertexWithUV(double x, double y, double z, double u, double v) {
        // Chunk meshing supplies the section-local origin; old fractional geometry is unchanged.
        vertices.addVertex((float) (x - origin.getX() + (origin.getX() & 15)),
                (float) (y - origin.getY() + (origin.getY() & 15)), (float) (z - origin.getZ() + (origin.getZ() & 15)))
                .setColor(red, green, blue, 1).setUv((float) u, (float) v).setLight(light).setNormal(normalX, normalY, normalZ);
    }
    private static int mixedBrightness(LegacyFluidWorld world, int x, int y, int z) {
        var level = (BlockAndTintGetter) world.access;
        int current = LevelRenderer.getLightColor(level, new BlockPos(x, y, z));
        int above = LevelRenderer.getLightColor(level, new BlockPos(x, y + 1, z));
        return Math.max(current & 255, above & 255) | Math.max(current >> 16 & 255, above >> 16 & 255) << 16;
    }
    /** Original RenderBlocks bottom face, full bounds and default UV rotation. */
    private void renderFaceYNeg(Block block, double x, double y, double z, Icon icon) {
        setNormal(0, -1, 0);
        double u0 = icon.getInterpolatedU(0), u1 = icon.getInterpolatedU(16), v0 = icon.getInterpolatedV(0), v1 = icon.getInterpolatedV(16);
        addVertexWithUV(x, y, z + 1, u0, v1); addVertexWithUV(x, y, z, u0, v0);
        addVertexWithUV(x + 1, y, z, u1, v0); addVertexWithUV(x + 1, y, z + 1, u1, v1);
    }
    private void renderFaceYPos(Block block, double x, double y, double z, Icon icon) {
        setNormal(0, 1, 0);
        double u0 = icon.getInterpolatedU(0), u1 = icon.getInterpolatedU(16), v0 = icon.getInterpolatedV(0), v1 = icon.getInterpolatedV(16);
        addVertexWithUV(x + 1, y + 1, z + 1, u1, v1); addVertexWithUV(x + 1, y + 1, z, u1, v0);
        addVertexWithUV(x, y + 1, z, u0, v0); addVertexWithUV(x, y + 1, z + 1, u0, v1);
    }
    public float getFluidHeightAverage(float[] flow)
    {
        float total = 0;
        int count = 0;
        
        float end = 0;

        for (int i = 0; i < flow.length; i++)
        {
            if (flow[i] >= 0.875F && end != 1F)
            {
            	end = flow[i];
            }

            if (flow[i] >= 0)
            {
                total += flow[i];
                count++;
            }
        }
        
        if (end == 0)
        	end = total / count;
        
        return end;
    }

    public float getFluidHeightForRender(LegacyFluidWorld world, int x, int y, int z, LegacyFluidBlock block)
    {
        if (world.getBlock(x, y, z) == block)
        {
            Block verticalOrigin = world.getBlock(x, y - block.densityDirection(), z);
            if (LegacyMaterials.of(verticalOrigin.defaultBlockState()).isLiquid() || verticalOrigin instanceof LegacyFluidBlock)
            {
                return 1;
            }

            if (world.getBlockMetadata(x, y, z) == block.getMaxRenderHeightMeta())
            {
                return 0.875F;
            }
        }
        return !world.material(x, y, z).isSolid() && world.getBlock(x, y - block.densityDirection(), z) == block ? 1 : block.getQuantaPercentage(world, x, y, z) * 0.875F;
    }

    public boolean renderWorldBlock(LegacyFluidWorld world, int x, int y, int z, Block block, LegacyFluidRenderer renderer)
    {
        if (!(block instanceof LegacyFluidBlock))
        {
            return false;
        }

        LegacyFluidRenderer tessellator = renderer;
        int color = 0xFFFFFF;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;

        LegacyFluidBlock theFluid = (LegacyFluidBlock) block;
        int bMeta = world.getBlockMetadata(x, y, z);

        boolean renderTop = world.getBlock(x, y - theFluid.densityDirection(), z) != theFluid;

        boolean renderBottom = theFluid.shouldSideBeRendered(world.access, new BlockPos(x, y + theFluid.densityDirection(), z)) && world.getBlock(x, y + theFluid.densityDirection(), z) != theFluid;

        boolean[] renderSides = new boolean[]
        {
            theFluid.shouldSideBeRendered(world.access, new BlockPos(x, y, z - 1)), 
            theFluid.shouldSideBeRendered(world.access, new BlockPos(x, y, z + 1)),
            theFluid.shouldSideBeRendered(world.access, new BlockPos(x - 1, y, z)), 
            theFluid.shouldSideBeRendered(world.access, new BlockPos(x + 1, y, z))
        };

        if (!renderTop && !renderBottom && !renderSides[0] && !renderSides[1] && !renderSides[2] && !renderSides[3])
        {
            return false;
        }
        else
        {
            boolean rendered = false;
            double heightNW, heightSW, heightSE, heightNE;
            float flow11 = getFluidHeightForRender(world, x, y, z, theFluid);

            if (flow11 != 1)
            {
                float flow00 = getFluidHeightForRender(world, x - 1, y, z - 1, theFluid);
                float flow01 = getFluidHeightForRender(world, x - 1, y, z,     theFluid);
                float flow02 = getFluidHeightForRender(world, x - 1, y, z + 1, theFluid);
                float flow10 = getFluidHeightForRender(world, x,     y, z - 1, theFluid);
                float flow12 = getFluidHeightForRender(world, x,     y, z + 1, theFluid);
                float flow20 = getFluidHeightForRender(world, x + 1, y, z - 1, theFluid);
                float flow21 = getFluidHeightForRender(world, x + 1, y, z,     theFluid);
                float flow22 = getFluidHeightForRender(world, x + 1, y, z + 1, theFluid);

                heightNW = getFluidHeightAverage(new float[]{ flow00, flow01, flow10, flow11 });
                heightSW = getFluidHeightAverage(new float[]{ flow01, flow02, flow12, flow11 });
                heightSE = getFluidHeightAverage(new float[]{ flow12, flow21, flow22, flow11 });
                heightNE = getFluidHeightAverage(new float[]{ flow10, flow20, flow21, flow11 });
            }
            else
            {
                heightNW = flow11;
                heightSW = flow11;
                heightSE = flow11;
                heightNE = flow11;
            }

            boolean rises = theFluid.densityDirection() == 1;
            if (renderer.renderAllFaces || renderTop)
            {
                rendered = true;
                Icon iconStill = icon(1);
                float flowDir = (float) theFluid.flowDirection(world.access, new BlockPos(x, y, z));

                if (flowDir > -999.0F)
                {
                    iconStill = icon(2);
                }

                heightNW -= RENDER_OFFSET;
                heightSW -= RENDER_OFFSET;
                heightSE -= RENDER_OFFSET;
                heightNE -= RENDER_OFFSET;

                double u1, u2, u3, u4, v1, v2, v3, v4;

                if (flowDir < -999.0F)
                {
                    u2 = iconStill.getInterpolatedU(0.0D);
                    v2 = iconStill.getInterpolatedV(0.0D);
                    u1 = u2;
                    v1 = iconStill.getInterpolatedV(16.0D);
                    u4 = iconStill.getInterpolatedU(16.0D);
                    v4 = v1;
                    u3 = u4;
                    v3 = v2;
                }
                else
                {
                    float xFlow = Mth.sin(flowDir) * 0.25F;
                    float zFlow = Mth.cos(flowDir) * 0.25F;
                    u2 = iconStill.getInterpolatedU(8.0F + (-zFlow - xFlow) * 16.0F);
                    v2 = iconStill.getInterpolatedV(8.0F + (-zFlow + xFlow) * 16.0F);
                    u1 = iconStill.getInterpolatedU(8.0F + (-zFlow + xFlow) * 16.0F);
                    v1 = iconStill.getInterpolatedV(8.0F + (zFlow + xFlow) * 16.0F);
                    u4 = iconStill.getInterpolatedU(8.0F + (zFlow + xFlow) * 16.0F);
                    v4 = iconStill.getInterpolatedV(8.0F + (zFlow - xFlow) * 16.0F);
                    u3 = iconStill.getInterpolatedU(8.0F + (zFlow - xFlow) * 16.0F);
                    v3 = iconStill.getInterpolatedV(8.0F + (-zFlow - xFlow) * 16.0F);
                }

                tessellator.setBrightness(mixedBrightness(world, x, y, z));
                tessellator.setColorOpaque_F(LIGHT_Y_POS * red, LIGHT_Y_POS * green, LIGHT_Y_POS * blue);

                if (!rises)
                {
                    tessellator.addVertexWithUV(x + 0, y + heightNW, z + 0, u2, v2);
                    tessellator.addVertexWithUV(x + 0, y + heightSW, z + 1, u1, v1);
                    tessellator.addVertexWithUV(x + 1, y + heightSE, z + 1, u4, v4);
                    tessellator.addVertexWithUV(x + 1, y + heightNE, z + 0, u3, v3);
                    
                    tessellator.addVertexWithUV(x + 0, y + heightNW, z + 0, u2, v2);
                    tessellator.addVertexWithUV(x + 1, y + heightNE, z + 0, u3, v3);
                    tessellator.addVertexWithUV(x + 1, y + heightSE, z + 1, u4, v4);
                    tessellator.addVertexWithUV(x + 0, y + heightSW, z + 1, u1, v1);
                }
                else
                {
                    tessellator.addVertexWithUV(x + 1, y + 1 - heightNE, z + 0, u3, v3);
                    tessellator.addVertexWithUV(x + 1, y + 1 - heightSE, z + 1, u4, v4);
                    tessellator.addVertexWithUV(x + 0, y + 1 - heightSW, z + 1, u1, v1);
                    tessellator.addVertexWithUV(x + 0, y + 1 - heightNW, z + 0, u2, v2);
                    
                    tessellator.addVertexWithUV(x + 1, y + 1 - heightNE, z + 0, u3, v3);
                    tessellator.addVertexWithUV(x + 0, y + 1 - heightNW, z + 0, u2, v2);
                    tessellator.addVertexWithUV(x + 0, y + 1 - heightSW, z + 1, u1, v1);
                    tessellator.addVertexWithUV(x + 1, y + 1 - heightSE, z + 1, u4, v4);
                }
            }

            if (renderer.renderAllFaces || renderBottom)
            {
                rendered = true;
                tessellator.setBrightness(mixedBrightness(world, x, y - 1, z));
                if (!rises)
                {
                    tessellator.setColorOpaque_F(LIGHT_Y_NEG * red, LIGHT_Y_NEG * green, LIGHT_Y_NEG * blue);
                    renderer.renderFaceYNeg(block, x, y + RENDER_OFFSET, z, icon(0));
                }
                else
                {
                    tessellator.setColorOpaque_F(LIGHT_Y_POS * red, LIGHT_Y_POS * green, LIGHT_Y_POS * blue);
                    renderer.renderFaceYPos(block, x, y + RENDER_OFFSET, z, icon(1));
                }
            }

            for (int side = 0; side < 4; ++side)
            {
                int x2 = x;
                int z2 = z;

                switch (side)
                {
                    case 0: --z2; break;
                    case 1: ++z2; break;
                    case 2: --x2; break;
                    case 3: ++x2; break;
                }

                Icon iconFlow = icon(side + 2);
                if (renderer.renderAllFaces || renderSides[side])
                {
                    rendered = true;

                    double ty1;
                    double tx1;
                    double ty2;
                    double tx2;
                    double tz1;
                    double tz2;

                    if (side == 0)
                    {
                        ty1 = heightNW;
                        ty2 = heightNE;
                        tx1 = x;
                        tx2 = x + 1;
                        tz1 = z + RENDER_OFFSET;
                        tz2 = z + RENDER_OFFSET;
                    }
                    else if (side == 1)
                    {
                        ty1 = heightSE;
                        ty2 = heightSW;
                        tx1 = x + 1;
                        tx2 = x;
                        tz1 = z + 1 - RENDER_OFFSET;
                        tz2 = z + 1 - RENDER_OFFSET;
                    }
                    else if (side == 2)
                    {
                        ty1 = heightSW;
                        ty2 = heightNW;
                        tx1 = x + RENDER_OFFSET;
                        tx2 = x + RENDER_OFFSET;
                        tz1 = z + 1;
                        tz2 = z;
                    }
                    else
                    {
                        ty1 = heightNE;
                        ty2 = heightSE;
                        tx1 = x + 1 - RENDER_OFFSET;
                        tx2 = x + 1 - RENDER_OFFSET;
                        tz1 = z;
                        tz2 = z + 1;
                    }

                    float u1Flow = iconFlow.getInterpolatedU(0.0D);
                    float u2Flow = iconFlow.getInterpolatedU(8.0D);
                    float v1Flow = iconFlow.getInterpolatedV((1.0D - ty1) * 16.0D * 0.5D);
                    float v2Flow = iconFlow.getInterpolatedV((1.0D - ty2) * 16.0D * 0.5D);
                    float v3Flow = iconFlow.getInterpolatedV(8.0D);
                    tessellator.setBrightness(mixedBrightness(world, x2, y, z2));
                    setNormal(side == 2 ? -1 : side == 3 ? 1 : 0, 0, side == 0 ? -1 : side == 1 ? 1 : 0);
                    float sideLighting = 1.0F;

                    if (side < 2)
                    {
                        sideLighting = LIGHT_XZ_NEG;
                    }
                    else
                    {
                        sideLighting = LIGHT_XZ_POS;
                    }

                    tessellator.setColorOpaque_F(LIGHT_Y_POS * sideLighting * red, LIGHT_Y_POS * sideLighting * green, LIGHT_Y_POS * sideLighting * blue);

                    if (!rises)
                    {
                        tessellator.addVertexWithUV(tx1, y + ty1, tz1, u1Flow, v1Flow);
                        tessellator.addVertexWithUV(tx2, y + ty2, tz2, u2Flow, v2Flow);
                        tessellator.addVertexWithUV(tx2, y + 0, tz2, u2Flow, v3Flow);
                        tessellator.addVertexWithUV(tx1, y + 0, tz1, u1Flow, v3Flow);

                        tessellator.addVertexWithUV(tx1, y + ty1, tz1, u1Flow, v1Flow);
                        tessellator.addVertexWithUV(tx1, y + 0, tz1, u1Flow, v3Flow);
                        tessellator.addVertexWithUV(tx2, y + 0, tz2, u2Flow, v3Flow);
                        tessellator.addVertexWithUV(tx2, y + ty2, tz2, u2Flow, v2Flow);
                    }
                    else
                    {
                        tessellator.addVertexWithUV(tx1, y + 1 - 0, tz1, u1Flow, v3Flow);
                        tessellator.addVertexWithUV(tx2, y + 1 - 0, tz2, u2Flow, v3Flow);
                        tessellator.addVertexWithUV(tx2, y + 1 - ty2, tz2, u2Flow, v2Flow);
                        tessellator.addVertexWithUV(tx1, y + 1 - ty1, tz1, u1Flow, v1Flow);

                        tessellator.addVertexWithUV(tx1, y + 1 - 0, tz1, u1Flow, v3Flow);
                        tessellator.addVertexWithUV(tx1, y + 1 - ty1, tz1, u1Flow, v1Flow);
                        tessellator.addVertexWithUV(tx2, y + 1 - ty2, tz2, u2Flow, v2Flow);
                        tessellator.addVertexWithUV(tx2, y + 1 - 0, tz2, u2Flow, v3Flow);
                    }
                }
            }
            renderer.renderMinY = 0;
            renderer.renderMaxY = 1;
            return rendered;
        }
    }

}
