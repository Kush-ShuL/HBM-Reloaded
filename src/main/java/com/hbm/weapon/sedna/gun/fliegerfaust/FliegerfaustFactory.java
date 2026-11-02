package com.hbm.weapon.sedna.gun.fliegerfaust;

import com.hbm.client.animation.bus.BusAnimation;
import com.hbm.client.animation.bus.BusAnimationKeyframe.IType;
import com.hbm.client.animation.bus.BusAnimationSequence;
import com.hbm.weapon.sedna.gun.Crosshair;
import com.hbm.weapon.sedna.gun.GunAnimation;
import com.hbm.weapon.sedna.gun.GunConfig;
import com.hbm.weapon.sedna.gun.GunInventory;
import com.hbm.weapon.sedna.gun.GunNetwork;
import com.hbm.weapon.sedna.gun.GunSounds;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.GunState;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.LambdaContext;
import com.hbm.weapon.sedna.gun.ItemGunBaseNT.WeaponQuality;
import com.hbm.weapon.sedna.gun.Receiver;
import com.hbm.weapon.sedna.gun.mags.MagazineFullReload;
import com.hbm.weapon.sedna.gun.pistols.PistolAmmo;
import net.minecraft.world.item.ItemStack;

/** Original X5863 nine-tube launcher, including its independent secondary volley and authored buses. */
public final class FliegerfaustFactory {
    private FliegerfaustFactory() { }

    public static ItemGunBaseNT create() {
        return new ItemGunBaseNT(WeaponQuality.SPECIAL, new GunConfig()
                .dura(9 * 25).draw(7).inspect(40).crosshair(Crosshair.L_CIRCUMFLEX)
                .rec(new Receiver(0)
                        .dmg(FliegerfaustSettings.DAMAGE).spread(.015F).spreadHipfire(.015F)
                        .auto(true).delay(4).reload(85).jam(60).sound("hbm:weapon.rocketFlame", 1F, 1F)
                        .mag(new MagazineFullReload(0, 9).addConfigs(FliegerfaustBullets.ROCKET))
                        .offset(1, -.0625 * 1.5, -.1875D).offsetScoped(1, -.0625 * 1.5, -.125D)
                        .setupStandardFire().recoil((stack, context) -> { }))
                .setupStandardConfiguration().ps(FliegerfaustFactory::secondary)
                .anim(FliegerfaustFactory::animation).orchestra(FliegerfaustFactory::orchestra))
                .setDefaultAmmo(PistolAmmo.ROCKET_MICRO, 9);
    }

    private static void secondary(ItemStack stack, LambdaContext context) {
        var entity = context.entity;
        var player = context.getPlayer();
        var receiver = context.config.getReceivers(stack)[0];
        int index = context.configIndex;
        GunState state = ItemGunBaseNT.getState(stack, index);
        if (state == GunState.IDLE) {
            boolean soundPlayed = false;
            for (int i = 0; i < 9; i++) {
                if (receiver.getCanFire(stack).apply(stack, context)) {
                    receiver.getOnFire(stack).accept(stack, context);
                    if (receiver.getFireSound(stack) != null && !soundPlayed) {
                        GunSounds.play(entity, receiver.getFireSound(stack), receiver.getFireVolume(stack), receiver.getFirePitch(stack));
                        soundPlayed = true;
                    }
                } else if (receiver.getDoesDryFire(stack)) {
                    ItemGunBaseNT.playAnimation(player, stack, GunAnimation.CYCLE_DRY, index);
                    ItemGunBaseNT.setState(stack, index, receiver.getRefireAfterDry(stack) ? GunState.COOLDOWN : GunState.DRAWING);
                    ItemGunBaseNT.setTimer(stack, index, receiver.getDelayAfterDryFire(stack));
                }
            }
            if (soundPlayed) {
                ItemGunBaseNT.setState(stack, index, GunState.COOLDOWN);
                ItemGunBaseNT.setTimer(stack, index, receiver.getDelayAfterFire(stack));
            }
        }
        if (state == GunState.RELOADING) ItemGunBaseNT.setReloadCancel(stack, true);
    }

