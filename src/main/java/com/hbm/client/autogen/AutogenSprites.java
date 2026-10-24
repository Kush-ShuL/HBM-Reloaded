package com.hbm.client.autogen;

import com.hbm.item.autogen.AutogenContent;
import com.hbm.item.autogen.AutogenItem;
import com.hbm.material.NtmMaterial;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.HashSet;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

/** Generate from the currently selected resource-pack image before mipmaps/atlas stitching. */
public final class AutogenSprites implements SpriteSource {
    public static final AutogenSprites INSTANCE=new AutogenSprites();
    public static final MapCodec<AutogenSprites> CODEC=MapCodec.unit(INSTANCE);
    static SpriteSourceType TYPE;
    private AutogenSprites() {}
    public static ResourceLocation texture(String name) { return ResourceLocation.fromNamespaceAndPath("hbm","item/"+name); }
    public static ResourceLocation remapped(AutogenItem item,NtmMaterial material) { return ResourceLocation.fromNamespaceAndPath("hbm","autogen/"+item.spec().id()+"/"+(material.id()<0?"negative_"+(-material.id()):material.id())); }
    @Override public SpriteSourceType type() { return TYPE; }
    @Override public void run(ResourceManager resources,Output output) {
        var added=new HashSet<ResourceLocation>();
        for(var holder:AutogenContent.all()) {
            AutogenItem item=holder.get(); ResourceLocation base=texture(item.spec().texture());
            var resource=resources.getResource(base.withPath(path->"textures/"+path+".png"));
            if(added.add(base)) resource.ifPresent(value->output.add(base,value));
            for(String override:item.spec().textureOverrides().values()) {
                ResourceLocation id=texture(override); if(added.add(id)) resources.getResource(id.withPath(path->"textures/"+path+".png")).ifPresent(value->output.add(id,value));
            }
            if(resource.isEmpty()) continue;
            NtmMaterial material=item.material();
            if(item.remaps(material)) {
                ResourceLocation id=remapped(item,material);
                if(!added.add(id)) continue;
                output.add(id,loader->{
                    var contents=loader.loadSprite(id,resource.get());
                    if(contents==null) return null;
                    var image=contents.getOriginalImage();
                    for(int y=0;y<image.getHeight();y++) for(int x=0;x<image.getWidth();x++) image.setPixelRGBA(x,y,ComponentRemap.remapAbgr(image.getPixelRGBA(x,y),material.solidColorLight(),material.solidColorDark()));
                    return contents;
                });
            }
        }
        for(String name:List.of("scraps_liquid","scraps_additive")) { ResourceLocation id=texture(name); if(added.add(id)) resources.getResource(id.withPath(path->"textures/"+path+".png")).ifPresent(value->output.add(id,value)); }
    }
}
