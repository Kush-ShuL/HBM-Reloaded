package com.hbm.item.dosimeter;

import com.hbm.exposure.HbmExposure;
import com.hbm.geiger.GeigerReadings;
import com.hbm.registry.HbmSounds;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** X5846 ItemDosimeter: its own click distribution and capped environmental readout. */
public final class DosimeterItem extends Item {
    // The source singleton owns this RNG; neither player nor world RNG is substituted.
    private final Random random = new Random();

    public DosimeterItem() { super(new Properties().stacksTo(1)); }

    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof LivingEntity living) || !(level instanceof ServerLevel)) return;
        float radiation = HbmExposure.data(living).radBuffer();
        if (level.getGameTime() % 5 != 0) return;

        if (radiation > 1E-5) {
            var candidates = new ArrayList<Integer>();
            if (radiation < 0.5) candidates.add(0);
            if (radiation < 1) candidates.add(1);
            if (radiation >= 0.5 && radiation < 2) candidates.add(2);
            if (radiation >= 1 && radiation >= 2) candidates.add(3);
            // Even a one-entry list consumes the source nextInt(1).
            int sound = candidates.get(random.nextInt(candidates.size()));
            if (sound > 0) click(level, entity, sound);
        } else if (random.nextInt(100) == 0) {
            click(level, entity, 1);
        }
    }

    private static void click(Level level, Entity entity, int sound) {
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                HbmSounds.get("item.geiger" + sound), SoundSource.PLAYERS, 1F, 1F);
    }

    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    HbmSounds.get("item.techBoop"), SoundSource.PLAYERS, 1F, 1F);
            print(player);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    /** Original ContaminationUtil.printDosimeterData, with the existing rate-color mapping. */
    private static void print(Player player) {
        double environment = ((int) (HbmExposure.data(player).radBuffer() * 10D)) / 10D;
        boolean limit = false;
        if (environment > 3.6D) {
            environment = 3.6D;
            limit = true;
        }
        player.displayClientMessage(Component.literal("===== ☢ ")
                .append(Component.translatable("geiger.title.dosimeter"))
                .append(" ☢ =====").withStyle(ChatFormatting.GOLD), false);
        player.displayClientMessage(Component.translatable("geiger.envRad").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(" " + (limit ? ">" : "") + environment + " RAD/s")
                        .withStyle(GeigerReadings.rateColor(environment))), false);
    }
}
