package com.hbm.armor.mods;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.armor.powered.SourcePoweredArmor;
import com.hbm.armor.t51.T51Runtime;
import com.hbm.machine.tesla.TeslaDischarge;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.Vec3;

/** Original ItemModTesla: only an enabled full electric suit powers its real Tesla discharge. */
public final class ItemModTesla extends ArmorModItem {
    // Source transient singleton field; the old beam rendering that read it is commented out.
    public List<Vec3> targets = new ArrayList<>();
    public ItemModTesla() { super(ArmorModStorage.CHEST, false, true, false, false); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.back_tesla.name"); }
    @Override public void modUpdate(LivingEntity wearer, ItemStack armor) {
        if (wearer.level().isClientSide || !(wearer instanceof Player player)
                || !(armor.getItem() instanceof SourcePoweredArmor)
                || !(armor.getItem() instanceof ArmorItem item) || !T51Runtime.fullSet(player, true)) return;
        targets = TeslaDischarge.zap(wearer.level(), wearer.position().add(0, 1.25, 0), 5, wearer);
        if (targets != null && !targets.isEmpty() && wearer.getRandom().nextInt(5) == 0)
            armor.hurtAndBreak(1, wearer, item.getEquipmentSlot());
    }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable("tooltip.hbm.tesla_mod.installed", stack.getHoverName()).withStyle(ChatFormatting.YELLOW);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.tesla_mod.zaps").withStyle(ChatFormatting.YELLOW));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
    }
}
