package com.hbm.client.autogen;

import com.hbm.item.autogen.AutogenItem;
import com.hbm.item.autogen.ScrapsItem;
import com.hbm.material.HbmMaterials;
import com.hbm.material.NtmMaterial;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

/** Runtime material selection supports config IDs as well as the source built-in IDs. */
public final class AutogenBakedModel extends BakedModelWrapper<BakedModel> {
    private final AutogenItem item;
    private final Function<Material,TextureAtlasSprite> sprites;
    private final Map<Variant,BakedModel> variants=new ConcurrentHashMap<>();
    private final ItemOverrides overrides=new ItemOverrides() {
        @Override public BakedModel resolve(BakedModel model,ItemStack stack,ClientLevel level,LivingEntity entity,int seed) {
            return variants.computeIfAbsent(new Variant(item.materialId(stack),item instanceof ScrapsItem && ScrapsItem.liquid(stack)),AutogenBakedModel.this::bake);
        }
    };
    public AutogenBakedModel(BakedModel base,AutogenItem item,Function<Material,TextureAtlasSprite> sprites) { super(base); this.item=item; this.sprites=sprites; }
    @Override public ItemOverrides getOverrides() { return overrides; }
    private BakedModel bake(Variant variant) {
        NtmMaterial material=HbmMaterials.byId(variant.materialId()); ResourceLocation texture=AutogenSprites.texture(item.spec().texture());
        if(material!=null) {
            if(variant.liquid() && material.smeltingBehavior()==NtmMaterial.SmeltingBehavior.SMELTABLE) texture=AutogenSprites.texture("scraps_liquid");
            else if(variant.liquid() && material.smeltingBehavior()==NtmMaterial.SmeltingBehavior.ADDITIVE) texture=AutogenSprites.texture("scraps_additive");
            else if(item.spec().textureOverrides().containsKey(Integer.toString(material.id()))) texture=AutogenSprites.texture(item.spec().textureOverrides().get(Integer.toString(material.id())));
            else if(item.remaps(material)) texture=AutogenSprites.remapped(item,material);
        }
        TextureAtlasSprite sprite=sprites.apply(new Material(TextureAtlas.LOCATION_BLOCKS,texture));
        var builder=new SimpleBakedModel.Builder(false,false,false,originalModel.getTransforms(),ItemOverrides.EMPTY).particle(sprite).item();
        for(var element:new ItemModelGenerator().processFrames(0,"layer0",sprite.contents())) for(var face:element.faces.entrySet()) builder.addUnculledFace(BlockModel.bakeFace(element,face.getValue(),sprite,face.getKey(),BlockModelRotation.X0_Y0));
        return builder.build();
    }
    private record Variant(int materialId,boolean liquid) {}
}
