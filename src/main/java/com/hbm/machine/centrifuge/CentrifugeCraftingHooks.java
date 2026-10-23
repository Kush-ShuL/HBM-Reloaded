package com.hbm.machine.centrifuge;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
/** SlotCraftingOutput dispatch point for actual future migrated achievement definitions. */
public final class CentrifugeCraftingHooks {
    private static final CopyOnWriteArrayList<BiConsumer<Player,ItemStack>> LISTENERS=new CopyOnWriteArrayList<>();
    private CentrifugeCraftingHooks() {}
    public static void register(BiConsumer<Player,ItemStack> listener) { LISTENERS.add(listener); }
    static void crafted(Player player,ItemStack stack) { LISTENERS.forEach(listener->listener.accept(player,stack)); }
}
