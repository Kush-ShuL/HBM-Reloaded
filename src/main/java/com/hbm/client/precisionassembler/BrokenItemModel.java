package com.hbm.client.precisionassembler;
import com.hbm.machine.precisionassembler.BrokenItem;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
/** Original two icon passes: underlying raw icon and unchanged source broken overlay, never the underlying custom 3D renderer. */
final class BrokenItemModel extends BakedModelWrapper<BakedModel> {
 private final Function<Material,TextureAtlasSprite> textures;private final Map<String,BakedModel> cache=new HashMap<>();
 private final ItemOverrides overrides=new ItemOverrides(){@Override public BakedModel resolve(BakedModel model,ItemStack stack,ClientLevel level,LivingEntity owner,int seed){return cache.computeIfAbsent(BrokenItem.sourceData(stack).toString(),key->bake(stack,level,owner,seed));}};
 BrokenItemModel(BakedModel original,Function<Material,TextureAtlasSprite> textures){super(original);this.textures=textures;}
 @Override public ItemOverrides getOverrides(){return overrides;}
 private BakedModel bake(ItemStack stack,ClientLevel level,LivingEntity owner,int seed){
  var overlay=textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.parse("hbm:item/broken_item")));var original=BrokenItem.original(stack);var icon=original.isEmpty()||original.getItem() instanceof BrokenItem?overlay:Minecraft.getInstance().getItemRenderer().getModel(original,level,owner,seed).getParticleIcon();
  var builder=new SimpleBakedModel.Builder(false,false,false,getTransforms(),ItemOverrides.EMPTY).particle(overlay).item();TextureAtlasSprite[] passes={icon,overlay};
  for(int pass=0;pass<passes.length;pass++)for(var element:new ItemModelGenerator().processFrames(pass,"layer"+pass,passes[pass].contents()))for(var face:element.faces.entrySet())builder.addUnculledFace(BlockModel.bakeFace(element,face.getValue(),passes[pass],face.getKey(),BlockModelRotation.X0_Y0));
  return builder.build();
 }
}
