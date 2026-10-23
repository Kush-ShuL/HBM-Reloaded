package com.hbm.client.structure.toaster;

import com.hbm.block.toaster.ToasterBlock;
import com.hbm.block.toaster.ToasterItem;
import com.hbm.bomb.nuclear.LegacyVector;
import com.hbm.client.model.LegacyObjModels;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.util.TriState;

/** Actual RenderToaster/ObjUtil matrices, flat normals, icon UVs and source shading, including true3D items. */
final class ToasterModel extends BakedModelWrapper<BakedModel> {
    private static final String[] TEXTURES = {"toaster_iron", "toaster_steel", "toaster_wood"};
    private final TextureAtlasSprite sprite;
    private final List<BakedQuad> quads;
    private ToasterModel[] itemVariants;

    static ToasterModel inventory(BakedModel original, Function<Material, TextureAtlasSprite> textures) {
        var variants = new ToasterModel[3];
        for (int i = 0; i < variants.length; i++) variants[i] = new ToasterModel(original, true, i * 4, textures);
        variants[0].itemVariants = variants; return variants[0];
    }
    ToasterModel(BakedModel original, boolean inventory, int metadata, Function<Material, TextureAtlasSprite> textures) {
        super(original);
        sprite = textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.parse("hbm:block/" + TEXTURES[ToasterBlock.damageDropped(metadata)])));
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), ResourceLocation.parse("hbm:models/blocks/toaster.obj"));
        float yaw = inventory ? -.5F * (float)Math.PI : switch (metadata % 4) { case 1 -> 0; case 2 -> 1.5F * (float)Math.PI; case 3 -> (float)Math.PI; default -> .5F * (float)Math.PI; };
        var output = new ArrayList<BakedQuad>();
        for (var face : mesh.faces()) {
            if (face.corners().size() != 3) throw new IllegalStateException("Original toaster ObjUtil expects triangular faces at " + face.sourceLine());
            // The shared loader computes the original float-difference/cross-product normal; noSmooth ignores authored vertex normals.
            var n = face.normal(); var normal = LegacyVector.createVectorHelper(n.x(), n.y(), n.z()); normal.rotateAroundY(yaw);
            float nx = (float)normal.xCoord, ny = (float)normal.yCoord, nz = (float)normal.zCoord;
            float brightness = inventory ? 1F : Math.max(.45F, (ny + .7F) * .9F - (float)Math.abs(normal.xCoord) * .1F + (float)Math.abs(normal.zCoord) * .1F);
            int gray = Math.clamp((int)(brightness * 255F), 0, 255), color = 0xff000000 | gray << 16 | gray << 8 | gray;
            int packedNormal = ((byte)(nx * 127F) & 255) | ((byte)(ny * 127F) & 255) << 8 | ((byte)(nz * 127F) & 255) << 16;
            int[] vertices = new int[32];
            for (int i = 0; i < 4; i++) {
                var corner = face.corners().get(Math.min(i, 2)); var p = mesh.positions().get(corner.position()); var uv = mesh.textureCoordinates().get(corner.texture());
                var point = LegacyVector.createVectorHelper(p.x(), p.y(), p.z()); point.rotateAroundY(yaw);
                // Source world T(.5,0,.5). Inventory T(0,-.25,0)*Ry(-pi/2), compensated only for native item -.5 centering.
                float x = (float)point.xCoord + .5F, y = (float)point.yCoord + (inventory ? .25F : 0), z = (float)point.zCoord + .5F;
                int at = i * 8; vertices[at] = Float.floatToRawIntBits(x); vertices[at + 1] = Float.floatToRawIntBits(y); vertices[at + 2] = Float.floatToRawIntBits(z);
                vertices[at + 3] = color; vertices[at + 4] = Float.floatToRawIntBits(sprite.getU(uv.u())); vertices[at + 5] = Float.floatToRawIntBits(sprite.getV(1F - uv.v()));
                vertices[at + 6] = 0; vertices[at + 7] = packedNormal;
            }
            output.add(new BakedQuad(vertices, -1, Direction.getNearest(nx, ny, nz), sprite, false));
        }
        quads = List.copyOf(output);
    }
    @Override public ItemOverrides getOverrides() {
        if (itemVariants == null) return ItemOverrides.EMPTY;
        return new ItemOverrides() {
            @Override public BakedModel resolve(BakedModel original, ItemStack stack, ClientLevel world, LivingEntity entity, int seed) { return itemVariants[ToasterItem.variant(stack)]; }
        };
    }
    @Override public boolean useAmbientOcclusion() { return false; }
    @Override public TriState useAmbientOcclusion(BlockState state, ModelData data, RenderType type) { return TriState.FALSE; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public TextureAtlasSprite getParticleIcon() { return sprite; }
    @Override public TextureAtlasSprite getParticleIcon(ModelData data) { return sprite; }
    @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack poses, boolean left) {
        int hand = context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND ? -1 : 1;
        if (context.firstPerson()) { poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand * 45)); poses.scale(.4F, .4F, .4F); }
        else if (context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
            // Source custom ISBRH returns true from shouldRender3DInInventory, using the actual block-item caller.
            poses.translate(-hand / 16D, -.125, .625); poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-180)); poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(90));
            poses.translate(-hand / 16D, .4375, .0625); poses.translate(0, .1875, -.3125); poses.mulPose(com.mojang.math.Axis.XP.rotationDegrees(20));
            poses.mulPose(com.mojang.math.Axis.YP.rotationDegrees(hand * 45)); poses.scale(-.375F, -.375F, .375F);
        } else getTransforms().getTransform(context).apply(left, poses);
        return this;
    }
    @Override public List<BakedModel> getRenderPasses(ItemStack stack, boolean fabulous) { return List.of(this); }
    @Override public List<RenderType> getRenderTypes(ItemStack stack, boolean fabulous) { return List.of(RenderType.entityCutoutNoCull(TextureAtlas.LOCATION_BLOCKS)); }
    @Override public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource random, ModelData data) { return ChunkRenderTypeSet.of(RenderType.cutout()); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random) { return side == null ? quads : List.of(); }
    @Override public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType type) { return getQuads(state, side, random); }
}
