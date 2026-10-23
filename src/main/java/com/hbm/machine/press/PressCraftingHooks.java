package com.hbm.machine.press;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
/** Source SlotCraftingOutput achievement dispatch; register actual migrated achievements here. */
public final class PressCraftingHooks {
    private static final List<BiConsumer<Player,ItemStack>> CALLBACKS=new CopyOnWriteArrayList<>();
    private PressCraftingHooks() {}
    public static void register(BiConsumer<Player,ItemStack> callback) { CALLBACKS.add(callback); }
    public static void crafted(Player player,ItemStack stack) { CALLBACKS.forEach(callback->callback.accept(player,stack)); }
}
