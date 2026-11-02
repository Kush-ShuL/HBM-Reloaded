package com.hbm.client.weapon.fliegerfaust;

import com.hbm.client.model.LegacyObjModels;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.weapon.flaregun.FlareSignalRenderer;
import com.hbm.client.weapon.pepperbox.GunBulletRenderContext;
import com.hbm.client.weapon.pepperbox.GunHandPoses;
import com.hbm.client.weapon.pepperbox.SourceGunHudComponents;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.weapon.sedna.gun.fliegerfaust.FliegerfaustBullets;
import com.hbm.weapon.sedna.gun.fliegerfaust.FliegerfaustContent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class FliegerfaustClient {
    private FliegerfaustClient() { }

    @SubscribeEvent
    public static void extensions(RegisterClientExtensionsEvent event) {
        var renderer = new ItemRenderFliegerfaust();
        event.registerItem(new IClientItemExtensions() {
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return renderer; }
            @Override public HumanoidModel.ArmPose getArmPose(LivingEntity owner, InteractionHand hand, ItemStack stack) {
                return GunHandPoses.pose(owner, hand, stack);
            }
        }, FliegerfaustContent.FLIEGERFAUST.get());
    }

    @SubscribeEvent
    public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            var stack = new ItemStack(FliegerfaustContent.FLIEGERFAUST.get());
            FliegerfaustContent.FLIEGERFAUST.get().getConfig(stack, 0)
                    .hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
            FliegerfaustBullets.ROCKET.setRenderer(FliegerfaustClient::rocket);
        });
    }

    private static void rocket(EntityBulletBaseMK4 bullet, float partial) {
        var context = GunBulletRenderContext.current();
        var poses = context.poses();
        poses.pushPose();
        poses.scale(1F, .125F, .125F);
        poses.mulPose(Axis.ZP.rotationDegrees(90));
        poses.translate(0, .25, 0);
        var mesh = LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), FliegerfaustModels.PROJECTILES);
        LegacyObjRenderer.renderOnly(mesh, poses, context.buffers().getBuffer(RenderType.entityCutout(FliegerfaustModels.GRENADE_TEXTURE)),
                context.light(), OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR, "Grenade");
        poses.popPose();

        // The entity renderer already applied yaw/pitch; the source flare cancels them to face the camera.
        poses.pushPose();
        poses.mulPose(Axis.ZN.rotationDegrees(bullet.xRotO + (bullet.getXRot() - bullet.xRotO) * partial + 180));
        poses.mulPose(Axis.YN.rotationDegrees(bullet.yRotO + (bullet.getYRot() - bullet.yRotO) * partial - 90));
        FlareSignalRenderer.sprite(poses, .375, 1F, .875F, .25F, .125F, .25F);
        poses.popPose();
    }

    @SubscribeEvent
    public static void models(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((id, model) -> id.getVariant().equals("inventory")
                && id.id().equals(ResourceLocation.parse("hbm:gun_fliegerfaust")) ? new Model(model) : model);
    }

    private static final class Model extends BakedModelWrapper<BakedModel> {
        Model(BakedModel model) { super(model); }
        @Override public boolean isCustomRenderer() { return true; }
        @Override public boolean isGui3d() { return false; }
        @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
        @Override public BakedModel applyTransform(ItemDisplayContext context, PoseStack pose, boolean left) { return this; }
    }
}
