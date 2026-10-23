package com.hbm.item.glitch;

import com.hbm.equipment.npcheadgear.NpcHeadgearContent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/** Only the original beta-item health and original author respawn producer, not a parallel hunger system. */
final class BetaRuntime {
    private static boolean has(Player player, net.minecraft.world.item.Item item) {
        return player.getInventory().items.stream().anyMatch(stack -> stack.is(item));
    }
    static void tick(PlayerTickEvent.Pre event) {
        var player = event.getEntity();
        if (player.level().isClientSide || !has(player, GlitchContent.BETA.get())) return;
        var food = player.getFoodData();
        if (food.getFoodLevel() > 10) player.heal(food.getFoodLevel() - 10);
        if (food.getFoodLevel() != 10) food.setFoodLevel(10);
    }
    static void respawn(PlayerEvent.PlayerRespawnEvent event) {
        var player = event.getEntity();
        if (player.level().isClientSide || !(player.getUUID().toString().equals("e82684a7-30f1-44d2-ab37-41b342be1bbd") || player.getDisplayName().getString().equals("Dr_Nostalgia"))) return;
        if (!has(player, NpcHeadgearContent.HAT.get())) player.getInventory().add(new ItemStack(NpcHeadgearContent.HAT.get()));
        if (!has(player, GlitchContent.BETA.get())) player.getInventory().add(new ItemStack(GlitchContent.BETA.get()));
    }
    private BetaRuntime() { }
}
