package com.hbm.machine.shredder;

import com.hbm.api.machine.MachineView;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

/** Original 30-slot batch processing, including its positive-power and metadata-space quirks. */
public final class ShredderBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, EnergyReceiver, MachineView, com.hbm.multiblock.MuffledMachine {
    public static final long CAPACITY = 10_000;
    public static final int PROCESSING_SPEED = 60;
    private static final int[] IO = java.util.stream.IntStream.range(0, 30).toArray();
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(30, ItemStack.EMPTY);
    private PowerNode node;
    private long power;
    private int progress, soundCycle;
    public boolean muffled, tilted;
    private String customName = "";
    private final ContainerData menuData = new ContainerData() {
        @Override public int get(int index) { return switch (index) { case 0 -> (int) power & 0xFFFF; case 1 -> (int) (power >> 16) & 0xFFFF; case 2 -> progress; default -> 0; }; }
        @Override public void set(int index, int value) {}
        @Override public int getCount() { return 3; }
    };
    public ShredderBlockEntity(BlockPos pos, BlockState state) { super(ShredderContent.ENTITY.get(), pos, state); }
    public ContainerData menuData() { return menuData; }
    public int progress() { return progress; }
    public boolean hasPower() { return power > 0; }
    public boolean isProcessing() { return progress > 0; }
    public void setCustomName(String name) { customName = name; setChanged(); }
    @Override public Component getDisplayName() { return customName.isEmpty() ? Component.translatable("container.machineShredder") : Component.literal(customName); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new ShredderMenu(id, inventory, this, menuData); }
    public static void tick(Level level, BlockPos pos, BlockState state, ShredderBlockEntity machine) { if (level instanceof ServerLevel server) machine.tickServer(server); }
    private void tickServer(ServerLevel server) {
        ensurePowerNode(server);
        boolean changed = false;
        if (progress == 0) soundCycle = 0;
        if (hasPower() && canProcess()) {
            progress++;
            // Source checks only power > 0, so the last processing tick may leave -4 HE.
            power -= 5;
            if (progress == PROCESSING_SPEED) {
                for (int slot = 27; slot <= 28; slot++) {
                    ItemStack blade = inventory.get(slot);
                    if (blade.getMaxDamage() > 0) blade.setDamageValue(blade.getDamageValue() + 1);
                }
                progress = 0;
                processItems();
                changed = true;
            }
            if (soundCycle == 0) server.playSound(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), SoundEvents.MINECART_RIDING, SoundSource.NEUTRAL, muffled ? .1F : 1F, .75F);
            soundCycle++;
            if (soundCycle >= 50) soundCycle = 0;
        } else progress = 0;
        boolean trigger = true;
        if (hasPower() && canProcess() && progress == 0) trigger = false;
        if (trigger) changed = true;
        chargeFromBattery();
        if (changed) setChanged();
    }
    private void chargeFromBattery() {
        ItemStack stack = inventory.get(29);
        if (stack.getItem() instanceof CreativeBatteryItem) { power = CAPACITY; return; }
        if (stack.getItem() instanceof HbmBattery battery) {
            long discharged = Math.min(Math.min(CAPACITY - power, battery.getDischargeRate(stack)), battery.getCharge(stack));
            battery.dischargeBattery(stack, discharged); power += discharged;
        }
    }
    public static int gear(ItemStack stack) {
        if (!(stack.getItem() instanceof ShredderBladeItem)) return 0;
        if (stack.getMaxDamage() == 0) return 1;
        return stack.getDamageValue() < stack.getMaxDamage() / 2 ? 1 : stack.getDamageValue() != stack.getMaxDamage() ? 2 : 3;
    }
    public int gearLeft() { return gear(inventory.get(27)); }
    public int gearRight() { return gear(inventory.get(28)); }
    public boolean canProcess() {
        if (gearLeft() < 1 || gearLeft() > 2 || gearRight() < 1 || gearRight() > 2) return false;
        for (int slot = 0; slot < 9; slot++) if (!inventory.get(slot).isEmpty() && hasSpace(inventory.get(slot))) return true;
        return false;
    }
    public boolean hasSpace(ItemStack input) {
        if (level == null) return false;
        ItemStack result = com.hbm.ore.OreProcessing.maximumOutput(ShredderRecipes.result(level, input),com.hbm.ore.OreProcessing.rate(input));
        if (result.isEmpty()) return false;
        for (int slot = 9; slot < 27; slot++) {
            ItemStack output = inventory.get(slot);
            if (output.isEmpty() && result.getCount() <= result.getMaxStackSize()) return true;
            // Original hasSpace ignores metadata here, while the insertion loop checks it.
            if (ItemStack.isSameItem(output, result) && output.getCount() + result.getCount() <= result.getMaxStackSize()) return true;
        }
        return false;
    }
    private void processItems() {
        for (int inputSlot = 0; inputSlot < 9; inputSlot++) {
            ItemStack input = inventory.get(inputSlot);
            if (input.isEmpty() || !hasSpace(input)) continue;
            ItemStack result = com.hbm.ore.OreProcessing.output(ShredderRecipes.result(level, input),com.hbm.ore.OreProcessing.rate(input),level.random);
            if (result.isEmpty()) { input.shrink(1); continue; }
            boolean inserted = false;
            for (int outputSlot = 9; outputSlot < 27; outputSlot++) {
                ItemStack output = inventory.get(outputSlot);
                if (!output.isEmpty() && sameLegacyItem(output, result) && output.getCount() + result.getCount() <= result.getMaxStackSize()) {
                    output.grow(result.getCount()); input.shrink(1); inserted = true; break;
                }
            }
            if (!inserted) for (int outputSlot = 9; outputSlot < 27; outputSlot++) if (inventory.get(outputSlot).isEmpty()) {
                inventory.set(outputSlot, result.copy()); input.shrink(1); break;
            }
            if (input.isEmpty()) inventory.set(inputSlot, ItemStack.EMPTY);
        }
    }
    private static boolean sameLegacyItem(ItemStack a, ItemStack b) { return ItemStack.isSameItem(a, b) && ShredderRecipe.metadata(a) == ShredderRecipe.metadata(b); }
    private void ensurePowerNode(ServerLevel server) {
        if (node == null || node.isExpired()) {
            node = new PowerNode(worldPosition); node.addReceiver(this); HbmEnergy.nodespace(server).createNode(node);
        }
    }
    private void removePowerNode() { if (node != null && level instanceof ServerLevel server) { HbmEnergy.nodespace(server).destroyNode(node); node = null; } }
    @Override public void onChunkUnloaded() { removePowerNode(); super.onChunkUnloaded(); }
    @Override public void setRemoved() { removePowerNode(); super.setRemoved(); }
    @Override public boolean isLoaded() { return !isRemoved() && level != null; }
    @Override public long getPower() { return power; }
    @Override public void setPower(long power) { this.power = power; setChanged(); }
    @Override public long transferPower(long offered) { long accepted = Math.min(offered, CAPACITY - power); setPower(power + accepted); return offered - accepted; }
    @Override public long getMaxPower() { return CAPACITY; }
    @Override public long energyStored() { return power; }
    @Override public long energyCapacity() { return CAPACITY; }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup); tag.putLong("powerTime", power); tag.putBoolean("muffled", muffled); tag.putBoolean("tilted", tilted);
        ListTag items = new ListTag();
        for (int slot = 0; slot < inventory.size(); slot++) if (!inventory.get(slot).isEmpty()) { CompoundTag item = (CompoundTag) inventory.get(slot).save(lookup); item.putByte("slot", (byte) slot); items.add(item); }
        tag.put("items", items); if (!customName.isEmpty()) tag.putString("name", customName);
        // progress and soundCycle were deliberately absent from the original NBT.
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup); power = tag.getLong("powerTime"); muffled = tag.getBoolean("muffled"); tilted = tag.getBoolean("tilted");
        inventory.clear(); ListTag items = tag.getList("items", Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) { CompoundTag item = items.getCompound(i); int slot = item.getByte("slot"); if (slot >= 0 && slot < inventory.size()) inventory.set(slot, ItemStack.parseOptional(lookup, item)); }
        customName = tag.getString("name"); progress = 0; soundCycle = 0;
    }
    @Override public int getContainerSize() { return inventory.size(); }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeItem(int slot, int count) { ItemStack result = ContainerHelper.removeItem(inventory, slot, count); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }
    @Override public void setItem(int slot, ItemStack stack) { inventory.set(slot, stack); if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize()); setChanged(); }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5) <= 64; }
    @Override public void clearContent() { inventory.clear(); setChanged(); }
    @Override public int[] getSlotsForFace(Direction side) { return IO.clone(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot < 9) return !(stack.getItem() instanceof ShredderBladeItem); // Unknown inputs have the original scrap fallback.
        if (slot == 29) return stack.getItem() instanceof HbmBattery;
        return (slot == 27 || slot == 28) && stack.getItem() instanceof ShredderBladeItem;
    }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) {
        if (slot >= 9 && slot != 27 && slot != 28 || !canPlaceItem(slot, stack)) return false;
        if (inventory.get(slot).isEmpty()) return true;
        int size = inventory.get(slot).getCount();
        for (int i = 0; i < 9; i++) { ItemStack other = inventory.get(i); if (other.isEmpty() || sameLegacyItem(other, stack) && other.getCount() < size) return false; }
        return true;
    }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return slot >= 9 && slot <= 26 || slot >= 27 && slot <= 28 && stack.getMaxDamage() > 0 && stack.getDamageValue() == stack.getMaxDamage();
    }

 @Override public boolean isMuffled(){return muffled;}
 @Override public void setMuffled(boolean value){muffled=value;}
}
