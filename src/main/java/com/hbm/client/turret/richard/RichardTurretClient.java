package com.hbm.client.turret.richard;
import com.hbm.machine.turret.richard.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.*;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class RichardTurretClient {
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(RichardTurretContent.MENU.get(),RichardTurretScreen::new);}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(RichardTurretContent.ENTITY.get(),RichardTurretRenderer::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private RichardTurretItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?(renderer=new RichardTurretItemRenderer()):renderer;}},RichardTurretContent.ITEM.get());}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((id,model)->id.id().equals(RichardTurretContent.ITEM.getId())&&id.getVariant().equals("inventory")?new ItemModel(model):model);}
    private static final class ItemModel extends BakedModelWrapper<BakedModel>{ItemModel(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public ItemTransforms getTransforms(){return ItemTransforms.NO_TRANSFORMS;}@Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack poses,boolean left){return this;}@Override public boolean useAmbientOcclusion(){return false;}}
}
