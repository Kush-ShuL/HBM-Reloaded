package com.hbm.weapon.sedna.gun;

import com.hbm.armor.ArmorModStorage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Source Trenchmaster/AoS checks against actual armor state, without creating unported armor or granting inferred set bonuses. */
public final class GunArmorPerks {
    /** The real future ArmorFSB implementations expose the same source material/enabled/noHelmet fields. */
    public interface FullSetArmor {
        Object fsbMaterialIdentity();
        boolean isArmorEnabled(ItemStack stack);
        /** Source ArmorFSB.thermal; only explicitly enabled matching full sets contribute. */
        default boolean fsbThermal() { return false; }
        /** Source ArmorFSB can enable VATS without being powered. */
        default boolean fsbVats() { return false; }
        default boolean noHelmet() { return false; }
        /** Original ArmorFSB.dashCount; only a matching enabled full set contributes its chest count. */
        default int fsbDashCount() { return 0; }
        /** Original ArmorFSB.cloneStats shares immutable effect templates; the existing player tick dispatcher applies copies. */
        default java.util.List<net.minecraft.world.effect.MobEffectInstance> fsbEffects() { return java.util.List.of(); }
        default String fsbStepSound() { return null; }
        default String fsbJumpSound() { return null; }
        default String fsbFallSound() { return null; }
        default boolean fsbHardLanding() { return false; }
        default boolean fsbHidesHat() { return false; }
    }
    public static boolean isTrenchMaster(Player player) {
        if (player == null) return false;
        var chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!matches(chest, "trenchmaster_plate") || !(chest.getItem() instanceof FullSetArmor plate)) return false;
        for (EquipmentSlot slot : plate.noHelmet() ? new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST} : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
            var stack = player.getItemBySlot(slot);
            if (!(stack.getItem() instanceof FullSetArmor armor) || armor.fsbMaterialIdentity() != plate.fsbMaterialIdentity() || !armor.isArmorEnabled(stack)) return false;
        }
        return true;
    }
    public static boolean hasAoS(Player player) {
        if (player == null) return false;
        var helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) return false;
        return matches(ArmorModStorage.pryMod(helmet, ArmorModStorage.HELMET, player.level().registryAccess()), "card_aos");
    }
    public static boolean shouldUseUp(Container inventory) {
        if (inventory instanceof GunInventory inv && (isTrenchMaster(inv.player) || hasAoS(inv.player))) return inv.player.getRandom().nextInt(3) < 2;
        return true;
    }
    private static boolean matches(ItemStack stack, String name) { return !stack.isEmpty() && BuiltInRegistries.ITEM.getOptional(ResourceLocation.fromNamespaceAndPath("hbm", name)).map(stack::is).orElse(false); }
    private GunArmorPerks() {}
}
