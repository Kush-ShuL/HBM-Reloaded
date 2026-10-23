package com.hbm.item.toolbox;

import com.hbm.machine.storage.CrateContents;
import com.hbm.machine.storage.CrateStackData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Actual source ItemToolBox: three8-slot rows rotate through the eight non-selected hotbar slots. */
public final class ToolboxItem extends Item {
    public ToolboxItem() { super(new Properties().stacksTo(1)); }
    @Override public Component getName(ItemStack stack) { return Component.translatable("item.toolbox.name"); }
    @Override public int getUseDuration(ItemStack stack, LivingEntity holder) { return 1; }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flags) {
        lines.add(Component.translatable("tooltip.hbm.toolbox.swap"));
        lines.add(Component.translatable("tooltip.hbm.toolbox.open"));
    }
    public List<Integer> getActiveRows(ItemStack box, Player player) {
        var stacks = ToolboxInventory.readRows(box, player.registryAccess());
        var active = new ArrayList<Integer>();
        if (stacks == null) return active;
        for (int row = 0; row < 3; row++) for (int slot = 0; slot < 8; slot++) {
            if (!stacks[row * 8 + slot].isEmpty()) { active.add(row); break; }
        }
        return active;
    }
    public void moveRows(ItemStack box, Player player) {
        var endingHotbar = new ItemStack[9]; Arrays.fill(endingHotbar, ItemStack.EMPTY);
        var transfer = new ItemStack[8]; Arrays.fill(transfer, ItemStack.EMPTY);
        boolean hasToolbox = false;
        int extraToolboxes = 0;
        for (int i = 0; i < 9; i++) {
            var stack = player.getInventory().getItem(i);
            if (stack.is(this) && i != player.getInventory().selected) {
                extraToolboxes++;
                player.drop(stack, true);
                player.getInventory().setItem(i, ItemStack.EMPTY);
            } else if (i == player.getInventory().selected) {
                hasToolbox = true;
                endingHotbar[i] = stack;
            } else transfer[i - (hasToolbox ? 1 : 0)] = stack;
        }
        if (extraToolboxes > 0) player.displayClientMessage((extraToolboxes == 1
                ? Component.translatable("message.hbm.toolbox.nested")
                : Component.translatable("message.hbm.toolbox.nested_many", extraToolboxes)).withStyle(ChatFormatting.RED), false);

        var stacks = ToolboxInventory.readRows(box, player.registryAccess());
        var ending = new ItemStack[24]; Arrays.fill(ending, ItemStack.EMPTY);
        int lowestActive = Integer.MAX_VALUE;
        int lowestInactive = Integer.MAX_VALUE;
        if (stacks != null) {
            var activeRows = getActiveRows(box, player);
            for (int i = 0; i < 3; i++) {
                if (activeRows.contains(i)) lowestActive = Math.min(i, lowestActive);
                else lowestInactive = Math.min(i, lowestInactive);
            }
            if (lowestInactive > 2) lowestInactive = 2;
            else lowestInactive = Math.max(0, lowestInactive - 1);
            for (int row : activeRows) {
                int from = row * 8;
                if (row == lowestActive) {
                    hasToolbox = false;
                    for (int i = 0; i < 9; i++) {
                        if (i == player.getInventory().selected) { hasToolbox = true; continue; }
                        endingHotbar[i] = stacks[from + i - (hasToolbox ? 1 : 0)];
                    }
                    continue;
                }
                System.arraycopy(stacks, from, ending, (row - 1) * 8, 8);
            }
        } else lowestInactive = 0;
        System.arraycopy(transfer, 0, ending, lowestInactive * 8, 8);
        for (int i = 0; i < 9; i++) player.getInventory().setItem(i, endingHotbar[i]);

        // Literal source setTagCompound(new NBTTagCompound()): rotation discards old source name/enchant/other NBT.
        CrateStackData.clearSourceData(box);
        ToolboxInventory.writeRows(box, ending, player.registryAccess());
        try {
            if (CrateStackData.compressedSize(box, player.registryAccess()) > 6000) {
                player.displayClientMessage(Component.translatable("message.hbm.storage_crate.overflow").withStyle(ChatFormatting.RED), false);
                CrateContents.ejectHeld(player, new ToolboxInventory(player, box));
                CrateStackData.clearSourceData(box);
            }
        } catch (IOException ignored) { }
    }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer server) {
            if (!player.isShiftKeyDown()) {
                moveRows(stack, player);
                player.inventoryMenu.broadcastChanges();
            } else {
                var data = CrateStackData.data(stack);
                data.putBoolean("isOpen", true);
                CrateStackData.data(stack, data);
                server.openMenu(new SimpleMenuProvider((id, inventory, viewer) -> new ToolboxMenu(id, inventory, hand),
                        stack.has(net.minecraft.core.component.DataComponents.CUSTOM_NAME) ? stack.getHoverName() : Component.translatable("container.toolBox")),
                        buffer -> buffer.writeEnum(hand));
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
