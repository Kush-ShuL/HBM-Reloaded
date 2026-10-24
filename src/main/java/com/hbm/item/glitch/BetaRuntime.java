package com.hbm.item.glitch;

import com.hbm.equipment.npcheadgear.NpcHeadgearContent;
import com.hbm.armor.HazmatRegistry;
import com.hbm.exposure.ExposureEffects;
import com.hbm.exposure.HbmExposure;
import com.hbm.registry.HbmSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Original player-tick health recovery, beta hunger, author radiation and author respawn producers. */
final class BetaRuntime {
    private static boolean has(Player player, net.minecraft.world.item.Item item) {
        return player.getInventory().items.stream().anyMatch(stack -> stack.is(item));
    }
    static void tick(PlayerTickEvent.Pre event) {
        var player = event.getEntity();
        if (player.level().isClientSide) return;
        if (!Float.isFinite(player.getHealth()) || !Float.isFinite(player.getAbsorptionAmount())) {
            player.sendSystemMessage(Component.translatable("message.hbm.health.restored"));
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    HbmSounds.get("item.syringe"), SoundSource.PLAYERS, 1F, 1F);
            player.setHealth(player.getMaxHealth());
            player.setAbsorptionAmount(0);
        }
        if (has(player, GlitchContent.BETA.get())) {
            var food = player.getFoodData();
            if (food.getFoodLevel() > 10) player.heal(food.getFoodLevel() - 10);
            if (food.getFoodLevel() != 10) food.setFoodLevel(10);
        }
        if (player.getUUID().equals(HazmatRegistry.SPECIAL_UUID)) {
            for (var nearby : player.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(3))) {
                if (nearby != player && !HbmExposure.spectatorRadiationExempt(nearby))
                    nearby.addEffect(new MobEffectInstance(ExposureEffects.RADIATION, 300, 2));
            }
        }
    }
    static void respawn(PlayerEvent.PlayerRespawnEvent event) {
        var player = event.getEntity();
        if (player.level().isClientSide || !(player.getUUID().toString().equals("e82684a7-30f1-44d2-ab37-41b342be1bbd") || player.getDisplayName().getString().equals("Dr_Nostalgia"))) return;
        if (!has(player, NpcHeadgearContent.HAT.get())) player.getInventory().add(new ItemStack(NpcHeadgearContent.HAT.get()));
        if (!has(player, GlitchContent.BETA.get())) player.getInventory().add(new ItemStack(GlitchContent.BETA.get()));
    }
    private BetaRuntime() { }
}
