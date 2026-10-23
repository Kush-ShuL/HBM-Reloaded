package com.hbm.item.fluid;

import com.hbm.fluid.HbmFluidType;
import com.hbm.fluid.HbmFluids;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** Original fixed-content canister, gas bottle and tank shell item classes. */
public class FluidContainerItem extends Item {
    public enum Kind { CANISTER, GAS, TANK, LEAD_TANK, BARREL, PACK }
    private final Kind kind;
    private final String empty;
    public FluidContainerItem(Kind kind, String empty) { super(new Properties()); this.kind = kind; this.empty = empty; }
    public Kind kind() { return kind; }
    public List<ItemStack> creativeStacks() {
        var result = new ArrayList<ItemStack>();
        var order = HbmFluids.getInNiceOrder();
        for (int index = 1; index < order.length; index++) {
            HbmFluidType type = order[index];
            boolean include = switch (kind) {
                case CANISTER -> type.getContainer(HbmFluidType.Canister.class) != null;
                case GAS -> type.getContainer(HbmFluidType.Gastank.class) != null;
                case LEAD_TANK -> !type.hasNoContainer();
                default -> !type.hasNoContainer() && !type.needsLeadContainer();
            };
            if (include) result.add(FluidItemContent.stack(this, type));
        }
        return result;
    }
    @Override public Component getName(ItemStack stack) {
        var type = FluidItemContent.type(stack).displayName();
        if (kind == Kind.CANISTER || kind == Kind.GAS) return Component.translatable(getDescriptionId()).append(" ").append(type);
        return Component.translatable(getDescriptionId(), type);
    }
    @Override public boolean hasCraftingRemainingItem(ItemStack stack) { return true; }
    @Override public ItemStack getCraftingRemainingItem(ItemStack stack) { return new ItemStack(com.hbm.registry.HbmContent.item(empty)); }
    public int color(ItemStack stack, int pass) {
        if (pass == 0) return 0xFFFFFF;
        var type = FluidItemContent.type(stack);
        if (kind == Kind.CANISTER) { var data = type.getContainer(HbmFluidType.Canister.class); return data == null || data.color() < 0 ? 0xFFFFFF : data.color(); }
        if (kind == Kind.GAS) { var data = type.getContainer(HbmFluidType.Gastank.class); return data == null ? 0xFFFFFF : pass == 1 ? data.bottleColor() : data.labelColor(); }
        return type.getColor() < 0 ? 0xFFFFFF : type.getColor();
    }
}
