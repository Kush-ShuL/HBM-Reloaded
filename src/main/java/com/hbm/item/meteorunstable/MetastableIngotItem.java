package com.hbm.item.meteorunstable;

import com.hbm.bomb.nuclear.NuclearExplosionMK5;
import com.hbm.damage.HbmDamageSources;
import com.hbm.hazard.HbmHazards;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/** ItemUnstable(350,200): only damage0 counts down, and the complete stack vanishes at exactly200. */
public final class MetastableIngotItem extends Item {
    private static final int RADIUS = 350, TIMER = 200;
    public MetastableIngotItem() { super(new Properties().component(HbmHazards.LEGACY_METADATA.get(), 0)); }
    public static int metadata(ItemStack stack) { return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0); }
    public static int icon(ItemStack stack) { int meta = metadata(stack); return meta >= 1 && meta <= 3 ? meta : 0; }
    private static int timer(ItemStack stack) {
        // Source getTimer creates the NBT compound even when invoked by a tooltip before the first tick.
        if (!stack.has(DataComponents.CUSTOM_DATA)) stack.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        return stack.get(DataComponents.CUSTOM_DATA).copyTag().getInt("timer");
    }
    private static void timer(ItemStack stack, int value) {
        var data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        data.putInt("timer", value); stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
    }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity holder, int slot, boolean selected) {
        if (metadata(stack) != 0) return;
        timer(stack, timer(stack) + 1);
        // Source equality is intentional: existing NBT >200 does not restart or immediately explode.
        if (timer(stack) == TIMER && !level.isClientSide) {
            level.addFreshEntity(NuclearExplosionMK5.statFac(level, RADIUS, holder.getX(), holder.getY(), holder.getZ()));
            level.playSound(null, holder.getX(), holder.getY(), holder.getZ(), HbmSounds.get("entity.oldExplosion"), SoundSource.AMBIENT, 1F, 1F);
            holder.hurt(HbmDamageSources.source(level, HbmDamageSources.nuclearBlast), 10000F);
            stack.setCount(0);
        }
    }
    @Override public Component getName(ItemStack stack) {
        return switch (metadata(stack)) {
            case 1 -> Component.translatable("item.hbm.metastable_ingot.elements");
            case 2 -> Component.translatable("item.hbm.metastable_ingot.arsenic");
            case 3 -> Component.translatable("item.hbm.metastable_ingot.vault");
            default -> Component.translatable("item.ingot_u238m2.name");
        };
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (metadata(stack) == 0) tooltip.add(Component.translatable("tooltip.hbm.metastable_ingot.decay", timer(stack) * 100 / TIMER));
    }
}
