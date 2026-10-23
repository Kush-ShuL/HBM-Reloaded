package com.hbm.machine.electricfurnace;

import com.hbm.api.machine.MachineView;
import com.hbm.energy.EnergyReceiver;
import com.hbm.energy.HbmEnergy;
import com.hbm.energy.LongEnergyStorage;
import com.hbm.energy.PowerNode;
import com.hbm.item.battery.CreativeBatteryItem;
import com.hbm.item.battery.HbmBattery;
import com.hbm.pollution.PollutionSystem;
import com.hbm.pollution.PollutionType;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** X5846 processing order, timings, reset/cooldown and HE math, adapted to 1.21.1 APIs. */
public final class ElectricFurnaceBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider, EnergyReceiver, MachineView {
    public static final long CAPACITY = 100_000L;
    private static final int[] IO_SLOTS = {0, 1, 2};
    private final NonNullList<ItemStack> inventory = NonNullList.withSize(4, ItemStack.EMPTY);
    private final LongEnergyStorage energy = new LongEnergyStorage(CAPACITY);
    private PowerNode node;
    private int progress;
    private int maxProgress = 100;
    private int consumption = 50;
    private int cooldown;
    private Component customName;
    private final ContainerData menuData = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> (int) (getPower() & 0xFFFF);
                case 1 -> (int) ((getPower() >>> 16) & 0xFFFF);
                case 2 -> progress;
                case 3 -> maxProgress;
                case 4 -> consumption;
                case 5 -> getBlockState().getBlock() instanceof ElectricFurnaceBlock block && block.active() ? 1 : 0;
                case 6 -> cooldown;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) { /* Server is authoritative. */ }
        @Override public int getCount() { return 7; }
    };

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) { super(ElectricFurnaceContent.ENTITY.get(), pos, state); }
    public ContainerData menuData() { return menuData; }
    public int progress() { return progress; }
    public int maxProgress() { return maxProgress; }
    public int consumption() { return consumption; }
    public int cooldown() { return cooldown; }
    public void setCustomName(Component name) { customName = name; setChanged(); }
    @Override public Component getDisplayName() { return customName == null ? Component.translatable("container.electricFurnace") : customName; }
    @Override public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) { return new ElectricFurnaceMenu(id, playerInventory, this, menuData); }

    public static void tick(Level level, BlockPos pos, BlockState state, ElectricFurnaceBlockEntity machine) {
        if (level instanceof ServerLevel server) machine.tickServer(server);
    }

    private void tickServer(ServerLevel server) {
        ensurePowerNode(server);
        boolean wasBurning = getBlockState().getBlock() instanceof ElectricFurnaceBlock block && block.active();
        long previousPower = getPower();
        int previousProgress = progress;
        if (cooldown > 0) cooldown--;
        chargeFromBattery();
        consumption = 50;
        maxProgress = 100;
        ItemStack upgradeStack = inventory.get(3);
        if (upgradeStack.getItem() instanceof FurnaceUpgradeItem upgrade) {
            int level = Math.min(upgrade.tier(), 3);
            if (upgrade.type() == FurnaceUpgradeItem.Type.SPEED) { maxProgress -= level * 25; consumption += level * 50; }
            if (upgrade.type() == FurnaceUpgradeItem.Type.POWER) { maxProgress += level * 10; consumption -= level * 15; }
        }
        if (!hasPower()) cooldown = 20;
        if (hasPower() && canProcess()) {
            progress++;
            energy.setPower(getPower() - consumption);
            if (server.getGameTime() % 20 == 0) PollutionSystem.increment(server, worldPosition, PollutionType.SOOT, PollutionSystem.SOOT_PER_SECOND);
            if (progress >= maxProgress) { progress = 0; processItem(); setChanged(); }
        } else {
            progress = 0;
        }
        boolean burning = progress > 0 || hasPower() && canProcess();
        if (wasBurning != burning) switchActiveBlock(server, burning);
        if (previousPower != getPower() || previousProgress != progress) setChanged();
    }

    private void chargeFromBattery() {
        ItemStack stack = inventory.get(0);
        if (stack.getItem() instanceof CreativeBatteryItem) { energy.setPower(CAPACITY); return; }
        if (stack.getItem() instanceof HbmBattery battery) {
            long amount = Math.min(Math.min(CAPACITY - getPower(), battery.getDischargeRate(stack)), battery.getCharge(stack));
            battery.dischargeBattery(stack, amount);
            energy.setPower(getPower() + amount);
        }
    }

    private RecipeHolder<SmeltingRecipe> recipe() {
        if (level == null || inventory.get(1).isEmpty()) return null;
        return level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(com.hbm.ore.RawOreContent.processingStack(inventory.get(1))), level).orElse(null);
    }

    private ItemStack result() {
        var recipe = recipe();
        return recipe == null ? ItemStack.EMPTY : recipe.value().assemble(new SingleRecipeInput(com.hbm.ore.RawOreContent.processingStack(inventory.get(1))), level.registryAccess());
    }

    public boolean hasPower() { return getPower() >= consumption; }
    public boolean canProcess() {
        if (inventory.get(1).isEmpty() || cooldown > 0) return false;
        ItemStack result = com.hbm.ore.OreProcessing.maximumOutput(result(), com.hbm.ore.OreProcessing.rate(inventory.get(1)));
        if (result.isEmpty()) return false;
        ItemStack output = inventory.get(2);
        if (output.isEmpty()) return result.getCount() <= Math.min(getMaxStackSize(), result.getMaxStackSize());
        if (!sameLegacyItem(output, result)) return false;
        return output.getCount() + result.getCount() <= Math.min(getMaxStackSize(), output.getMaxStackSize());
    }

    public static boolean sameLegacyItem(ItemStack first, ItemStack second) {
        return ItemStack.isSameItem(first, second) && first.getDamageValue() == second.getDamageValue()
                && first.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(), 0).equals(second.getOrDefault(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(), 0));
    }

    private void processItem() {
        if (!canProcess()) return;
        ItemStack result = com.hbm.ore.OreProcessing.output(result(), com.hbm.ore.OreProcessing.rate(inventory.get(1)), level.random);
        if (inventory.get(2).isEmpty()) inventory.set(2, result.copy());
        else if (sameLegacyItem(inventory.get(2), result)) inventory.get(2).grow(result.getCount());
        inventory.get(1).shrink(1);
        if (inventory.get(1).isEmpty()) inventory.set(1, ItemStack.EMPTY);
    }

    private void switchActiveBlock(ServerLevel server, boolean active) {
        BlockState next = (active ? ElectricFurnaceContent.ON.get() : ElectricFurnaceContent.OFF.get()).defaultBlockState().setValue(ElectricFurnaceBlock.FACING, getBlockState().getValue(ElectricFurnaceBlock.FACING));
        // onRemove keeps the shared BE; LevelChunk updates its state/ticker in place.
        server.setBlock(worldPosition, next, Block.UPDATE_CLIENTS);
        setChanged();
    }

    private void ensurePowerNode(ServerLevel server) {
        if (node == null || node.isExpired()) {
            node = new PowerNode(worldPosition);
            node.addReceiver(this);
            HbmEnergy.nodespace(server).createNode(node);
        }
    }
    @Override public void onLoad() { super.onLoad(); if (level instanceof ServerLevel server) ensurePowerNode(server); }
    @Override public void setRemoved() {
        if (node != null && level instanceof ServerLevel server) { HbmEnergy.nodespace(server).destroyNode(node); node = null; }
        super.setRemoved();
    }
    @Override public boolean isLoaded() { return !isRemoved() && level != null; }
    @Override public long getPower() { return energy.getPower(); }
    @Override public void setPower(long power) { energy.setPower(power); setChanged(); }
    @Override public long getMaxPower() { return CAPACITY; }
    @Override public java.util.List<ProgressView> progressBars() { if (!(progress > 0 || canProcess())) return java.util.List.of(); return java.util.List.of(new ProgressView("jade.hbm.processing",progress,maxProgress,0x71C78B)); }
    @Override public long energyStored() { return getPower(); }
    @Override public long energyCapacity() { return CAPACITY; }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup);
        tag.putLong("power", getPower()); tag.putInt("progress", progress);
        ListTag items = new ListTag();
        for (int slot = 0; slot < inventory.size(); slot++) {
            if (inventory.get(slot).isEmpty()) continue;
            CompoundTag item = (CompoundTag) inventory.get(slot).save(lookup);
            item.putByte("slot", (byte) slot); items.add(item);
        }
        tag.put("items", items);
        if (customName != null) tag.putString("name", customName.getString());
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup);
        energy.setPower(Math.max(0, Math.min(CAPACITY, tag.getLong("power"))));
        progress = tag.getInt("progress");
        inventory.clear();
        ListTag items = tag.getList("items", Tag.TAG_COMPOUND);
        for (int i = 0; i < items.size(); i++) {
            CompoundTag item = items.getCompound(i);
            int slot = item.getByte("slot");
            if (slot >= 0 && slot < inventory.size()) inventory.set(slot, ItemStack.parseOptional(lookup, item));
        }
        customName = tag.getString("name").isEmpty() ? null : Component.literal(tag.getString("name"));
    }
    @Override public int getContainerSize() { return 4; }
    @Override public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return inventory.get(slot); }
    @Override public ItemStack removeItem(int slot, int amount) { ItemStack result = ContainerHelper.removeItem(inventory, slot, amount); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }
    @Override public void setItem(int slot, ItemStack stack) { inventory.set(slot, stack); if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize()); setChanged(); }
    @Override public boolean stillValid(Player player){return com.hbm.multiblock.SourceMachineMenuRange.isValid(this,player);}
    @Override public void clearContent() { inventory.clear(); setChanged(); }
    @Override public int[] getSlotsForFace(Direction side) { return IO_SLOTS.clone(); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0) return stack.getItem() instanceof HbmBattery;
        return slot == 1 && level != null && level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(com.hbm.ore.RawOreContent.processingStack(stack)), level).isPresent();
    }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return canPlaceItem(slot, stack); }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return slot == 2; }
}
