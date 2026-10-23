package com.hbm.client.devtesters;
import com.hbm.block.devtesters.DevTestersContent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
@EventBusSubscriber(modid="hbm",bus=EventBusSubscriber.Bus.MOD,value=Dist.CLIENT)
public final class DevTestersClient {
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerBlockEntityRenderer(DevTestersContent.OBJ_ENTITY.get(),ObjTesterRenderer::new);}
    @SubscribeEvent public static void screens(RegisterMenuScreensEvent event){event.register(DevTestersContent.MENU.get(),EventTesterScreen::new);}
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private ObjTesterItemRenderer renderer;@Override public BlockEntityWithoutLevelRenderer getCustomRenderer(){return renderer==null?renderer=new ObjTesterItemRenderer():renderer;}},DevTestersContent.OBJ_ITEM.get());}
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event){event.getModels().replaceAll((location,model)->location.id().getNamespace().equals("hbm")&&location.id().getPath().equals("obj_tester")&&location.getVariant().equals("inventory")?new ItemModel(model):model);}
    private static final class ItemModel extends BakedModelWrapper<BakedModel>{ItemModel(BakedModel model){super(model);}@Override public boolean isCustomRenderer(){return true;}@Override public BakedModel applyTransform(ItemDisplayContext context,PoseStack pose,boolean left){return this;}}
    private DevTestersClient(){}
}
