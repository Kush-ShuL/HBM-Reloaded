package com.hbm.fluid;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.hbm.item.fluid.FluidIdentifier;
import com.hbm.item.fluid.FluidItemContent;
import net.minecraft.core.BlockPos;

/** X5846 tank storage and wire/NBT layout; original item loaders are separate work. */
public class FluidTank implements Cloneable {
    public static final FluidTank NONE_TANK = new FluidTank(HbmFluids.NONE, 1_000);
    public static final FluidTank[] EMPTY_ARRAY = new FluidTank[0];
    public static final List<FluidLoadingHandler> loadingHandlers = new ArrayList<>(List.of(new FluidLoaderStandard(), new FluidLoaderFillableItem(), new FluidLoaderInfinite(), new NeoForgeFluidItemLoader()));
    public static final Set<Item> noDualUnload = new HashSet<>();
    protected HbmFluidType type;
    protected int fluid, maxFluid;
    protected int pressure;

    public FluidTank(HbmFluidType type, int maxFluid) { this.type = type; this.maxFluid = maxFluid; }
    public FluidTank withPressure(int pressure) {
        if (this.pressure != pressure) setFill(0);
        this.pressure = pressure;
        return this;
    }
    public void setFill(int fill) { fluid = fill; }
    public void setTankType(HbmFluidType type) {
        if (type == null) type = HbmFluids.NONE;
        if (this.type == type) return;
        this.type = type;
        setFill(0);
    }
    public void resetTank() { type = HbmFluids.NONE; fluid = 0; pressure = 0; }
    public FluidTank conform(FluidStack stack) { setTankType(stack.type); withPressure(stack.pressure); return this; }
    public HbmFluidType getTankType() { return type; }
    public int getFill() { return fluid; }
    public int getMaxFill() { return maxFluid; }
    public int getPressure() { return pressure; }
    public int changeTankSize(int size) {
        maxFluid = size;
        if (fluid > maxFluid) { int difference = fluid - maxFluid; fluid = maxFluid; return difference; }
        return 0;
    }
    public boolean loadTank(int in, int out, ItemStack[] slots) {
        if (slots[in] == null || slots[in].isEmpty()) return false;
        if (slots[in].getItem() != FluidItemContent.INFINITE.get() && pressure != 0) return false;
        int previous = fluid;
        for (var handler : loadingHandlers) if (handler.emptyItem(slots, in, out, this)) break;
        return fluid > previous;
    }
    public boolean unloadTank(int in, int out, ItemStack[] slots) {
        if (slots[in] == null || slots[in].isEmpty()) return false;
        int previous = fluid;
        for (var handler : loadingHandlers) if (handler.fillItem(slots, in, out, this)) break;
        return fluid < previous;
    }
    public boolean setType(int in, ItemStack[] slots) { return setType(in, in, slots); }
    public boolean setType(int in, int out, ItemStack[] slots) {
        if (slots[in] == null || slots[in].isEmpty() || !(slots[in].getItem() instanceof FluidIdentifier identifier)) return false;
        if (in != out && slots[out] != null && !slots[out].isEmpty()) return false;
        var next = identifier.getType(null, BlockPos.ZERO, slots[in]);
        if (type == next) return false;
        type = next; fluid = 0;
        if (in != out) { slots[out] = slots[in].copy(); slots[in] = ItemStack.EMPTY; }
        return true;
    }
    public void writeToNBT(CompoundTag tag, String key) {
        tag.putInt(key, fluid);
        tag.putInt(key + "_max", maxFluid);
        tag.putInt(key + "_type", type.getID());
        tag.putShort(key + "_p", (short) pressure);
    }
    public void readFromNBT(CompoundTag tag, String key) {
        fluid = tag.getInt(key);
        int maximum = tag.getInt(key + "_max");
        if (maximum > 0) maxFluid = maximum;
        // Source clamps against the saved maximum, including a missing maximum of zero.
        fluid = Mth.clamp(fluid, 0, maximum);
        type = HbmFluids.fromNameCompat(tag.getString(key + "_type"));
        if (type == HbmFluids.NONE) type = HbmFluids.fromID(tag.getInt(key + "_type"));
        pressure = tag.getShort(key + "_p");
    }
    public void serialize(ByteBuf buffer) { buffer.writeInt(fluid).writeInt(maxFluid).writeInt(type.getID()).writeShort((short) pressure); }
    public void deserialize(ByteBuf buffer) { fluid = buffer.readInt(); maxFluid = buffer.readInt(); type = HbmFluids.fromID(buffer.readInt()); pressure = buffer.readShort(); }
}
