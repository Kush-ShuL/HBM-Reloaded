package com.hbm.client.wiaj;

import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.minecraft.client.renderer.RenderType;

/** Original PowerDetector tutorial-only full cube: six exact on/off source textures, modern native AO/culling. No registered block/item/BE. */
final class JarDetectorModel extends BakedModelWrapper<BakedModel> {
    private final TextureAtlasSprite texture;
    JarDetectorModel(BakedModel cube,int metadata){super(cube);texture=Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(ResourceLocation.parse("hbm:block/machine_detector"+(metadata==1?"":"_off")));}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random){var result=new ArrayList<BakedQuad>();for(var quad:originalModel.getQuads(state,side,random)){var old=quad.getSprite();var vertices=quad.getVertices().clone();int stride=vertices.length/4;for(int corner=0;corner<4;corner++){int offset=corner*stride;float u=Float.intBitsToFloat(vertices[offset+4]),v=Float.intBitsToFloat(vertices[offset+5]);float localU=(u-old.getU0())/(old.getU1()-old.getU0()),localV=(v-old.getV0())/(old.getV1()-old.getV0());vertices[offset+4]=Float.floatToRawIntBits(texture.getU(localU));vertices[offset+5]=Float.floatToRawIntBits(texture.getV(localV));}result.add(new BakedQuad(vertices,quad.getTintIndex(),quad.getDirection(),texture,quad.isShade()));}return result;}
    @Override public List<BakedQuad> getQuads(BlockState state,Direction side,RandomSource random,ModelData data,RenderType type){return getQuads(state,side,random);}
}
