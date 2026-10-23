package com.hbm.client.sellafield;
import com.hbm.block.sellafield.SellafieldContent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
final class SellafieldItemModel extends BakedModelWrapper<BakedModel> {
    private final BakedModel[][] models;
    private final ItemOverrides overrides=new ItemOverrides(){@Override public BakedModel resolve(BakedModel original,ItemStack stack,ClientLevel level,LivingEntity entity,int seed){return models[Math.abs(SellafieldContent.metadata(stack))%6][0];}};
    SellafieldItemModel(BakedModel original,BakedModel[][] models){super(original);this.models=models;}
    @Override public ItemOverrides getOverrides(){return overrides;}
}
