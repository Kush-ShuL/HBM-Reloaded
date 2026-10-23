package com.hbm.client.weapon.b92;
import com.hbm.weapon.b92.*;import net.minecraft.client.model.HumanoidModel;import net.minecraft.world.InteractionHand;import net.minecraft.world.entity.LivingEntity;import net.minecraft.world.item.ItemStack;import net.neoforged.api.distmarker.Dist;import net.neoforged.bus.api.SubscribeEvent;import net.neoforged.fml.common.EventBusSubscriber;import net.neoforged.neoforge.client.event.EntityRenderersEvent;import net.neoforged.neoforge.client.extensions.common.*;
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class B92Client {
 @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event){event.registerEntityRenderer(B92Content.BEAM.get(),B92BeamRenderer::new);}
 @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event){event.registerItem(new IClientItemExtensions(){private B92ItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new B92ItemRenderer();return renderer;}@Override public HumanoidModel.ArmPose getArmPose(LivingEntity holder,InteractionHand hand,ItemStack stack){return holder.isUsingItem()&&holder.getUsedItemHand()==hand?HumanoidModel.ArmPose.BOW_AND_ARROW:HumanoidModel.ArmPose.ITEM;}@Override public boolean shouldBobAsEntity(ItemStack stack){return false;}},B92Content.GUN.get());}
 private B92Client(){}
}
