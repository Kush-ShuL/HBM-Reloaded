package com.hbm.machine.radgen;

import api.hbm.redstoneoverradio.IRORValueProvider;
import com.hbm.api.machine.MachineView;
import com.hbm.energy.*;
import com.hbm.multiblock.*;
import java.math.BigInteger;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Real12 concurrent fuel queues. Output space is checked only at load; saturated HE does not pause source fuel consumption. */
public final class RadGenEntity extends BlockEntity implements WorldlyContainer, MenuProvider, NamedMultiblock,
        EnergyProvider, SidedEnergyConnector, MachineView, IRORValueProvider, MuffledMachine {
    public static final long MAX_POWER = 1000000;
    public int[] progress = new int[12], maxProgress = new int[12], production = new int[12];
    public final ItemStack[] processing = new ItemStack[12];
    private final ItemStack[] slots = new ItemStack[24];
    private BigInteger power = BigInteger.ZERO;
    public boolean isOn, muffled, tilted;
    private int output;
    private Component customName;
    private PowerNode node;

    public RadGenEntity(BlockPos pos, BlockState state) {
        super(RadGenContent.ENTITY.get(), pos, state);
        Arrays.fill(slots, ItemStack.EMPTY);
        Arrays.fill(processing, ItemStack.EMPTY);
    }
    @Override public boolean isValidBlockState(BlockState state) { return getType().isValid(state) && DummyableBlock.isCore(state); }
    public static BlockPos powerPort(BlockPos core, Direction facing) { return core.relative(facing, -3); }
    public static void tick(Level level, BlockPos pos, BlockState state, RadGenEntity machine) { if (level instanceof ServerLevel server) machine.serverTick(server); }
    private void updatePorts(ServerLevel server) {
        var position = powerPort(worldPosition, DummyableBlock.facing(getBlockState()));
        if (!server.hasChunkAt(position) || !server.getBlockState(position).is(getBlockState().getBlock()) || !DummyableBlock.isPort(server.getBlockState(position))) { disconnect(); return; }
        if (node == null || node.isExpired() || !node.positions().contains(position)) {
            disconnect();
            var outward = DummyableBlock.facing(getBlockState()).getOpposite();
            node = new PowerNode(position).setConnections(new NodeConnection(position.relative(outward), outward));
            HbmEnergy.nodespace(server).createNode(node);
        }
        if (node.hasValidNet()) node.addProvider(this);
    }
    private void serverTick(ServerLevel server) {
        // Native provider subscriptions are refreshed before generation, matching source setup/update/provide order.
        updatePorts(server);
        output = 0;
        for (int i = 0; i < 12; i++) {
            var fuel = RadGenFuels.find(slots[i]);
            if (!processing[i].isEmpty() || fuel == null || fuel.duration() <= 0) continue;
            var result = fuel.outputStack();
            var existing = slots[i + 12];
            if (!result.isEmpty() && !existing.isEmpty() && !(result.is(existing.getItem())
                && RadGenFuels.metadata(result) == RadGenFuels.metadata(existing)
                && result.getCount() + existing.getCount() <= existing.getMaxStackSize())) continue;
            progress[i] = 0;
            maxProgress[i] = fuel.duration();
            production[i] = fuel.production();
            processing[i] = fuel.inputStack();
            removeItem(i, 1);
            setChanged();
        }
        isOn = false;
        for (int i = 0; i < 12; i++) if (!processing[i].isEmpty()) {
            isOn = true;
            power = power.add(BigInteger.valueOf(production[i]));
            output += production[i];
            progress[i]++;
            if (progress[i] >= maxProgress[i]) {
                progress[i] = 0;
                var fuel = RadGenFuels.find(processing[i]);
                var result = fuel == null ? ItemStack.EMPTY : fuel.outputStack();
                if (!result.isEmpty()) {
                    if (slots[i + 12].isEmpty()) slots[i + 12] = result;
                    else slots[i + 12].grow(result.getCount());
                }
                processing[i] = ItemStack.EMPTY;
                setChanged();
            }
        }
        if (power.compareTo(BigInteger.valueOf(MAX_POWER)) > 0) power = BigInteger.valueOf(MAX_POWER);
        setChanged();
        var packet = getUpdatePacket();
        try(var dispatchScope=com.hbm.network.dispatch.PacketDispatch.beginGroup(server.getServer(),packet)){for (var player : server.players()) if (player.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) < 2500) player.connection.send(packet);dispatchScope.commit();}
    }
    private void disconnect() { if (node != null && level instanceof ServerLevel server) HbmEnergy.nodespace(server).destroyNode(node); node = null; }
    @Override public void setRemoved() { disconnect(); super.setRemoved(); }
    @Override public void onChunkUnloaded() { disconnect(); super.onChunkUnloaded(); }
    @Override public boolean isLoaded() { return !isRemoved() && level != null && level.hasChunkAt(worldPosition); }
    @Override public boolean canConnect(Direction side) { return side != null; }
    @Override public long getPower() { return power.max(BigInteger.valueOf(Long.MIN_VALUE)).min(BigInteger.valueOf(Long.MAX_VALUE)).longValue(); }
    @Override public long getMaxPower() { return MAX_POWER; }
    @Override public BigInteger getPowerExact() { return power; }
    @Override public BigInteger getMaxPowerExact() { return BigInteger.valueOf(MAX_POWER); }
    @Override public void setPower(long power) { setPowerExact(BigInteger.valueOf(power)); }
    @Override public void setPowerExact(BigInteger power) { this.power = power; setChanged(); }
    @Override public void usePower(long amount) { setPowerExact(power.subtract(BigInteger.valueOf(amount))); }
    @Override public void usePowerExact(BigInteger amount) { setPowerExact(power.subtract(amount)); }
    @Override public long energyStored() { return getPower(); }
    @Override public long energyCapacity() { return MAX_POWER; }
    @Override public List<ProgressView> progressBars() {
        var result = new ArrayList<ProgressView>();
        for (int i = 0; i < 12; i++) if (!processing[i].isEmpty() && maxProgress[i] > 0) result.add(new ProgressView("jade.hbm.radgen.slot_" + (i + 1), progress[i], maxProgress[i], 0x62B949));
        return List.copyOf(result);
    }
    public int output() { return output; }
    public void provideExtraInfo(CompoundTag tag) { tag.putDouble("output", output); }
    @Override public String[] getFunctionInfo() {
        var result = new ArrayList<String>(List.of(PREFIX_VALUE + "state", PREFIX_VALUE + "power", PREFIX_VALUE + "output"));
        for (int i = 1; i <= 12; i++) result.add(PREFIX_VALUE + "output" + i);
        for (int i = 1; i <= 12; i++) result.add(PREFIX_VALUE + "progress" + i);
        return result.toArray(String[]::new);
    }
    @Override public String provideRORValue(String name) {
        if (name.equals(PREFIX_VALUE + "state")) return isOn ? "1" : "0";
        if (name.equals(PREFIX_VALUE + "power")) return power.toString();
        if (name.equals(PREFIX_VALUE + "output")) return Integer.toString(output);
        for (int i = 0; i < 12; i++) {
            if (name.equals(PREFIX_VALUE + "output" + (i + 1))) return Integer.toString(production[i]);
            if (name.equals(PREFIX_VALUE + "progress" + (i + 1))) return Integer.toString(progress[i] * 100 / Math.max(maxProgress[i], 1));
        }
        return null;
    }

    private void writeState(CompoundTag tag) {
        tag.putIntArray("progress", progress); tag.putIntArray("maxProgress", maxProgress); tag.putIntArray("production", production);
        if (power.bitLength() < 64) tag.putLong("power", power.longValue()); else tag.putString("power", power.toString());
        tag.putBoolean("isOn", isOn); tag.putBoolean("muffled", muffled); tag.putBoolean("tilted", tilted);
    }
    private boolean readState(CompoundTag tag) {
        progress = tag.getIntArray("progress");
        if (progress.length != 12) { progress = new int[12]; return false; }
        maxProgress = tag.getIntArray("maxProgress"); production = tag.getIntArray("production");
        power = tag.contains("power", Tag.TAG_STRING) ? new BigInteger(tag.getString("power")) : BigInteger.valueOf(tag.getLong("power"));
        isOn = tag.getBoolean("isOn"); muffled = tag.getBoolean("muffled"); tilted = tag.getBoolean("tilted");
        return true;
    }
    private static void saveItems(CompoundTag tag, String name, ItemStack[] items, HolderLookup.Provider lookup) {
        var list = new ListTag();
        for (int i = 0; i < items.length; i++) if (!items[i].isEmpty()) { var item = (CompoundTag) items[i].save(lookup); item.putByte("slot", (byte) i); list.add(item); }
        tag.put(name, list);
    }
    private static void loadItems(CompoundTag tag, String name, ItemStack[] items, HolderLookup.Provider lookup) {
        Arrays.fill(items, ItemStack.EMPTY);
        for (var value : tag.getList(name, Tag.TAG_COMPOUND)) { var item = (CompoundTag) value; int index = item.getByte("slot"); if (index >= 0 && index < items.length) items[index] = ItemStack.parseOptional(lookup, item); }
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup); saveItems(tag, "items", slots, lookup); writeState(tag); saveItems(tag, "progressing", processing, lookup);
        if (customName != null) tag.putString("name", Component.Serializer.toJson(customName, lookup));
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup); loadItems(tag, "items", slots, lookup);
        customName = null;
        if (tag.contains("name")) { String value = tag.getString("name"); try { customName = Component.Serializer.fromJson(value, lookup); } catch (com.google.gson.JsonParseException error) { customName = Component.literal(value); } if (customName == null && !value.isEmpty()) customName = Component.literal(value); }
        if (!readState(tag)) return; // Original invalid-progress early-return, not a fabricated new validation mode.
        loadItems(tag, "progressing", processing, lookup);
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { var tag = new CompoundTag(); writeState(tag); return tag; }
    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookup) { readState(tag); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(), lookup); }
    @Override public Component getDisplayName() { return customName == null || customName.getString().isEmpty() ? Component.translatable("container.radGen") : customName; }
    @Override public void setCustomName(Component name) { customName = name; setChanged(); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new RadGenMenu(id, inventory, this, worldPosition); }
    /** Native spectator opens also invoke the provider; this is the complete one-BlockPos menu schema. */
    @Override public void writeClientSideData(AbstractContainerMenu menu,net.minecraft.network.RegistryFriendlyByteBuf buffer){buffer.writeBlockPos(worldPosition);}
    @Override public boolean isMuffled() { return muffled; }
    @Override public void setMuffled(boolean value) { muffled = value; setChanged(); }
    @Override public int getContainerSize() { return 24; }
    @Override public boolean isEmpty() { return Arrays.stream(slots).allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return slots[slot]; }
    @Override public ItemStack removeItem(int slot, int count) { var result = slots[slot].split(count); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { var result = slots[slot]; slots[slot] = ItemStack.EMPTY; return result; }
    @Override public void setItem(int slot, ItemStack stack) { slots[slot] = stack; stack.limitSize(64); setChanged(); }
    @Override public void clearContent() { Arrays.fill(slots, ItemStack.EMPTY); setChanged(); }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && worldPosition.distToCenterSqr(player.position()) <= 144; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot >= 12 || RadGenFuels.find(stack) == null) return false;
        if (slots[slot].isEmpty()) return true;
        int size = slots[slot].getCount();
        for (int i = 0; i < 12; i++) {
            if (slots[i].isEmpty()) return false;
            if (slots[i].is(stack.getItem()) && RadGenFuels.metadata(slots[i]) == RadGenFuels.metadata(stack) && slots[i].getCount() < size) return false;
        }
        return true;
    }
    @Override public int[] getSlotsForFace(Direction side) { return java.util.stream.IntStream.range(0, 24).toArray(); }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return canPlaceItem(slot, stack); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return slot >= 12; }
}
