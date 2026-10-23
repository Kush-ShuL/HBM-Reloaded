package com.hbm.client.weapon.kinetic;

import com.hbm.client.weapon.energy.EnergyBeams;
import com.hbm.client.weapon.legendary.ItemRenderMaresleg;
import com.hbm.client.weapon.legendary.LegacyGunRenderer;
import com.hbm.client.weapon.pepperbox.GunHandPoses;
import com.hbm.client.weapon.pepperbox.SourceBulletStyles;
import com.hbm.client.weapon.pepperbox.SourceGunHudComponents;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.weapon.sedna.gun.kinetic.KineticContent;
import com.hbm.weapon.sedna.gun.kinetic.KineticGuns;
import com.hbm.weapon.sedna.gun.kinetic.XFactory12ga;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.model.BakedModelWrapper;

@EventBusSubscriber(modid="hbm", value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public final class KineticClient {
    private KineticClient() {}
    private static void add(Item item, LegacyGunRenderer renderer, RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override public BlockEntityWithoutLevelRenderer getCustomRenderer() { return renderer; }
            @Override public HumanoidModel.ArmPose getArmPose(LivingEntity owner, InteractionHand hand, ItemStack stack) { return GunHandPoses.pose(owner, hand, stack); }
        }, item);
    }
    @SubscribeEvent public static void extensions(RegisterClientExtensionsEvent event) {
        add(KineticGuns.gun_maresleg, new ItemRenderMaresleg(KineticModels.maresleg_tex), event);
        add(KineticGuns.gun_maresleg_akimbo, new ItemRenderMareslegAkimbo(), event);
        add(KineticGuns.gun_liberator, new ItemRenderLiberator(), event);
        add(KineticGuns.gun_spas12, new ItemRenderSPAS12(), event);
        add(KineticGuns.gun_autoshotgun, new ItemRenderShredder(KineticModels.shredder_tex), event);
        add(KineticGuns.gun_autoshotgun_shredder, new ItemRenderShredder(KineticModels.shredder_orig_tex), event);
    }
    @SubscribeEvent public static void setup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (var stack : KineticContent.allStacks()) {
                var gun = (com.hbm.weapon.sedna.gun.ItemGunBaseNT) stack.getItem();
                gun.getConfig(stack, 0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
            }
            KineticGuns.gun_maresleg_akimbo.getConfig(null, 0).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY_MIRROR, SourceGunHudComponents.HUD_COMPONENT_AMMO_MIRROR);
            KineticGuns.gun_maresleg_akimbo.getConfig(null, 1).hud(SourceGunHudComponents.HUD_COMPONENT_DURABILITY, SourceGunHudComponents.HUD_COMPONENT_AMMO);
            XFactory12ga.g12_sub.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
            XFactory12ga.g12_sub_slug.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
            XFactory12ga.g12_sub_flechette.setRenderer(SourceBulletStyles.RENDER_FLECHETTE_BULLET);
            XFactory12ga.g12_sub_magnum.setRenderer(SourceBulletStyles.RENDER_STANDARD_BULLET);
            XFactory12ga.g12_sub_explosive.setRenderer(SourceBulletStyles.RENDER_EXPRESS_BULLET);
            XFactory12ga.g12_sub_phosphorus.setRenderer(SourceBulletStyles.RENDER_AP_BULLET);
            for (BulletConfig beam : new BulletConfig[]{XFactory12ga.g12_shredder, XFactory12ga.g12_shredder_slug,
                    XFactory12ga.g12_shredder_flechette, XFactory12ga.g12_shredder_magnum, XFactory12ga.g12_shredder_explosive, XFactory12ga.g12_shredder_phosphorus})
                beam.setRendererBeam((entity, partial) -> EnergyBeams.laser(entity, partial, 0x15, 0x15, 0x80));
        });
    }
    @SubscribeEvent public static void models(ModelEvent.ModifyBakingResult event) {
        event.getModels().replaceAll((id, model) -> id.getVariant().equals("inventory") && KineticContent.allStacks().stream()
                .anyMatch(stack -> net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(id.id())) ? new Model(model) : model);
    }
    private static final class Model extends BakedModelWrapper<BakedModel> {
        Model(BakedModel original) { super(original); }
        @Override public boolean isCustomRenderer() { return true; }
        @Override public boolean isGui3d() { return false; }
        @Override public ItemTransforms getTransforms() { return ItemTransforms.NO_TRANSFORMS; }
        @Override public BakedModel applyTransform(ItemDisplayContext context, com.mojang.blaze3d.vertex.PoseStack pose, boolean left) { return this; }
    }
}
