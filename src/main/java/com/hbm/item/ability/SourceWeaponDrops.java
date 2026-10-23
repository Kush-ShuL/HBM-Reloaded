package com.hbm.item.ability;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ResolvableProfile;

/** Shared original IWeaponAbility death drops, used by actual weapons only. */
public final class SourceWeaponDrops {
    public static void behead(LivingEntity victim) {
        // Skeleton type0/type1 map to real modern skeleton/wither skeleton classes, retaining the original20-sided roll.
        if (victim instanceof AbstractSkeleton) {
            if (victim instanceof WitherSkeleton) { if (victim.level().random.nextInt(20) == 0) victim.spawnAtLocation(new ItemStack(Items.WITHER_SKELETON_SKULL), 0F); else victim.spawnAtLocation(new ItemStack(Items.COAL, 3), 0F); }
            else victim.spawnAtLocation(new ItemStack(Items.SKELETON_SKULL), 0F);
        }
        else if (victim instanceof Zombie) victim.spawnAtLocation(new ItemStack(Items.ZOMBIE_HEAD), 0F);
        else if (victim instanceof Creeper) victim.spawnAtLocation(new ItemStack(Items.CREEPER_HEAD), 0F);
        else if (victim instanceof MagmaCube) victim.spawnAtLocation(new ItemStack(Items.MAGMA_CREAM, 3), 0F);
        else if (victim instanceof Slime) victim.spawnAtLocation(new ItemStack(Items.SLIME_BALL, 3), 0F);
        else if (victim instanceof Player player) {
            var head = new ItemStack(Items.PLAYER_HEAD);
            // Original SkullOwner was the display name; modern profile component is the actual rendered head data.
            head.set(DataComponents.PROFILE, new ResolvableProfile(new GameProfile(null, player.getDisplayName().getString())));
            victim.spawnAtLocation(head, 0F);
        } else { victim.spawnAtLocation(new ItemStack(Items.ROTTEN_FLESH, 3), 0F); victim.spawnAtLocation(new ItemStack(Items.BONE, 2), 0F); }
    }
    public static void bobble(LivingEntity victim) {
        if (!(victim instanceof Monster) || victim.getHealth() > 0) return;
        int chance = victim.getMaxHealth() > 20 ? 750 : 1000;
        if (victim.level().random.nextInt(chance) == 0) victim.spawnAtLocation(com.hbm.block.bobble.BobbleContent.stack(victim.level().random.nextInt(com.hbm.block.bobble.BobbleType.values().length - 1) + 1), 0F);
    }
    private SourceWeaponDrops() {}
}
