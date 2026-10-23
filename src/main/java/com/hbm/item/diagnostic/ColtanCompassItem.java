package com.hbm.item.diagnostic;

import com.hbm.worldgen.ore.ColtanDepositLocation;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/** ItemColtanCompass: its target/lease intentionally belong to the item, not each render stack. */
public final class ColtanCompassItem extends Item {
    public static Consumer<String> CLIENT_DISTANCE = message -> { };
    public int lastX, lastZ;
    public long lease;

    public ColtanCompassItem() { super(new Properties().stacksTo(1)); }

    @Override public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide) {
            if (stack.has(DataComponents.CUSTOM_DATA)) {
                var data = stack.get(DataComponents.CUSTOM_DATA).copyTag();
                lastX = data.getInt("colX");
                lastZ = data.getInt("colZ");
                lease = System.currentTimeMillis() + 1000;
                double dx = entity.getX() - lastX, dz = entity.getZ() - lastZ;
                CLIENT_DISTANCE.accept(((int) Math.sqrt(dx * dx + dz * dz)) + "m");
            }
            if (lease < System.currentTimeMillis()) {
                lastX = 0;
                lastZ = 0;
            }
        } else if (level instanceof ServerLevel server && !stack.has(DataComponents.CUSTOM_DATA)) {
            var target = ColtanDepositLocation.fromSeed(server.getSeed());
            var data = new net.minecraft.nbt.CompoundTag();
            data.putInt("colX", target.x());
            data.putInt("colZ", target.z());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(data));
        }
    }

    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        lines.add(Component.translatable("tooltip.hbm.coltan_tool.deposit"));
        lines.add(Component.translatable("tooltip.hbm.coltan_tool.area"));
        lines.add(Component.translatable("tooltip.hbm.coltan_tool.not_blob"));
    }
}
