package com.hbm.client.scaffold;

import com.hbm.block.scaffold.*;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.client.model.*;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;

/** Original noSmooth scaffold OBJ, face-normal Z-then-Y rotations, exact icon UVs and explicit ObjUtil shade. */
final class ScaffoldBakedModel extends BakedModelWrapper<BakedModel> {
    private static final String[] TEXTURES = {"scaffold_steel", "scaffold_red", "scaffold_white", "scaffold_yellow"};
    private static final ResourceLocation OBJ = ResourceLocation.fromNamespaceAndPath("hbm", "models/blocks/scaffold.obj");
    private final TextureAtlasSprite sprite;
    private final List<BakedQuad> quads;
    private ItemOverrides overrides = ItemOverrides.EMPTY;
    static BakedModel world(BakedModel original, int metadata, Function<Material, TextureAtlasSprite> textures) { return new ScaffoldBakedModel(original, false, metadata, SteelScaffoldBlock.color(metadata), textures); }
    static BakedModel inventory(BakedModel original, Function<Material, TextureAtlasSprite> textures) {
        var variants = new ScaffoldBakedModel[4];
        for (int color = 0; color < 4; color++) variants[color] = new ScaffoldBakedModel(original, true, 0, color, textures);
        variants[0].overrides = new ItemOverrides() {
            @Override public BakedModel resolve(BakedModel model, ItemStack stack, ClientLevel level, LivingEntity owner, int seed) { return variants[SteelScaffoldItem.color(stack)]; }
        };
        return variants[0];
    }
    private ScaffoldBakedModel(BakedModel original, boolean inventory, int metadata, int color, Function<Material, TextureAtlasSprite> textures) {
        super(original);
        sprite = textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.fromNamespaceAndPath("hbm", "block/" + TEXTURES[color])));
        quads = bake(inventory, metadata);
    }
    @Override public boolean useAmbientOcclusion() { return false; }
    @Override public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType type) { return TriState.FALSE; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public ItemOverrides getOverrides() { return overrides; }
    @Override public TextureAtlasSprite getParticleIcon() { return sprite; }
    @Override public TextureAtlasSprite getParticleIcon(ModelData data) { return sprite; }
    @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack poses, boolean left) { getTransforms().getTransform(context).apply(left, poses); return this; }
    @Override public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) { return List.of(this); }
    @Override public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) { return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)); }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) { return ChunkRenderTypeSet.of(RenderType.cutout()); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return side == null ? quads : List.of(); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType layer) { return getQuads(state, side, random); }
    private List<BakedQuad> bake(boolean inventory, int metadata) {
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), OBJ);
        float rotation = inventory ? 0F : (float)Math.PI * -.5F, pitch = 0F;
        float ox = .5F, oy = 0F, oz = .5F;
        if (!inventory) {
            if (metadata >= 12) { pitch = (float)Math.PI * .5F; rotation = (float)-Math.PI; ox = 1F; oy = .5F; }
            else if (metadata >= 8) rotation = (float)-Math.PI;
            else if (metadata >= 4) { pitch = (float)Math.PI * .5F; oy = .5F; oz = 0F; }
        }
        // Inventory source T(0,-.5,0), rot0: native ItemRenderer supplies T(-.5,-.5,-.5),
        // so bake only +.5 on X/Z. Using world metadata0's -90-degree rotation is incorrect.
        var result = new ArrayList<BakedQuad>(mesh.faces().size());
        for (var face : mesh.faces()) {
            if (face.corners().size() != 3) throw new IllegalStateException("Original scaffold OBJ must retain its triangulated faces at line " + face.sourceLine());
            var normal = faceNormal(mesh, face);
            normal.rotateAroundZ(pitch); normal.rotateAroundY(rotation);
            float nx = (float)normal.xCoord, ny = (float)normal.yCoord, nz = (float)normal.zCoord;
            float brightness = inventory ? 1F : Math.max(.45F, (ny + .7F) * .9F - (float)Math.abs(normal.xCoord) * .1F + (float)Math.abs(normal.zCoord) * .1F);
            int gray = Math.clamp((int)(brightness * 255F), 0, 255), color = 0xff000000 | gray << 16 | gray << 8 | gray;
            int packed = ((byte)(nx * 127F) & 255) | ((byte)(ny * 127F) & 255) << 8 | ((byte)(nz * 127F) & 255) << 16;
            int[] vertices = new int[32]; int[] corners = {0, 1, 2, 2};
            for (int i = 0; i < 4; i++) {
                var corner = face.corners().get(corners[i]); var position = mesh.positions().get(corner.position()); var uv = mesh.textureCoordinates().get(corner.texture());
                var point = LegacyVector.createVectorHelper(position.x(), position.y(), position.z());
                point.rotateAroundZ(pitch); point.rotateAroundY(rotation);
                int start = i * 8;
                vertices[start] = Float.floatToRawIntBits((float)point.xCoord + ox);
                vertices[start + 1] = Float.floatToRawIntBits((float)point.yCoord + oy);
                vertices[start + 2] = Float.floatToRawIntBits((float)point.zCoord + oz);
                vertices[start + 3] = color;
                vertices[start + 4] = Float.floatToRawIntBits(sprite.getU(uv.u()));
                vertices[start + 5] = Float.floatToRawIntBits(sprite.getV(1F - uv.v()));
                vertices[start + 6] = 0; vertices[start + 7] = packed;
            }
            // The source already supplies all shade. Native directional shade/AO would darken it a second time.
            result.add(new BakedQuad(vertices, -1, Direction.getNearest(nx, ny, nz), sprite, false));
        }
        return List.copyOf(result);
    }
    private static LegacyVector faceNormal(WavefrontMesh mesh, WavefrontMesh.Face face) {
        var a = mesh.positions().get(face.corners().get(0).position()); var b = mesh.positions().get(face.corners().get(1).position()); var c = mesh.positions().get(face.corners().get(2).position());
        // HFR S_Face subtracts float Vertex values first, then old Vec3 normalizes using a float sqrt length.
        double ax = b.x() - a.x(), ay = b.y() - a.y(), az = b.z() - a.z();
        double bx = c.x() - a.x(), by = c.y() - a.y(), bz = c.z() - a.z();
        var normalized = LegacyVector.createVectorHelper(ay * bz - az * by, az * bx - ax * bz, ax * by - ay * bx).normalize();
        // S_Face stores the result in float Vertex fields before ObjUtil performs either rotation.
        return LegacyVector.createVectorHelper((float)normalized.xCoord, (float)normalized.yCoord, (float)normalized.zCoord);
    }
}
