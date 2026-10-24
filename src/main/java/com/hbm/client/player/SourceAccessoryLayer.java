package com.hbm.client.player;

import com.hbm.client.boostmurk.MurkWingRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/** Original independent author-identity checks; accessories require a bare chest and no invisibility potion. */
public final class SourceAccessoryLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private SourceAccessoryLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override public void render(PoseStack pose, MultiBufferSource buffers, int light, AbstractClientPlayer player,
            float swing, float amount, float partial, float age, float yaw, float pitch) {
        if (!player.getItemBySlot(EquipmentSlot.CHEST).isEmpty() || player.hasEffect(MobEffects.INVISIBILITY)) return;
        var model = getParentModel();
        if (SourcePlayerCosmetics.matches(player, SourcePlayerCosmetics.HBM, "HbMinecraft"))
            MurkWingRenderer.renderAuthorWings(pose, buffers, light, player, 2, model);
        if (SourcePlayerCosmetics.matches(player, SourcePlayerCosmetics.NCR, "the_NCR"))
            MurkWingRenderer.renderAuthorWings(pose, buffers, light, player, 3, model);
        if (SourcePlayerCosmetics.matches(player, SourcePlayerCosmetics.PHEO, "pheo7"))
            SourceBackAccessories.renderAxe(pose, buffers, light, player, model);
        if (SourcePlayerCosmetics.matches(player, SourcePlayerCosmetics.PEEP, "LePeeperSauvage"))
            SourceBackAccessories.renderTail(pose, buffers, light, player, model);
    }

    @EventBusSubscriber(modid = "hbm", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static final class Layers {
        @SubscribeEvent public static void add(EntityRenderersEvent.AddLayers event) {
            for (var skin : event.getSkins()) {
                net.minecraft.client.renderer.entity.player.PlayerRenderer renderer = event.getSkin(skin);
                if (renderer != null) renderer.addLayer(new SourceAccessoryLayer(renderer));
            }
        }
    }
}
