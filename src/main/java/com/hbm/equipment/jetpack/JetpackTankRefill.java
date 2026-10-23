package com.hbm.equipment.jetpack;

import com.hbm.armor.*;
import com.hbm.fluid.FillableItem;
import com.hbm.registry.HbmSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;

/** Exact ItemSyringe.jetpack_tank branch; shared MedicineItem invokes it only for self use. */
public final class JetpackTankRefill {
    public static void use(ItemStack tank, Player player) {
        var chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty() || player.level().isClientSide) return;
        var jetpack = chest;
        if (chest.getItem() instanceof ArmorItem && ArmorModStorage.hasMods(chest))
            jetpack = ArmorModStorage.pryMod(chest, ArmorModStorage.CHEST, player.level().registryAccess());
        if (!(jetpack.getItem() instanceof FillableItem fillable) || !fillable.acceptsFluid(SourceJetpackItem.fuelType(), jetpack)) return;
        if (fillable.tryFill(SourceJetpackItem.fuelType(), 1000, jetpack) < 1000) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), HbmSounds.get("item.jetpackTank"), SoundSource.PLAYERS, 1, 1);
            tank.shrink(1);
        }
        if (jetpack.getItem() != chest.getItem() && jetpack.getItem() instanceof ArmorModItem)
            ArmorModStorage.applyMod(chest, jetpack, player.level().registryAccess());
    }
    private JetpackTankRefill() { }
}
