package com.hbm.machine.battery;

import com.hbm.api.computer.ComputerArguments;
import com.hbm.api.computer.ComputerControl;
import com.hbm.api.computer.ComputerException;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.ConnectionPriority;
import com.hbm.energy.EnergyConductor;
import com.hbm.energy.EnergyProvider;
import com.hbm.energy.EnergyReceiver;
import com.hbm.energy.HbmEnergy;
import com.hbm.energy.PowerNode;
import com.hbm.item.battery.CreativeBatteryItem;
import com.hbm.item.battery.HbmBattery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Native HE storage, inventory and the two original read-only OC callbacks. */
public final class BatteryBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider,
        EnergyProvider, EnergyReceiver, EnergyConductor, MachineView, ComputerControl {
    public static final int MODE_INPUT = 0, MODE_BUFFER = 1, MODE_OUTPUT = 2, MODE_NONE = 3;
    private static final int[] TOP = {0}, BOTTOM = {0, 1}, SIDE = {1};
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(2, ItemStack.EMPTY);
    private final long[] log = new long[20];
    private final BatteryTier tier;
    private PowerNode node;
    private long power;
    private long delta;
    private long previousPowerState;
    private int redLow = MODE_INPUT;
    private int redHigh = MODE_OUTPUT;
    private int lastComparator;
    private ConnectionPriority priority = ConnectionPriority.LOW;
    private Component customName;
    private final ContainerData menuData = new ContainerData() {
        @Override public int get(int index) {
            if (index < 4) return word(power, index);
            if (index < 8) return word(getMaxPower(), index - 4);
            if (index < 12) return word(delta, index - 8);
            return switch (index) { case 12 -> redLow; case 13 -> redHigh; case 14 -> priority.ordinal(); default -> 0; };
        }
        @Override public void set(int index, int value) {}
        @Override public int getCount() { return 15; }
    };

    public BatteryBlockEntity(BlockPos pos, BlockState state) {
        super(BatteryMachineContent.ENTITY.get(), pos, state);
        if (!(state.getBlock() instanceof BatteryBlock block)) throw new IllegalArgumentException("Storage block entity requires a battery block");
        tier = block.tier();
    }

    private static int word(long value, int index) { return (int) (value >>> (index * 16) & 0xFFFFL); }
    public BatteryTier tier() { return tier; }
    public ContainerData menuData() { return menuData; }
    public long delta() { return delta; }
    public int redLow() { return redLow; }
    public int redHigh() { return redHigh; }
    public Component customName() { return customName; }
    public void setCustomName(Component name) { customName = name; setChanged(); }
    @Override public Component getDisplayName() { return customName == null ? Component.translatable("container.battery") : customName; }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new BatteryMenu(id, inventory, this, menuData); }

    public static void tick(Level level, BlockPos pos, BlockState state, BatteryBlockEntity battery) {
        if (level instanceof ServerLevel server) battery.tickServer(server);
    }

    private void tickServer(ServerLevel server) {
        ensureNode(server);
        long previousPower = power;
        chargeItem(); // Source slot 1 first.
        updateComparator();
        dischargeItem(); // Source slot 0 second.
        long average = (power + previousPower) / 2L;
        delta = average - log[0];
        System.arraycopy(log, 1, log, 0, log.length - 1);
        log[19] = average;
        previousPowerState = power;
        if (power != previousPower) setChanged();
        updateComparator();
    }

    private void chargeItem() {
        ItemStack stack = inventory.get(1);
        if (stack.getItem() instanceof CreativeBatteryItem) { power = 0; return; }
        if (stack.getItem() instanceof HbmBattery battery) {
            long amount = Math.max(0, Math.min(Math.min(power, battery.getChargeRate(stack)), battery.getMaxCharge(stack) - battery.getCharge(stack)));
            if (amount > 0) { battery.chargeBattery(stack, amount); power -= amount; }
        }
    }

    private void dischargeItem() {
        ItemStack stack = inventory.get(0);
        if (stack.getItem() instanceof CreativeBatteryItem) { power = getMaxPower(); return; }
        if (stack.getItem() instanceof HbmBattery battery) {
            long amount = Math.max(0, Math.min(Math.min(getMaxPower() - power, battery.getDischargeRate(stack)), battery.getCharge(stack)));
            if (amount > 0) { battery.dischargeBattery(stack, amount); power += amount; }
        }
    }

    public int relevantMode() { return level != null && level.hasNeighborSignal(worldPosition) ? redHigh : redLow; }
    @Override public long getProviderSpeed() { int mode = relevantMode(); return mode == MODE_OUTPUT || mode == MODE_BUFFER ? tier.providerSpeed() : 0; }
    @Override public long getReceiverSpeed() { int mode = relevantMode(); return mode == MODE_INPUT || mode == MODE_BUFFER ? tier.receiverSpeed() : 0; }
    @Override public long getPower() { return power; }
    @Override public long getMaxPower() { return tier.capacity(); }
    @Override public ConnectionPriority getPriority() { return priority; }
    @Override public boolean canConnect(Direction side) { return true; }
    @Override public boolean isLoaded() { return !isRemoved() && level != null; }
    @Override public PowerNode energyNode() { return node; }
    @Override public long energyStored() { return power; }
    @Override public long energyCapacity() { return getMaxPower(); }

    @Override public void setPower(long value) {
        long next = Math.max(0, Math.min(getMaxPower(), value));
        if (power != next) { power = next; setChanged(); updateComparator(); }
    }

    public int comparatorPower() {
        if (power == 0) return 0;
        return Math.min(15, (int) ((double) power / getMaxPower() * 15D) + 1);
    }

    private void updateComparator() {
        int comparator = comparatorPower();
        if (lastComparator != comparator && level != null && !level.isClientSide) {
            lastComparator = comparator;
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }

    public boolean cycleButton(int button) {
        switch (button) {
            case 0 -> redLow = (redLow + 1) % 4;
            case 1 -> redHigh = (redHigh + 1) % 4;
            case 2 -> priority = switch (priority) {
                case LOW -> ConnectionPriority.NORMAL;
                case NORMAL -> ConnectionPriority.HIGH;
                default -> ConnectionPriority.LOW;
            };
            default -> { return false; }
        }
        setChanged();
        return true;
    }

    private void ensureNode(ServerLevel server) {
        if (node == null || node.isExpired()) {
            node = new PowerNode(worldPosition);
            node.addProvider(this);
            node.addReceiver(this);
            HbmEnergy.nodespace(server).createNode(node);
        }
    }

    @Override public void onLoad() { super.onLoad(); if (level instanceof ServerLevel server) ensureNode(server); }
    @Override public void setRemoved() {
        if (node != null && !node.isExpired() && level instanceof ServerLevel server) HbmEnergy.nodespace(server).destroyNode(node);
        node = null;
        super.setRemoved();
    }

    @Override public String computerType() { return "ntm_energy_storage_legacy"; }
    @Override public String[] computerMethods() { return new String[]{"getEnergyInfo", "getInfo"}; }
    @Override public Object[] callComputer(String method, ComputerArguments arguments) throws ComputerException {
        return switch (method) {
            case "getEnergyInfo", "getInfo" -> new Object[]{getPower(), getMaxPower()};
            default -> throw new ComputerException("No such method: " + method);
        };
    }

    public CompoundTag persistentData() {
        var root = new CompoundTag();
        var data = new CompoundTag();
        data.putLong("power", power);
        data.putLong("prevPowerState", previousPowerState);
        data.putShort("redLow", (short) redLow);
        data.putShort("redHigh", (short) redHigh);
        data.putInt("priority", priority.ordinal());
        root.put("persistent", data);
        return root;
    }

    public void restorePersistent(CompoundTag root) {
        if (!root.contains("persistent", Tag.TAG_COMPOUND)) return;
        CompoundTag data = root.getCompound("persistent");
        setPower(data.getLong("power"));
        previousPowerState = Math.max(0, Math.min(getMaxPower(), data.getLong("prevPowerState")));
        redLow = validMode(data.getShort("redLow"), MODE_INPUT);
        redHigh = validMode(data.getShort("redHigh"), MODE_OUTPUT);
        priority = validPriority(data.getInt("priority"));
        setChanged();
    }

    private static int validMode(int value, int fallback) { return value >= 0 && value <= 3 ? value : fallback; }
    private static ConnectionPriority validPriority(int ordinal) {
        return ordinal >= 1 && ordinal <= 3 ? ConnectionPriority.values()[ordinal] : ConnectionPriority.LOW;
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putLong("power", power);
        tag.putLong("prevPowerState", previousPowerState);
        tag.putShort("redLow", (short) redLow);
        tag.putShort("redHigh", (short) redHigh);
        tag.putByte("lastRedstone", (byte) lastComparator);
        tag.putByte("priority", (byte) priority.ordinal());
        ContainerHelper.saveAllItems(tag, inventory, lookup);
        if (customName != null) tag.putString("CustomName", Component.Serializer.toJson(customName, lookup));
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        power = Math.max(0, Math.min(getMaxPower(), tag.getLong("power")));
        previousPowerState = Math.max(0, Math.min(getMaxPower(), tag.getLong("prevPowerState")));
        redLow = validMode(tag.getShort("redLow"), MODE_INPUT);
        redHigh = validMode(tag.getShort("redHigh"), MODE_OUTPUT);
        lastComparator = tag.getByte("lastRedstone");
        priority = validPriority(tag.getByte("priority"));
        inventory.clear();
        ContainerHelper.loadAllItems(tag, inventory, lookup);
        customName = tag.contains("CustomName", Tag.TAG_STRING) ? Component.Serializer.fromJson(tag.getString("CustomName"), lookup) : null;
    }

    @Override public int getContainerSize() { return inventory.size(); }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack stack = ContainerHelper.removeItem(inventory, slot, amount); if (!stack.isEmpty()) setChanged(); return stack; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }
    @Override public void setItem(int slot, ItemStack stack) { inventory.set(slot, stack); if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize()); setChanged(); }
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public void clearContent() { inventory.clear(); setChanged(); }
    @Override public int[] getSlotsForFace(Direction side) { return (side == Direction.DOWN ? BOTTOM : side == Direction.UP ? TOP : SIDE).clone(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot == 0 || slot == 1; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return canPlaceItem(slot, stack); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        if (!(stack.getItem() instanceof HbmBattery battery)) return false;
        return slot == 0 && battery.getCharge(stack) == 0 || slot == 1 && battery.getCharge(stack) == battery.getMaxCharge(stack);
    }
}