    private static void orchestra(ItemStack stack, LambdaContext context) {
        var entity = context.entity;
        if (entity.level().isClientSide) return;
        GunAnimation type = ItemGunBaseNT.getLastAnim(stack, context.configIndex);
        int timer = ItemGunBaseNT.getAnimTimer(stack, context.configIndex);
        if (type == GunAnimation.CYCLE && timer == 0) GunNetwork.flash(entity);
        if (type == GunAnimation.RELOAD) {
            if (timer == 20) GunSounds.play(entity, "hbm:weapon.reload.boltOpen", 1F, .85F);
            if (timer == 62) GunSounds.play(entity, "hbm:weapon.reload.insertCanister", 1F, 1F);
            if (timer == 64) GunSounds.play(entity, "hbm:weapon.reload.boltClose", 1F, .85F);
        }
        if (type == GunAnimation.JAMMED) {
            if (timer == 25) GunSounds.play(entity, "hbm:weapon.reload.boltOpen", 1F, .85F);
            if (timer == 32) GunSounds.play(entity, "hbm:weapon.reload.boltClose", 1F, .85F);
        }
    }

    private static BusAnimation animation(ItemStack stack, GunAnimation type) {
        var player = ItemGunBaseNT.CLIENT.localPlayer();
        boolean empty = ((ItemGunBaseNT)stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0]
                .getMagazine(stack).getAmount(stack, player == null ? null : new GunInventory(player)) <= 0;
        return switch (type) {
            case EQUIP -> new BusAnimation()
                    .addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
            case CYCLE -> new BusAnimation()
                    .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -.125, 50, IType.SIN_DOWN).addPos(0, 0, 0, 150, IType.SIN_FULL));
            case RELOAD -> new BusAnimation()
                    .addBus("RELOAD", new BusAnimationSequence().addPos(75, 0, 0, 750, IType.SIN_FULL).hold(2750).addPos(0, 0, 0, 750, IType.SIN_FULL))
                    .addBus("DRUM", new BusAnimationSequence().hold(1000).addPos(0, 0, -1, 500, IType.SIN_FULL).addPos(0, -4, -1, 500, IType.SIN_UP).hold(500).addPos(0, 0, -1, 500, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_UP))
                    .addBus("DRUMROT", new BusAnimationSequence().hold(1500).addPos(-90, 0, 0, 500, IType.SIN_UP).hold(500).addPos(0, 0, 0, 500, IType.SIN_DOWN))
                    .addBus("RECOIL", new BusAnimationSequence().hold(3250).addPos(0, 0, .25, 150, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
                    .addBus("UNLOADING", new BusAnimationSequence().setPos(1, 0, 0).hold(2500).setPos(0, 0, 0));
            case JAMMED -> new BusAnimation()
                    .addBus("RELOAD", new BusAnimationSequence().hold(500).addPos(75, 0, 0, 500, IType.SIN_FULL).hold(1500).addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("DRUM", new BusAnimationSequence().hold(1250).addPos(0, 0, -1, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_UP))
                    .addBus("RECOIL", new BusAnimationSequence().hold(1750).addPos(0, 0, .25, 150, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
                    .addBus("UNLOADING", new BusAnimationSequence().setPos(1, 0, 0).hold(1750).setPos(0, 0, 0));
            case INSPECT -> new BusAnimation()
                    .addBus("RELOAD", new BusAnimationSequence().addPos(75, 0, 0, 750, IType.SIN_FULL).hold(500).addPos(0, 0, 0, 750, IType.SIN_FULL))
                    .addBus("ROCKET", new BusAnimationSequence().addPos(0, empty ? -3 : 0, 0, 0));
            default -> null;
        };
    }
}
