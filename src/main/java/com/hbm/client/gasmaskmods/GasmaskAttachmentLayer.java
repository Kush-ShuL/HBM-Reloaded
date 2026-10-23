package com.hbm.client.gasmaskmods;

import com.hbm.armor.ArmorModStorage;
import com.hbm.armor.gasmaskmods.ItemModGasmask;
import com.hbm.client.armor.LegacyHazmatArmorModel;
import com.hbm.client.armor.LegacyM65Model;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;

/** Source ItemModGasmask.modRender: the same real M65 model, authored scale/UV/filter and player head angles. */
public final class GasmaskAttachmentLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final EquipmentSlot[] ORDER = {EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD};
    private static final ResourceLocation MASK = ResourceLocation.parse("hbm:textures/models/modelm65.png");
    private static final ResourceLocation MONO = ResourceLocation.parse("hbm:textures/models/modelm65mono.png");
    private final LegacyHazmatArmorModel model = new LegacyHazmatArmorModel(new LegacyM65Model());
    public GasmaskAttachmentLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) { super(parent); }
    @Override public void render(PoseStack poses, MultiBufferSource buffers, int light, AbstractClientPlayer player,
            float swing, float amount, float partial, float age, float yaw, float pitch) {
        for (var slot : ORDER) {
            var armor = player.getItemBySlot(slot);
            if (!ArmorModStorage.hasMods(armor)) continue;
            for (var mod : ArmorModStorage.pryMods(armor, player.registryAccess())) {
                if (!(mod.getItem() instanceof ItemModGasmask item)) continue;
                float head = Mth.lerp(partial, player.yHeadRotO, player.yHeadRot);
                float body = Mth.lerp(partial, player.yBodyRotO, player.yBodyRot);
                model.configure(player, EquipmentSlot.HEAD, getParentModel());
                model.animate(0, 0, Mth.wrapDegrees(head - body), head - body, player.getXRot());
                // LegacyM65Model owns the original18/16,1.01 scale and head/crouch/child transforms.
                poses.pushPose();
                try { model.renderToBuffer(poses, buffers.getBuffer(RenderType.armorCutoutNoCull(item.mono ? MONO : MASK)),
                        light, OverlayTexture.NO_OVERLAY, -1); }
                finally { poses.popPose(); }
            }
        }
    }
}
