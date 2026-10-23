package com.hbm.item.diagnostic;

import com.hbm.exposure.HbmExposure;
import com.hbm.registry.HbmSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Original ItemDigammaDiagnostic and ContaminationUtil.printDiagnosticData, without a second exposure state. */
public final class DigammaDiagnosticItem extends Item {
    public DigammaDiagnosticItem() { super(new Properties().stacksTo(1)); }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    HbmSounds.get("item.techBoop"), SoundSource.PLAYERS, 1F, 1F);
            double digamma = ((int) (HbmExposure.digamma(player) * 100)) / 100D;
            double influence = ((int) ((1D - Math.pow(0.5, digamma)) * 10000)) / 100D;
            player.displayClientMessage(Component.literal("===== Ϝ ")
                    .append(Component.translatable("digamma.title")).append(" Ϝ =====")
                    .withStyle(ChatFormatting.DARK_PURPLE), false);
            line(player, "digamma.playerDigamma", digamma + " DRX", ChatFormatting.RED);
            line(player, "digamma.playerHealth", influence + "%", ChatFormatting.RED);
            // The source deliberately reports N/A, even when protection equipment exists.
            line(player, "digamma.playerRes", "N/A", ChatFormatting.BLUE);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    private static void line(Player player, String key, String value, ChatFormatting color) {
        player.displayClientMessage(Component.translatable(key)
                .append(Component.literal(" " + value).withStyle(color))
                .withStyle(ChatFormatting.LIGHT_PURPLE), false);
    }
}
