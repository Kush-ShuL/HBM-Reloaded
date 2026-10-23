package com.hbm.client.weapon.pepperbox;

import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.GunHands;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;
import net.neoforged.neoforge.client.IArmPoseTransformer;

/** Source aimed-bow grip, with independent arm poses when each hand holds a gun. */
public final class GunHandPoses {
    // Vanilla twoHanded=true overwrites the offhand pose before either arm is posed.
    public static final EnumProxy<HumanoidModel.ArmPose> GUN = new EnumProxy<>(HumanoidModel.ArmPose.class, false,
            (IArmPoseTransformer) GunHandPoses::transform);

    public static HumanoidModel.ArmPose pose(LivingEntity entity, InteractionHand hand, ItemStack stack) {
        boolean twoGuns = entity instanceof net.minecraft.world.entity.player.Player player && GunHands.dual(player);
        return hand == InteractionHand.MAIN_HAND && !twoGuns && !builtin(entity) ? HumanoidModel.ArmPose.BOW_AND_ARROW : GUN.getValue();
    }

    private static void transform(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
        var held = arm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
        held.zRot = 0;
        held.yRot = model.head.yRot + (arm == HumanoidArm.RIGHT ? -.1F : .1F);
        held.xRot = -(float) Math.PI / 2 + model.head.xRot;
        boolean mainArm = arm == entity.getMainArm();
        var other = entity.getItemInHand(mainArm ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        boolean builtin=builtin(entity);
        if (!other.isEmpty()&&!builtin) return;
        // A lone offhand gun gets the source supporting arm, mirrored around its grip.
        var support = arm == HumanoidArm.RIGHT ? model.leftArm : model.rightArm;
        support.zRot = 0;
        support.yRot = model.head.yRot + (arm == HumanoidArm.RIGHT ? (builtin?.1F:.5F) : (builtin?-.1F:-.5F));
        support.xRot = -(float) Math.PI / 2 + model.head.xRot;
    }
    private static boolean builtin(LivingEntity entity){var renderer=net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(entity.getMainHandItem()).getCustomRenderer();return renderer instanceof GunFirstPersonRenderer gun&&gun.isAkimbo(entity);}
    private GunHandPoses() { }
}
