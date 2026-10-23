package com.hbm.item.toolability;

import com.hbm.armor.LegacyArmorDurabilityRules;
import com.hbm.item.battery.BatteryItem;
import com.hbm.item.battery.HbmBattery;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.IShearable;

/** Actual source powered subclass; all abilities, presets, mining and combat still use ItemToolAbility. */
public class ItemToolAbilityPower extends ItemToolAbility implements HbmBattery {
    public long maxPower, chargeRate, consumption;
    private boolean shears;
    private static final class PoweredProperties extends SourceProperties {
        @Override public Item.Properties durability(int ignoredMaterialUses) { return super.durability(1); }
    }
    public ItemToolAbilityPower(String name, float damage, double movement, Tier material, Type type,
        AvailableAbilities abilities, long maxPower, long chargeRate, long consumption) {
        super(name, damage, movement, material, type, abilities, new PoweredProperties());
        this.maxPower = maxPower; this.chargeRate = chargeRate; this.consumption = consumption;
    }
    public ItemToolAbilityPower setShears() { shears = true; return this; }
    private long charge(ItemStack stack, long untagged) {
        var data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? untagged : data.copyTag().getLong("charge");
    }
    @Override public void setCharge(ItemStack stack, long amount) {
        var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.putLong("charge", amount); stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
    @Override public void chargeBattery(ItemStack stack, long amount) { setCharge(stack, charge(stack, 0) + amount); }
    @Override public void dischargeBattery(ItemStack stack, long amount) { setCharge(stack, Math.max(0, charge(stack, maxPower) - amount)); }
    @Override public long getCharge(ItemStack stack) {
        if (!stack.has(DataComponents.CUSTOM_DATA)) setCharge(stack, maxPower);
        return charge(stack, maxPower); // Source has no clamp of externally overcharged/negative existing NBT.
    }
    @Override public long getMaxCharge(ItemStack stack) { return maxPower; }
    @Override public long getChargeRate(ItemStack stack) { return chargeRate; }
    @Override public long getDischargeRate(ItemStack stack) { return 0; }
    @Override public boolean canOperate(ItemStack stack) { return getCharge(stack) >= consumption; }
    @Override public void setDamage(ItemStack stack, int damage) { dischargeBattery(stack, damage * consumption); }
    private void wear(ItemStack stack, int amount, LivingEntity owner) {
        if (!(owner.level() instanceof ServerLevel level) || owner instanceof Player player && player.getAbilities().instabuild) return;
        // Original ItemStack calls the powered setter after old Unbreaking; do not invoke modern maxDamage1 pre-break removal.
        setDamage(stack, stack.getDamageValue() + LegacyArmorDurabilityRules.damageAfterUnbreaking(level, stack, amount, owner.getRandom()));
    }
    @Override public void postHurtEnemy(ItemStack stack, LivingEntity victim, LivingEntity attacker) { wear(stack, 1, attacker); }
    @Override public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (state.getDestroySpeed(level, pos) != 0) wear(stack, 1, miner);
        return true;
    }
    @Override public boolean sourceCanShear(Player player, ItemStack stack, Level level, BlockPos pos) {
        return shears && level.getBlockState(pos).getBlock() instanceof IShearable target && target.isShearable(player, stack, level, pos);
    }
    @Override public boolean isBarVisible(ItemStack stack) { return getCharge(stack) < maxPower; }
    @Override public int getBarWidth(ItemStack stack) { return Math.round(13F * (float) ((double) getCharge(stack) / maxPower)); }
    @Override public int getBarColor(ItemStack stack) { return Mth.hsvToRgb(Math.max(0, (float) ((double) getCharge(stack) / maxPower)) / 3F, 1, 1); }
    @Override public boolean shouldCauseReequipAnimation(ItemStack previous, ItemStack current, boolean slotChanged) {
        // The real1.7 caller compares item/damage; charge and preset updates do not continually hide the held animated model.
        return previous.getItem() != current.getItem() || previous.getDamageValue() != current.getDamageValue();
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.hbm.source_sword.charge", BatteryItem.shortNumber(getCharge(stack)), BatteryItem.shortNumber(maxPower)));
        super.appendHoverText(stack, context, lines, flag);
    }
}
