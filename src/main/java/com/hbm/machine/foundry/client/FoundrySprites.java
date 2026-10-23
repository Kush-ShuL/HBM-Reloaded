package com.hbm.machine.foundry.client;
import com.hbm.client.autogen.ComponentRemap;
import com.hbm.machine.anvil.AnvilContent;
import com.hbm.material.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.texture.atlas.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
/** Source dynamic slag component remap and custom mold textures from the active resource pack. */
public final class FoundrySprites implements SpriteSource {
 public static final FoundrySprites INSTANCE=new FoundrySprites();public static final MapCodec<FoundrySprites> CODEC=MapCodec.unit(INSTANCE);static SpriteSourceType TYPE;
 public static ResourceLocation slag(NtmMaterial material){return ResourceLocation.fromNamespaceAndPath("hbm",material!=null&&material.solidColorLight()!=material.solidColorDark()?"foundry/slag/"+material.id():"block/slag");}
 @Override public SpriteSourceType type(){return TYPE;}
 @Override public void run(ResourceManager resources,Output output){
  for(var mold:AnvilContent.MOLD.get().molds()){var id=ResourceLocation.fromNamespaceAndPath("hbm","item/mold_"+mold.name());resources.getResource(id.withPath(path->"textures/"+path+".png")).ifPresent(resource->output.add(id,resource));}
  var source=resources.getResource(ResourceLocation.fromNamespaceAndPath("hbm","textures/block/slag.png"));if(source.isEmpty())return;
  for(var material:HbmMaterials.ordered())if(material.solidColorLight()!=material.solidColorDark()){var id=slag(material);output.add(id,loader->{var contents=loader.loadSprite(id,source.get());if(contents==null)return null;var image=contents.getOriginalImage();for(int y=0;y<image.getHeight();y++)for(int x=0;x<image.getWidth();x++)image.setPixelRGBA(x,y,ComponentRemap.remapAbgr(image.getPixelRGBA(x,y),material.solidColorLight(),material.solidColorDark()));return contents;});}
 }
}
