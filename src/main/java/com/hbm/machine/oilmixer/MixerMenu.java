package com.hbm.machine.oilmixer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
public final class MixerMenu extends AbstractContainerMenu {
 public final BlockPos position;private final Container machine;
 public MixerMenu(int id, Inventory inv, RegistryFriendlyByteBuf data) {
     this(id, inv, data.readBlockPos());
 }
 private MixerMenu(int id, Inventory inv, BlockPos pos) {
     this(id, inv, inv.player.level().getBlockEntity(pos) instanceof MixerEntity mixer ? mixer : new SimpleContainer(5), pos);
 }
 public MixerMenu(int id, Inventory inv, Container machine, BlockPos pos) {
     super(MixerContent.MENU.get(), id);
     this.position = pos;
     this.machine = machine;
     int[] x = {12, 52, 126, 148, 148};
     int[] y = {72, 72, 72, 18, 36};
     for (int slot = 0; slot < x.length; slot++) {
         addSlot(new Slot(machine, slot, x[slot], y[slot]) {
             @Override public boolean mayPlace(ItemStack stack) {
                 return container.canPlaceItem(getContainerSlot(), stack);
             }
         });
     }
     for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) addSlot(new Slot(inv, 9 + row * 9 + col, 8 + col * 18, 122 + row * 18));
     }
     for (int col = 0; col < 9; col++) addSlot(new Slot(inv, col, 8 + col * 18, 180));
 }
 public void cycle(){if(machine instanceof MixerEntity mixer)mixer.cycle();}
 @Override public boolean stillValid(Player player){return machine instanceof MixerEntity mixer?mixer.stillValid(player):position.distToCenterSqr(player.position())<=144;}
 @Override public ItemStack quickMoveStack(Player player, int index) {
     var slot = getSlot(index);
     if (!slot.hasItem()) return ItemStack.EMPTY;
     var stack = slot.getItem();
     var original = stack.copy();
     if (index < 5) {
         if (!moveItemStackTo(stack, 5, slots.size(), true)) return ItemStack.EMPTY;
     } else {
         for (int target = 0; target < 5 && !stack.isEmpty(); target++) {
             if (getSlot(target).mayPlace(stack)) moveItemStackTo(stack, target, target + 1, false);
         }
     }
     if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
     if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
     else slot.setChanged();
     slot.onTake(player, stack);
     return original;
 }
}
