package com.hbm.equipment.jetpack;

import com.hbm.armor.ArmorModItem;
import com.hbm.armor.ArmorModStorage;
import com.hbm.fluid.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;

/** Actual JetpackBase/FueledBase, using the one existing armor-mod and private fluid stores. */
public final class SourceJetpackItem extends ArmorModItem implements FillableItem {
    public enum Kind { REGULAR, BRAKE, VECTOR, BOOST }
    public final Kind kind;
    public final int maxFuel;
    public SourceJetpackItem(Kind kind) { super(ArmorModStorage.CHEST, false, true, false, false); this.kind = kind; maxFuel = kind == Kind.BOOST ? 32000 : kind == Kind.VECTOR ? 16000 : 12000; }
    public static HbmFluidType fuelType() { return HbmFluids.fromName("KEROSENE"); }
    /** Original Booster uses BALEFIRE; the old static kerosene API remains for the original reserve syringe. */
    public HbmFluidType sourceFuelType() { return kind == Kind.BOOST ? HbmFluids.fromName("BALEFIRE") : fuelType(); }
    public static int getFuel(ItemStack stack) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) { stack.set(DataComponents.CUSTOM_DATA, CustomData.of(new CompoundTag())); return 0; }
        return stack.get(DataComponents.CUSTOM_DATA).copyTag().getInt("fuel");
    }
    public static void setFuel(ItemStack stack, int fuel) {
        var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag(); tag.putInt("fuel", fuel); stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    public int getMaxFill(ItemStack stack) { return maxFuel; }
    public int getLoadSpeed(ItemStack stack) { return 10; }
    @Override public boolean acceptsFluid(HbmFluidType type, ItemStack stack) { return type == sourceFuelType(); }
    @Override public int tryFill(HbmFluidType type, int amount, ItemStack stack) {
        if (!acceptsFluid(type, stack)) return amount;
        int fill = getFuel(stack), required = maxFuel - fill, transferred = Math.min(amount, required);
        // The source deliberately does not enforce getLoadSpeed here or clamp an externally overfilled NBT value.
        setFuel(stack, fill + transferred); return amount - transferred;
    }
    @Override public boolean providesFluid(HbmFluidType type, ItemStack stack) { return false; }
    @Override public int tryEmpty(HbmFluidType type, int amount, ItemStack stack) { return 0; }
    @Override public HbmFluidType getFirstFluidType(ItemStack stack) { return null; }
    @Override public int getFill(ItemStack stack) { return 0; }
    @Override public void inventoryTick(ItemStack stack, net.minecraft.world.level.Level level, net.minecraft.world.entity.Entity entity, int slot, boolean selected) {
        if (entity instanceof net.minecraft.world.entity.player.Player player && player.getItemBySlot(EquipmentSlot.CHEST) == stack) JetpackRuntime.armorTick(player,stack,this);
    }
    @Override public EquipmentSlot getEquipmentSlot(ItemStack stack) { return EquipmentSlot.CHEST; }
    @Override public boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity) { return slot == EquipmentSlot.CHEST; }
    public Component installedDescription(ItemStack stack) {
        return Component.literal("  ").append(stack.getHoverName()).append(" (")
                .append(Component.translatable("tooltip.hbm.jetpack.fuel", sourceFuelType().displayName(), getFuel(stack), maxFuel))
                .append(")").withStyle(ChatFormatting.RED);
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        String kindKey = kind.name().toLowerCase(java.util.Locale.ROOT);
        lines.add(Component.translatable("tooltip.hbm.jetpack." + kindKey + ".1"));
        if (kind != Kind.REGULAR) lines.add(Component.translatable("tooltip.hbm.jetpack." + kindKey + ".2"));
        if (kind == Kind.BRAKE) lines.add(Component.translatable("tooltip.hbm.jetpack.brake.3"));
        lines.add(Component.translatable("tooltip.hbm.jetpack.fuel", sourceFuelType().displayName(), getFuel(stack), maxFuel).withStyle(ChatFormatting.LIGHT_PURPLE));
        lines.add(Component.empty()); super.appendHoverText(stack, context, lines, flag);
        lines.add(Component.translatable("tooltip.hbm.jetpack.wearable").withStyle(ChatFormatting.GOLD));
    }
}
