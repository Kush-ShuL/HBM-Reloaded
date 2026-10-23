package com.hbm.missile.custom;

import java.util.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

/** Source three ItemLootCrate groups, exact rejection-sampling order and rarity weights. */
public final class CustomMissileLootCrateItem extends Item {
    private static final Random RANDOM = new Random();
    private final String group;
    public CustomMissileLootCrateItem(String group) { super(new Properties().stacksTo(1)); this.group = group; }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.inventoryMenu.broadcastChanges();
            var choices = CustomMissileContent.loot(group);
            CustomMissilePartItem part;
            do {
                part = choices.get(RANDOM.nextInt(choices.size()));
                int weight = switch (part.definition.rarity()) { case "COMMON" -> 1; case "UNCOMMON" -> 5; case "RARE" -> 10;
                    case "EPIC" -> 25; case "LEGENDARY" -> 50; default -> 100; };
                if (weight == 1 || RANDOM.nextInt(weight) == 0) break;
            } while (true);
            // Original addItemStackToInventory fails without granting a drop when full; the crate is still consumed.
            player.getInventory().add(new ItemStack(part)); stack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
