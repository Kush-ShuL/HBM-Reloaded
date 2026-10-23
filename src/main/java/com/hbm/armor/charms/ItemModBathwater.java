package com.hbm.armor.charms;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

/** Source two toxic-water mods, including the original upgraded-wither / poison-tooltip mismatch. */
public final class ItemModBathwater extends ArmorModItem {
    public final boolean upgraded;
    public ItemModBathwater(boolean upgraded) { super(ArmorModStorage.EXTRA, true, true, true, true); this.upgraded = upgraded; }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item." + (upgraded ? "bathwater_mk2" : "bathwater") + ".name"); }
    private ChatFormatting color() {
        boolean first = System.currentTimeMillis() % 1000 < 500;
        return upgraded ? (first ? ChatFormatting.GREEN : ChatFormatting.YELLOW) : (first ? ChatFormatting.BLUE : ChatFormatting.LIGHT_PURPLE);
    }
    @Override public void modDamage(LivingIncomingDamageEvent event, ItemStack armor) {
        if (!event.getEntity().level().isClientSide && event.getSource().getEntity() instanceof LivingEntity attacker)
            attacker.addEffect(new MobEffectInstance(upgraded ? MobEffects.WITHER : MobEffects.POISON, 200, upgraded ? 4 : 2));
    }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable("tooltip.hbm.bathwater.installed", stack.getHoverName()).withStyle(color());
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.bathwater.poison").withStyle(color()));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
    }
}
