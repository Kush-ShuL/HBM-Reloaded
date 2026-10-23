package com.hbm.item.fluid;

import com.hbm.fluid.FluidStack;
import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;

/** Native-fluid recipe icon keeps source fill and pressure custom data. */
public final class FluidIconItem extends Item {
    public FluidIconItem() { super(new Properties()); }
    /** Source ItemFluidIcon.getSubItems: all fluids in nice order, excluding NONE. */
    public static List<ItemStack> recipeViewerStacks() {
        return java.util.Arrays.stream(HbmFluids.getInNiceOrder())
                .filter(type -> type != HbmFluids.NONE)
                .map(type -> make(type, 0)).toList();
    }
    public static ItemStack make(FluidStack stack) { return make(stack.type, stack.fill, stack.pressure); }
    public static ItemStack make(HbmFluidType type, int fill) { return make(type, fill, 0); }
    public static ItemStack addQuantity(ItemStack stack, int fill) {
        if (fill > 0) CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("fill", fill));
        return stack;
    }
    public static ItemStack addPressure(ItemStack stack, int pressure) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("pressure", pressure));
        return stack;
    }
    public static ItemStack make(HbmFluidType type, int fill, int pressure) {
        var result = FluidItemContent.stack(FluidItemContent.ICON.get(), type);
        return addPressure(addQuantity(result, fill), pressure);
    }
    public static int getQuantity(ItemStack stack) { return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("fill"); }
    public static int getPressure(ItemStack stack) { return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("pressure"); }
    @Override public Component getName(ItemStack stack) { return FluidItemContent.type(stack).displayName(); }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (getQuantity(stack) > 0) tooltip.add(Component.literal(getQuantity(stack) + "mB"));
        if (getPressure(stack) > 0) {
            tooltip.add(Component.literal(getPressure(stack) + "PU").withStyle(ChatFormatting.RED));
            tooltip.add(Component.literal("Pressurized, use compressor!").withStyle((System.currentTimeMillis() % 1000 < 500) ? ChatFormatting.RED : ChatFormatting.DARK_RED));
        }
        if (net.neoforged.fml.loading.FMLEnvironment.dist.isClient()) {
            com.hbm.item.fluid.client.FluidInfo.append(FluidItemContent.type(stack), tooltip, net.minecraft.client.gui.screens.Screen.hasShiftDown());
        }
    }
}
