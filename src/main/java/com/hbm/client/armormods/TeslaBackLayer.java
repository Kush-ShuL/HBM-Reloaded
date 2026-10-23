package com.hbm.client.armormods;

import com.hbm.armor.ArmorModStorage;
import com.hbm.armor.mods.ItemModTesla;
import com.hbm.client.model.LegacyObjRenderer;
import com.hbm.client.model.LegacyObjModels;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;

/** Source ModelBackTesla: actual biped body joint,1/16 OBJ scale, authored normals and unchanged UV. */
public final class TeslaBackLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final ResourceLocation MODEL = ResourceLocation.parse("hbm:models/armor/mod_tesla.obj");
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("hbm:textures/armor/mod_tesla.png");
    public TeslaBackLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) { super(parent); }
    @Override public void render(PoseStack poses, MultiBufferSource buffers, int light, AbstractClientPlayer player,
            float swing, float amount, float partial, float age, float yaw, float pitch) {
        for (var slot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
            var armor = player.getItemBySlot(slot); if (!ArmorModStorage.hasMods(armor)) continue;
            for (var mod : ArmorModStorage.pryMods(armor, player.level().registryAccess())) {
                if (!(mod.getItem() instanceof ItemModTesla)) continue;
                poses.pushPose();
                try {
                    getParentModel().body.translateAndRotate(poses);
                    poses.scale(1F / 16F, 1F / 16F, 1F / 16F);
                    LegacyObjRenderer.renderAll(LegacyObjModels.INSTANCE.get(Minecraft.getInstance().getResourceManager(), MODEL), poses,
                            buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light,
                            OverlayTexture.NO_OVERLAY, -1, LegacyObjRenderer.Convention.HFR);
                } finally { poses.popPose(); }
            }
        }
    }
}
