package com.hbm.client.bomb.conventional;

import com.hbm.bomb.conventional.ConventionalContent;
import com.hbm.bomb.conventional.RemoteDetonatorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class ConventionalClient {
    private ConventionalClient() {}
    @SubscribeEvent public static void renderers(EntityRenderersEvent.RegisterRenderers event) { event.registerEntityRenderer(ConventionalContent.PRIMED.get(), PrimedExplosiveRenderer::new); event.registerBlockEntityRenderer(ConventionalContent.CHARGE_ENTITY.get(),TimedChargeRenderer::new);event.registerEntityRenderer(com.hbm.bomb.conventional.ConventionalProjectiles.MIST.get(),net.minecraft.client.renderer.entity.NoopRenderer::new);event.registerEntityRenderer(com.hbm.bomb.conventional.ConventionalProjectiles.RUBBLE.get(),context->new ConventionalProjectileRenderer<>(context,true));event.registerEntityRenderer(com.hbm.bomb.conventional.ConventionalProjectiles.SHRAPNEL.get(),context->new ConventionalProjectileRenderer<>(context,false)); }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions(){private TimedChargeItemRenderer renderer;@Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new TimedChargeItemRenderer();return renderer;}},ConventionalContent.item("charge_dynamite"),ConventionalContent.item("charge_miner"),ConventionalContent.item("charge_c4"),ConventionalContent.item("charge_semtex"));
        event.registerItem(new IClientItemExtensions() {
            private LaserDetonatorRenderer renderer;
            @Override public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer() { if(renderer==null) renderer=new LaserDetonatorRenderer(); return renderer; }
            @Override public HumanoidModel.ArmPose getArmPose(net.minecraft.world.entity.LivingEntity entity, InteractionHand hand, net.minecraft.world.item.ItemStack stack) { return HumanoidModel.ArmPose.BOW_AND_ARROW; }
        }, ConventionalContent.item("detonator_laser"));
    }
}
