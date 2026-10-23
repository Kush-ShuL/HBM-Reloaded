package com.hbm.block.pipe;

import com.hbm.hazard.HbmHazards;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public final class PipeBlockItem extends BlockItem {
    public PipeBlockItem(NativePipeBlock block) { super(block, new Properties()); }
    public ItemStack stack(int variant) { var stack = new ItemStack(this); if (variant != 0) stack.set(HbmHazards.LEGACY_METADATA.get(), variant); return stack; }
    public int variantCount() { return ((NativePipeBlock) getBlock()).kind() == PipeKind.BOX ? 15 : ((NativePipeBlock) getBlock()).kind() == PipeKind.EXHAUST ? 5 : ((NativePipeBlock) getBlock()).kind() == PipeKind.STANDARD ? 3 : 1; }
    public List<ItemStack> creativeStacks() { var result = new java.util.ArrayList<ItemStack>(); for (int i = 0; i < variantCount(); i++) result.add(stack(((NativePipeBlock) getBlock()).kind() == PipeKind.EXHAUST ? i * 3 : i)); return result; }
    @Override protected BlockState getPlacementState(BlockPlaceContext context) {
        var state = super.getPlacementState(context); if (state == null) return null;
        int meta = Math.abs(context.getItemInHand().getOrDefault(HbmHazards.LEGACY_METADATA.get(), 0));
        var kind = ((NativePipeBlock) getBlock()).kind();
        if (kind.box()) return state.setValue(NativePipeBlock.VARIANT, meta % 3).setValue(SpecialPipeBlock.SIZE, meta % 15 / 3);
        return kind == PipeKind.STANDARD ? state.setValue(NativePipeBlock.VARIANT, meta % 3) : state;
    }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        if (((NativePipeBlock) getBlock()).kind().anchor()) {
            tooltip.add(Component.translatable("tooltip.hbm.network.connection_type",Component.translatable("tooltip.hbm.network.single").withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GOLD));
            tooltip.add(Component.translatable("tooltip.hbm.network.range",Component.translatable("tooltip.hbm.network.metres",10).withStyle(ChatFormatting.YELLOW)).withStyle(ChatFormatting.GOLD));
        }
    }
}
