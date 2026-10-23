package com.hbm.client.jetpack;

import com.hbm.armor.ArmorModStorage;
import com.hbm.equipment.jetpack.SourceJetpackItem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/** Source ModelJetPack over the actual original biped, whether worn alone or installed in plate_only. */
public final class JetpackLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private final ModelJetPack model = new ModelJetPack();
    public JetpackLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) { super(parent); }
    @Override public void render(PoseStack pose, MultiBufferSource buffers, int light, AbstractClientPlayer player,
            float swing, float amount, float partial, float age, float yaw, float pitch) {
        var chest = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean attached = !(chest.getItem() instanceof SourceJetpackItem || chest.getItem() instanceof com.hbm.equipment.boostmurk.SourceWingsItem);
        ItemStack stack = attached ? ArmorModStorage.pryMod(chest, ArmorModStorage.CHEST, player.level().registryAccess()) : chest;
        if (player.isInvisible()) return;
        if (stack.getItem() instanceof com.hbm.equipment.boostmurk.SourceWingsItem wings) {
            com.hbm.client.boostmurk.MurkWingRenderer.render(pose,buffers,light,player,stack,wings,getParentModel()); return;
        }
        if (!(stack.getItem() instanceof SourceJetpackItem item)) return;
        model.isRiding = !attached && getParentModel().riding; model.isChild = !attached && player.isBaby();
        model.onGround = attached ? 0 : getParentModel().attackTime; model.isSneak = player.isCrouching();
        float renderYaw = yaw, renderAge = age;
        if (attached) {
            float head = Mth.lerp(partial, player.yHeadRotO, player.yHeadRot), body = Mth.lerp(partial, player.yBodyRotO, player.yBodyRot);
            renderYaw = head - body; renderAge = Mth.wrapDegrees(renderYaw); pitch = player.getXRot(); swing = amount = 0;
        }
        var texture = ResourceLocation.parse("hbm:textures/models/" + switch (item.kind) {
            case REGULAR -> "jetpackred.png"; case BRAKE -> "jetpackblue.png"; case VECTOR -> "jetpackgreen.png"; case BOOST -> "jetpack.png";
        });
        var vertices = net.minecraft.client.renderer.entity.ItemRenderer.getArmorFoilBuffer(buffers, RenderType.armorCutoutNoCull(texture), stack.hasFoil());
        model.renderToBuffer(pose, vertices, light, OverlayTexture.NO_OVERLAY, -1, player, swing, amount, renderAge, renderYaw, pitch, 1F / 16F);
    }
}
