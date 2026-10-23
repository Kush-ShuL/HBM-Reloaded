package com.hbm.item.potatos;

import com.hbm.item.battery.BatteryItem;
import com.hbm.registry.HbmSounds;
import java.util.Random;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/** Original ItemPotatos, retaining the real inherited native HE battery. */
public final class ItemPotatos extends BatteryItem {
    // Old Item.itemRand was one thread-safe java.util.Random shared by items.
    // Modern Item has no matching public RNG: all PotatOS stacks share this one,
    // without consuming player/world RNG or introducing a per-stack sequence.
    private static final Random ITEM_RANDOM = new Random();

    public ItemPotatos() { super(500_000L, 0L, 100L); }

    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (getCharge(stack) == 0) return;
        if (getTimer(stack) > 0) {
            setTimer(stack, getTimer(stack) - 1);
        } else if (entity instanceof Player player && player.getMainHandItem() == stack) {
            float pitch = (float) getCharge(stack) / (float) getMaxCharge(stack) * 0.5F + 0.5F;
            // Preserve the source two-sided timer path. Native ClientLevel ignores
            // a null excluded player; the real ServerLevel broadcasts the voice.
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    HbmSounds.get("potatos.random"), SoundSource.PLAYERS, 1F, pitch);
            setTimer(stack, 200 + ITEM_RANDOM.nextInt(100));
        }
    }

    private static int getTimer(ItemStack stack) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            return 0;
        }
        return stack.get(DataComponents.CUSTOM_DATA).copyTag().getInt("timer");
    }

    private static void setTimer(ItemStack stack, int value) {
        var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putInt("timer", value);
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
