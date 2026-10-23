package com.hbm.block.fluid;

import java.util.function.Consumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.hbm.exposure.ExposureEffects;

/** Original triggers exposed to the separately ported achievement and particle systems. */
public final class WorldFluidHooks {
    public static Consumer<Player> sulfuricAchievement = ignored -> { };
    private WorldFluidHooks() { }
    public static boolean checkForHazmat(LivingEntity entity) {
        for (String prefix : new String[]{"hazmat", "hazmat_red", "hazmat_grey", "schrabidium", "hazmat_paa", "liquidator", "euphemium", "rpa", "fau", "dns"}) {
            boolean match = true;
            String[] parts = {"helmet", "plate", "legs", "boots"};
            EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
            for (int i = 0; i < parts.length; i++) {
                String symbol = prefix.startsWith("hazmat_") && !prefix.equals("hazmat_paa")
                        ? "hazmat_" + parts[i] + prefix.substring(6) : prefix + "_" + parts[i];
                var stack = entity.getItemBySlot(slots[i]);
                if (stack.isEmpty() || !BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals("hbm:" + symbol)) { match = false; break; }
            }
            if (match) return true;
        }
        var mutation = BuiltInRegistries.MOB_EFFECT.getHolder(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("hbm", "mutation"));
        return mutation.isPresent() && entity.hasEffect(mutation.get());
    }
}
