package com.hbm.armor.pads;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.armor.powered.SourcePoweredArmor;
import com.hbm.armor.t51.T51Runtime;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/** Source boots-only pads; static charging is the original walking counter and private armor charge. */
public final class ItemModPads extends ArmorModItem {
    public final String name;
    public final float damageMod;
    public final boolean staticPads;
    public ItemModPads(String name, float damageMod, boolean staticPads) {
        super(ArmorModStorage.BOOTS, false, false, false, true);
        this.name = name; this.damageMod = damageMod; this.staticPads = staticPads;
    }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + name + ".name"); }
    private int reduction() { return Math.round((1F - damageMod) * 100); }
    @Override public void modDamage(LivingIncomingDamageEvent event, ItemStack armor) {
        // Original DamageSource.fall identity: do not broaden to every fall-tagged custom damage source.
        if (event.getSource().is(DamageTypes.FALL)) event.setAmount(event.getAmount() * damageMod);
    }
    @Override public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (entity.level().isClientSide || !staticPads || !(entity instanceof Player player)) return;
        if (player.walkDist == player.walkDistO || !T51Runtime.fullSet(player, false)) return;
        for (var slot : T51Runtime.ORDER) {
            var stack = player.getItemBySlot(slot);
            if (!(stack.getItem() instanceof SourcePoweredArmor powered)) continue;
            long charge = powered.sourceDrain() / 2;
            if (charge == 0) charge = powered.sourceDamageConsumption() / 40;
            powered.setCharge(stack, Math.min(powered.getMaxCharge(stack), powered.getCharge(stack) + charge));
        }
    }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable(staticPads ? "tooltip.hbm.armor_pads.installed_static" : "tooltip.hbm.armor_pads.installed",
                stack.getHoverName(), reduction()).withStyle(ChatFormatting.DARK_PURPLE);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        if (damageMod != 1F) lines.add(Component.translatable("tooltip.hbm.armor_pads.fall", reduction()).withStyle(ChatFormatting.RED));
        if (staticPads) lines.add(Component.translatable("tooltip.hbm.armor_pads.passive").withStyle(ChatFormatting.DARK_PURPLE));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
    }
}
