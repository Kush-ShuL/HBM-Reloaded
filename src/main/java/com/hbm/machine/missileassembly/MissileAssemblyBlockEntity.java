package com.hbm.machine.missileassembly;

import com.hbm.missile.custom.*;
import com.hbm.registry.HbmSounds;
import java.util.Arrays;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** Original six-slot, unpowered, immediate component assembler. All automated insertion/extraction is forbidden. */
public final class MissileAssemblyBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    private final ItemStack[] slots = new ItemStack[6];
    private String customName;
    public CustomMissileStruct load;
    public MissileAssemblyBlockEntity(BlockPos pos, BlockState state) { super(MissileAssemblyContent.ENTITY.get(), pos, state); Arrays.fill(slots, ItemStack.EMPTY); }
    @Override public Component getDisplayName() { return customName == null || customName.isEmpty() ? Component.translatable("container.missileAssembly") : Component.literal(customName); }
    public void setCustomName(Component name) { customName = name.getString(); setChanged(); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory player, Player opener) { return new MissileAssemblyMenu(id, player, this, worldPosition); }
    public void construct() {
        if (level == null || level.isClientSide || !MissileAssemblyRules.canBuild(this)) return;
        slots[5] = CustomMissileItem.build(slots[0], slots[1], slots[2], slots[3], slots[4]).copy();
        if (MissileAssemblyRules.stability(this) == 1) slots[3] = ItemStack.EMPTY;
        // Original clears these stacks, rather than subtracting one. Inputs deliberately have ordinary unrestricted GUI slots.
        slots[0] = slots[1] = slots[2] = slots[4] = ItemStack.EMPTY;
        level.playSound(null, worldPosition, HbmSounds.get("block.missileAssembly2"), SoundSource.BLOCKS, 1, 1);
        setChanged();
    }
    public static void tick(Level level, BlockPos pos, BlockState state, MissileAssemblyBlockEntity machine) {
        if (!(level instanceof ServerLevel server)) return;
        // Original TEMissileMultipartPacket is sent each tick to a 250-block radius, independently of an open menu.
        var packet = machine.getUpdatePacket();
        for (var player : server.players()) if (player.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 250 * 250) player.connection.send(packet);
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.saveAdditional(tag, lookup); var items = new ListTag();
        for (int slot = 0; slot < slots.length; slot++) if (!slots[slot].isEmpty()) {
            var entry = (CompoundTag) slots[slot].save(lookup); entry.putByte("slot", (byte) slot); items.add(entry);
        }
        tag.put("items", items); if (customName != null) tag.putString("name", customName);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookup) {
        super.loadAdditional(tag, lookup); Arrays.fill(slots, ItemStack.EMPTY);
        var items = tag.getList("items", Tag.TAG_COMPOUND);
        for (int index = 0; index < items.size(); index++) { var entry = items.getCompound(index); int slot = entry.getByte("slot"); if (slot >= 0 && slot < slots.length) slots[slot] = ItemStack.parseOptional(lookup, entry); }
        customName = tag.getString("name");
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
        var tag = new CompoundTag(); tag.put("multipart", new CustomMissileStruct(slots[1], slots[2], slots[3], slots[4]).write());
        if (customName != null) tag.putString("name", customName); return tag;
    }
    @Override public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookup) { load = CustomMissileStruct.read(tag.getCompound("multipart")); customName = tag.getString("name"); }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public void onDataPacket(net.minecraft.network.Connection connection, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookup) { handleUpdateTag(packet.getTag(), lookup); }
    @Override public int getContainerSize() { return 6; }
    @Override public boolean isEmpty() { return Arrays.stream(slots).allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return slots[slot]; }
    @Override public ItemStack removeItem(int slot, int count) { var result = slots[slot].split(count); if (!result.isEmpty()) setChanged(); return result; }
    @Override public ItemStack removeItemNoUpdate(int slot) { var stack = slots[slot]; slots[slot] = ItemStack.EMPTY; return stack; }
    @Override public void setItem(int slot, ItemStack stack) { slots[slot] = stack; if (stack.getCount() > 64) stack.setCount(64); setChanged(); }
    @Override public void clearContent() { Arrays.fill(slots, ItemStack.EMPTY); setChanged(); }
    @Override public boolean stillValid(Player player) { return level != null && level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5) <= 64; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return false; }
    @Override public int[] getSlotsForFace(Direction side) { return new int[]{0}; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return false; }
}
