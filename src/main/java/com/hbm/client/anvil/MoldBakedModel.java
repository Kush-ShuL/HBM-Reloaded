package com.hbm.client.anvil;
import com.hbm.machine.anvil.*;
import java.util.*;
import java.util.function.Function;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
/** Stable mold IDs select source artwork; runtime custom-shape mold IDs use their configured resource names. */
final class MoldBakedModel extends BakedModelWrapper<BakedModel>{
    private final Function<Material,TextureAtlasSprite> textures;private final Map<Integer,BakedModel> variants=new HashMap<>();
    private final ItemOverrides overrides=new ItemOverrides(){@Override public BakedModel resolve(BakedModel model,ItemStack stack,ClientLevel level,LivingEntity owner,int seed){var mold=AnvilContent.MOLD.get().mold(stack);return variants.computeIfAbsent(mold.id(),ignored->bake(mold));}};
    MoldBakedModel(BakedModel original,Function<Material,TextureAtlasSprite> textures){super(original);this.textures=textures;}
    @Override public ItemOverrides getOverrides(){return overrides;}
    private BakedModel bake(MoldItem.Mold mold){var sprite=textures.apply(new Material(TextureAtlas.LOCATION_BLOCKS,ResourceLocation.fromNamespaceAndPath("hbm","item/mold_"+mold.name())));var builder=new SimpleBakedModel.Builder(false,false,false,getTransforms(),ItemOverrides.EMPTY).particle(sprite).item();for(var element:new ItemModelGenerator().processFrames(0,"layer0",sprite.contents()))for(var face:element.faces.entrySet())builder.addUnculledFace(BlockModel.bakeFace(element,face.getValue(),sprite,face.getKey(),BlockModelRotation.X0_Y0));return builder.build();}
}
