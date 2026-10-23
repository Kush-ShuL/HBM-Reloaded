package com.hbm.client.weapon.legacymelee;
import com.hbm.client.meteorgavel.GavelItemRenderer;
import com.hbm.weapon.legacymelee.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class LegacyMeleeClient {
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){
        event.registerItem(new IClientItemExtensions(){private LegacyMeleeItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new LegacyMeleeItemRenderer();return renderer;}},LegacyMeleeContent.SLEDGE.get(),LegacyMeleeContent.AXE.get(),LegacyMeleeContent.STOP.get(),LegacyMeleeContent.SOP.get(),LegacyMeleeContent.CHERNOBYL.get());
        event.registerItem(new IClientItemExtensions(){private LegacyMeleeItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new LegacyMeleeItemRenderer();return renderer;}@Override public boolean shouldBobAsEntity(ItemStack stack){return false;}},LegacyMeleeContent.BIG_SWORD.get());
        event.registerItem(new IClientItemExtensions(){private GavelItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new GavelItemRenderer();return renderer;}},LegacyMeleeContent.WOOD_GAVEL.get());
    }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){
        var names=java.util.Set.of("shimmer_sledge","shimmer_axe","stopsign","sopsign","chernobylsign","big_sword","wood_gavel");
        event.getModels().replaceAll((id,model)->id.getVariant().equals("inventory")&&id.id().getNamespace().equals("hbm")&&names.contains(id.id().getPath())?new SourceModel(model):model);
    }
    private static final class SourceModel extends BakedModelWrapper<BakedModel>{
        SourceModel(BakedModel original){super(original);}
        @Override public boolean isCustomRenderer(){return true;}
        @Override public boolean isGui3d(){return true;}
        @Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}
        @Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){return context==ItemDisplayContext.GUI||context==ItemDisplayContext.HEAD||context==ItemDisplayContext.NONE?originalModel.applyTransform(context,poses,left):this;}
    }
    private LegacyMeleeClient(){}
}
