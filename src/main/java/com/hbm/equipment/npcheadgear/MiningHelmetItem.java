package com.hbm.equipment.npcheadgear;

import com.hbm.armor.LegacyArmorDamageHandler;
import com.hbm.armor.t51.T51Network;
import com.hbm.equipment.steel.SteelEquipmentContent;
import com.hbm.exposure.ExposureData;
import com.hbm.exposure.HbmExposure;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.*;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/** Actual ArmorNo9: unbreakable, old damage callbacks, coal-lung treatment and shared HUD flag. */
public final class MiningHelmetItem extends NpcHeadgearItem implements LegacyArmorDamageHandler {
    public static Consumer<Player> CLIENT_TICK = player -> {};
    public MiningHelmetItem() { super(SteelEquipmentContent.MATERIAL, 0, "no9", "textures/models/capes/capeunknown.png"); }
    @Override public boolean sourceCancelAttack(Player player, ItemStack armor, DamageSource source, float amount) {
        return !source.is(DamageTypeTags.BYPASSES_ARMOR) && amount <= .5F;
    }
    @Override public float sourceHurt(Player player, ItemStack armor, DamageSource source, float amount) {
        return source.is(DamageTypeTags.BYPASSES_ARMOR) ? amount : Math.max(0, amount - .5F);
    }
    @Override public void inventoryTick(ItemStack armor, Level level, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player) || player.getItemBySlot(EquipmentSlot.HEAD) != armor) return;
        if (level.isClientSide) {
            CLIENT_TICK.accept(player);
            return;
        }
        var data = armor.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        boolean enabled = T51Network.enabled(player), previous = data.getBoolean("isOn");
        if (enabled && !previous) level.playSound(null, player, SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1, 1.5F);
        if (!enabled && previous) level.playSound(null, player, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, .5F, 2);
        data.putBoolean("isOn", enabled);
        armor.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
        if (HbmExposure.blackLung(player) > ExposureData.MAX_BLACK_LUNG * .9)
            HbmExposure.setBlackLung(player, (int) (ExposureData.MAX_BLACK_LUNG * .9));
        if (HbmExposure.blackLung(player) >= ExposureData.MAX_BLACK_LUNG * .25)
            HbmExposure.setBlackLung(player, HbmExposure.blackLung(player) - 1);
    }
    public static boolean isOn(ItemStack stack) { return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getBoolean("isOn"); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.npc_headgear.dt", "+0.5").withStyle(ChatFormatting.BLUE));
        lines.add(Component.translatable("tooltip.hbm.npc_headgear.no9_coal").withStyle(ChatFormatting.YELLOW));
    }
}
