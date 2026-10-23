package com.hbm.armor.gassensor;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.block.gas.GasBlock;
import com.hbm.registry.HbmSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Source fixed147-cell gas scan, in inventory or an armor EXTRA slot; not an inferred exposure detector. */
public final class ItemModSensor extends ArmorModItem {
    public ItemModSensor() { super(ArmorModStorage.EXTRA, true, true, true, true); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.gas_tester.name"); }
    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (entity instanceof LivingEntity wearer) modUpdate(wearer, ItemStack.EMPTY);
    }
    @Override public void modUpdate(LivingEntity entity, ItemStack armor) {
        var level = entity.level();
        if (level.isClientSide || level.getGameTime() % 20 != 0) return;
        int x = Mth.floor(entity.getX()), y = Mth.floor(entity.getEyeY()), z = Mth.floor(entity.getZ());
        boolean poison = false, explosive = false;
        var pos = new BlockPos.MutableBlockPos();
        for (int i = -3; i <= 3; i++) for (int j = -1; j <= 1; j++) for (int k = -3; k <= 3; k++) {
            var block = level.getBlockState(pos.set(x + i * 2, y + j * 2, z + k * 2)).getBlock();
            if (!(block instanceof GasBlock gas)) continue;
            switch (gas.kind()) {
                case ASBESTOS, COAL, RADON, MONOXIDE, RADON_DENSE, CHLORINE -> poison = true;
                case FLAMMABLE, EXPLOSIVE -> explosive = true;
                default -> { } // Original source excludes gas_radon_tomb and gas_meltdown.
            }
        }
        if (explosive) level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), HbmSounds.get("weapon.follyAquired"), SoundSource.PLAYERS, .5F, 1F);
        else if (poison) level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), HbmSounds.get("item.techBoop"), SoundSource.PLAYERS, 2F, 1.5F);
    }
    @Override public Component installedDescription(ItemStack stack) {
        return Component.translatable("tooltip.hbm.gas_sensor.installed", stack.getHoverName()).withStyle(ChatFormatting.YELLOW);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.gas_sensor.beeps").withStyle(ChatFormatting.YELLOW));
        lines.add(Component.translatable("tooltip.hbm.gas_sensor.inventory").withStyle(ChatFormatting.YELLOW));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flags);
    }
}
