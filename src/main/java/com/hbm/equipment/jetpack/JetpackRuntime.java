package com.hbm.equipment.jetpack;

import com.hbm.armor.*;
import com.hbm.equipment.jetpack.mixin.JetpackFlightAccess;
import com.hbm.registry.HbmSounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Source armor-mod living update plus actual standalone native inventory tick; no parallel FSB ticker. */
public final class JetpackRuntime {
    public static void tick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        for (var slot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
            var armor = player.getItemBySlot(slot);
            if (ArmorModStorage.hasMods(armor)) {
                var mod = ArmorModStorage.pryMod(armor, ArmorModStorage.CHEST, player.level().registryAccess());
                if (mod.getItem() instanceof SourceJetpackItem item) {
                    armorTick(player, mod, item); ArmorModStorage.applyMod(armor, mod, player.level().registryAccess());
                }
            }
        }
    }
    public static void armorTick(Player player, ItemStack stack, SourceJetpackItem item) {
        boolean enabled = JetpackNetwork.enabled(player), active = enabled && JetpackNetwork.jump(player);
        int fuel = SourceJetpackItem.getFuel(stack);
        boolean exhaust = fuel > 0 && (active || item.kind == SourceJetpackItem.Kind.BRAKE && !player.onGround() && !player.isShiftKeyDown() && enabled);
        if (exhaust && player instanceof ServerPlayer server) JetpackNetwork.exhaust(server, item.kind == SourceJetpackItem.Kind.VECTOR || item.kind == SourceJetpackItem.Kind.BOOST ? 1 : 0);
        if (fuel <= 0) return;
        var velocity = player.getDeltaMovement();
        if (item.kind == SourceJetpackItem.Kind.BOOST) {
            if (!active) return;
            if (velocity.y < .6D) velocity = velocity.add(0, .1D, 0);
            var look = player.getLookAngle();
            if ((double)(float)Math.sqrt(velocity.lengthSqr()) < 5D) {
                velocity = velocity.add(look.x * .25D, look.y * .25D, look.z * .25D);
                if (look.y > 0) player.fallDistance = 0;
            }
            player.setDeltaMovement(velocity);
            player.level().playSound(null,player.getX(),player.getY(),player.getZ(),HbmSounds.get("weapon.flamethrowerShoot"),SoundSource.PLAYERS,.25F,1F);
            useFuel(player,stack,1); resetFlight(player);
        } else if (item.kind == SourceJetpackItem.Kind.BRAKE) {
            boolean triesHover = player.isShiftKeyDown() && active, shouldHover = triesHover || !player.isShiftKeyDown();
            if (active && !triesHover) {
                player.fallDistance = 0;
                if (velocity.y < .4) velocity = velocity.add(0, .1, 0);
                player.setDeltaMovement(velocity); sound(player); useFuel(player, stack, 5);
            } else if (shouldHover && !player.onGround() && enabled) {
                player.fallDistance = 0;
                double y = velocity.y;
                if (y < -1) y += .2; else if (y < -.1) y += .1; else if (y < 0) y = 0;
                player.setDeltaMovement(velocity.x * 1.025, y, velocity.z * 1.025);
                sound(player); useFuel(player, stack, 10);
            }
            // Source Brake resets flight even when disabled/no thrust, as long as any fuel remains.
            resetFlight(player);
        } else if (active) {
            if (item.kind == SourceJetpackItem.Kind.REGULAR) player.fallDistance = 0;
            if (velocity.y < .4) velocity = velocity.add(0, .1, 0);
            if (item.kind == SourceJetpackItem.Kind.VECTOR && (float) Math.sqrt(velocity.lengthSqr()) < 2) {
                var look = player.getLookAngle(); velocity = velocity.add(look.x * .1, look.y * .1, look.z * .1);
                if (look.y > 0) player.fallDistance = 0;
            }
            player.setDeltaMovement(velocity); sound(player); useFuel(player, stack, item.kind == SourceJetpackItem.Kind.VECTOR ? 3 : 5); resetFlight(player);
        }
    }
    private static void useFuel(Player player, ItemStack stack, int rate) { if (player.tickCount % rate == 0) SourceJetpackItem.setFuel(stack, SourceJetpackItem.getFuel(stack) - 1); }
    public static void resetFlight(Player player) { if (player instanceof ServerPlayer server) ((JetpackFlightAccess) server.connection).hbm$resetJetpackFloatingTicks(0); }
    private static void sound(Player player) { player.level().playSound(null, player.getX(), player.getY(), player.getZ(), HbmSounds.get("weapon.flamethrowerShoot"), SoundSource.PLAYERS, .25F, 1.5F); }
    private JetpackRuntime() { }
}
