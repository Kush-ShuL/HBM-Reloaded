package com.hbm.machine.storagedrum;

import com.hbm.api.machine.IFluidCopiable;
import com.hbm.api.machine.MachineView;
import com.hbm.exposure.HbmExposure;
import com.hbm.fluid.FluidTank;
import com.hbm.fluid.HbmFluids;
import com.hbm.fluid.network.FluidPorts;
import com.hbm.fluid.network.StandardFluidSender;
import com.hbm.hazard.HazardType;
import com.hbm.hazard.HbmHazards;
import com.hbm.multiblock.MuffledMachine;
import com.hbm.multiblock.NamedMultiblock;
import com.hbm.registry.HbmContent;
import com.hbm.item.nuclear.waste.SourceWasteIsotopes;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/** Original24 single-item slots, around-six private fluid outputs and timed CREATIVE radiation exposure. No HE. */
public final class StorageDrumEntity extends BlockEntity implements WorldlyContainer, MenuProvider,
        StandardFluidSender, IFluidCopiable, MachineView, NamedMultiblock, MuffledMachine {
    private static final int[] SLOTS = IntStream.range(0, 24).toArray();
    private final ItemStack[] slots = new ItemStack[24];
    public final FluidTank[] tanks = {
        new FluidTank(HbmFluids.fromName("WASTEFLUID"), 16000),
        new FluidTank(HbmFluids.fromName("WASTEGAS"), 16000)
    };
    public boolean muffled, tilted;
    private Component customName;
    private FluidPorts ports;

    public StorageDrumEntity(BlockPos pos, BlockState state) {
        super(StorageDrumContent.ENTITY.get(), pos, state);
        Arrays.fill(slots, ItemStack.EMPTY);
    }

    @Override public boolean isValidBlockState(BlockState state) { return getType().isValid(state); }
    public static void tick(Level level, BlockPos pos, BlockState state, StorageDrumEntity drum) {
        if (level instanceof ServerLevel server) drum.serverTick(server);
    }

    private void serverTick(ServerLevel server) {
        if (ports == null) ports = new FluidPorts(worldPosition);
        ports.update(server, this);
        float radiation = 0;
        int liquid = 0, gas = 0;
        boolean changed = false;
        for (int slot = 0; slot < 24; slot++) {
            if (slots[slot].isEmpty()) continue;
            if (server.getGameTime() % 20 == 0) {
                var value = HbmHazards.registry().evaluate(slots[slot]).firstLevel(HazardType.RADIATION);
                if (value.isEmpty()) throw new IllegalStateException("Actual drum inventory radiation modifier is unresolved: " + slots[slot]);
                radiation += (float) value.getAsDouble();
            }
            var result = StorageDrumDecay.decay(slots[slot], server.random);
            if (result != null) {
                slots[slot] = result.stack();
                liquid += result.liquid();
                gas += result.gas();
                changed = true;
            }
        }
        tanks[0].setFill(tanks[0].getFill() + liquid);
        tanks[1].setFill(tanks[1].getFill() + gas);
        for (var tank : tanks) {
            int overflow = Math.max(tank.getFill() - tank.getMaxFill(), 0);
            if (overflow > 0) tank.setFill(tank.getFill() - overflow);
            // The actual two source types are plain FluidType. Its old onFluidRelease overload is empty;
            // this caller does not dispatch FT_VentRadiation/SPILL traits or create another radiation aura.
        }
        if (changed) setChanged();
        var packet = getUpdatePacket();
        for (var player : server.players()) {
            if (player.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) < 625) player.connection.send(packet);
        }
        if (radiation > 0) radiate(server, radiation);
    }

    private void radiate(ServerLevel server, float radiation) {
        double x = worldPosition.getX() + .5, y = worldPosition.getY() + .5, z = worldPosition.getZ() + .5;
        for (var entity : server.getEntitiesOfClass(LivingEntity.class, new AABB(x, y, z, x, y, z).inflate(32))) {
            double dx = entity.getX() - x, dy = entity.getY() + entity.getEyeHeight() - y, dz = entity.getZ() - z;
            double length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
            double vx = length < 1E-4 ? 0 : dx / length, vy = length < 1E-4 ? 0 : dy / length, vz = length < 1E-4 ? 0 : dz / length;
            float resistance = 0;
            for (int i = 1; i < length; i++) {
                resistance += server.getBlockState(new BlockPos(Mth.floor(x + vx * i), Mth.floor(y + vy * i), Mth.floor(z + vz * i))).getBlock().getExplosionResistance();
            }
            if (resistance < 1) resistance = 1;
            float exposure = radiation;
            exposure /= resistance;
            exposure /= (float) (length * length);
            HbmExposure.contaminate(entity, HbmExposure.Contamination.CREATIVE, exposure);
        }
    }

    private void disconnect() { if (ports != null && level instanceof ServerLevel server) ports.remove(server); ports = null; }
    @Override public void setRemoved() { disconnect(); super.setRemoved(); }
    @Override public void onChunkUnloaded() { disconnect(); super.onChunkUnloaded(); }
    @Override public FluidTank[] getAllTanks() { return tanks; }
    @Override public FluidTank[] getSendingTanks() { return tanks; }
    @Override public FluidTank getTankToPaste() { return null; }
    @Override public void useUpFluid(com.hbm.fluid.HbmFluidType type, int pressure, long amount) {
        StandardFluidSender.super.useUpFluid(type, pressure, amount);
        if (amount != 0) setChanged();
    }
    @Override public long energyStored() { return 0; }
    @Override public long energyCapacity() { return 0; }
    @Override public List<TankView> tanks() { return Arrays.stream(tanks).map(tank -> new TankView(tank.getTankType(), tank.getFill(), tank.getMaxFill())).toList(); }

    private void writeTanks(CompoundTag tag) { tanks[0].writeToNBT(tag, "liquid"); tanks[1].writeToNBT(tag, "gas"); }
    private void readTanks(CompoundTag tag) { tanks[0].readFromNBT(tag, "liquid"); tanks[1].readFromNBT(tag, "gas"); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        var list = new ListTag();
        for (int i = 0; i < 24; i++) if (!slots[i].isEmpty()) {
            var item = (CompoundTag) slots[i].save(lookup);
            item.putByte("slot", (byte) i);
            list.add(item);
        }
        tag.put("items", list);
        writeTanks(tag);
        tag.putBoolean("muffled", muffled);
        tag.putBoolean("tilted", tilted);
        if (customName != null) tag.putString("name", Component.Serializer.toJson(customName, lookup));
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        Arrays.fill(slots, ItemStack.EMPTY);
        for (var value : tag.getList("items", Tag.TAG_COMPOUND)) {
            var item = (CompoundTag) value;
            int index = item.getByte("slot");
            if (index >= 0 && index < 24) slots[index] = ItemStack.parseOptional(lookup, item);
        }
        readTanks(tag);
        muffled = tag.getBoolean("muffled");
        tilted = tag.getBoolean("tilted");
        customName = null;
        if (tag.contains("name")) {
            String value = tag.getString("name");
            try { customName = Component.Serializer.fromJson(value, lookup); }
            catch (com.google.gson.JsonParseException error) { customName = Component.literal(value); }
            if (customName == null && !value.isEmpty()) customName = Component.literal(value);
        }
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) { var tag = new CompoundTag(); writeTanks(tag); return tag; }
    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookup) { readTanks(tag); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(), lookup); }
    @Override public Component getDisplayName() { return customName == null ? Component.translatable("container.storageDrum") : customName; }
    @Override public void setCustomName(Component name) { customName = name; setChanged(); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new StorageDrumMenu(id, inventory, this, worldPosition); }
    @Override public boolean isMuffled() { return muffled; }
    @Override public void setMuffled(boolean value) { muffled = value; setChanged(); }

    @Override public int getContainerSize() { return 24; }
    @Override public boolean isEmpty() { return Arrays.stream(slots).allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return slots[slot]; }
    @Override public ItemStack removeItem(int slot, int count) { var result = slots[slot].split(count); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { var result = slots[slot]; slots[slot] = ItemStack.EMPTY; return result; }
    @Override public void setItem(int slot, ItemStack stack) { slots[slot] = stack; stack.limitSize(1); setChanged(); }
    @Override public int getMaxStackSize() { return 1; }
    @Override public void clearContent() { Arrays.fill(slots, ItemStack.EMPTY); setChanged(); }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && worldPosition.distToCenterSqr(player.position()) <= 144; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return SourceWasteIsotopes.is(stack,SourceWasteIsotopes.Form.LONG) || SourceWasteIsotopes.is(stack,SourceWasteIsotopes.Form.LONG_TINY)
            || SourceWasteIsotopes.is(stack,SourceWasteIsotopes.Form.SHORT) || SourceWasteIsotopes.is(stack,SourceWasteIsotopes.Form.SHORT_TINY) || stack.is(HbmContent.item("ingot_au198"));
    }
    @Override public int[] getSlotsForFace(Direction side) { return SLOTS; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return canPlaceItem(slot, stack); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return SourceWasteIsotopes.is(stack,SourceWasteIsotopes.Form.LONG_DEPLETED) || SourceWasteIsotopes.is(stack,SourceWasteIsotopes.Form.LONG_DEPLETED_TINY)
            || SourceWasteIsotopes.is(stack,SourceWasteIsotopes.Form.SHORT_DEPLETED) || SourceWasteIsotopes.is(stack,SourceWasteIsotopes.Form.SHORT_DEPLETED_TINY) || stack.is(HbmContent.item("ingot_mercury"));
    }
}
