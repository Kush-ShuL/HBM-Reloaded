package com.hbm.machine.anvil;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
/** Actual source AchievementHandler.fire/SlotCraftingOutput dispatch when migrated definitions attach. */
public final class AnvilCraftingHooks{private static final CopyOnWriteArrayList<BiConsumer<Player,ItemStack>> LISTENERS=new CopyOnWriteArrayList<>();private AnvilCraftingHooks(){}public static void register(BiConsumer<Player,ItemStack> listener){LISTENERS.add(listener);}static void crafted(Player player,ItemStack stack){LISTENERS.forEach(listener->listener.accept(player,stack));}}
