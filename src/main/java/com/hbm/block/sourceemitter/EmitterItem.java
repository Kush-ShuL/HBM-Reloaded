package com.hbm.block.sourceemitter;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;

/** Source ItemBlockBase -> ITooltipProvider gold hints, translated without changing the four controls. */
public final class EmitterItem extends BlockItem {
    private final boolean light;
    public EmitterItem(Block block, boolean light) { super(block, new Properties()); this.light = light; }
    @Override public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        if (light) {
            lines.add(Component.translatable("tooltip.hbm.emitter.widen").withStyle(ChatFormatting.GOLD));
            lines.add(Component.translatable("tooltip.hbm.emitter.narrow").withStyle(ChatFormatting.GOLD));
        }
        lines.add(Component.translatable("tooltip.hbm.emitter.cycle").withStyle(ChatFormatting.GOLD));
        if (light) lines.add(Component.translatable("tooltip.hbm.emitter.color").withStyle(ChatFormatting.GOLD));
    }
}
