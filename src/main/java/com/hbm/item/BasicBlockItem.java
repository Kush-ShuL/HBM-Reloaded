package com.hbm.item;

import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/** The source no-spawn hint and ITooltipProvider's exact left-shift convention. */
public final class BasicBlockItem extends BlockItem {
    public static BooleanSupplier leftShiftHeld = () -> false;
    private final String sourceClass;
    private final String id;

    public BasicBlockItem(Block block, Properties properties, String sourceClass, String id) {
        super(block, properties);
        this.sourceClass = sourceClass;
        this.id = id;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);
        if (sourceClass.equals("BlockNoSpawn")) {
            lines.add(Component.literal("\u00a7c" + Language.getInstance().getOrDefault("tile.nospawn")));
        } else if (sourceClass.equals("BlockGenericTooltip")) {
            if (leftShiftHeld.getAsBoolean()) {
                for (String line : Language.getInstance().getOrDefault("tile." + id + ".desc").split("\\$")) {
                    lines.add(Component.literal("\u00a7e" + line));
                }
            } else {
                lines.add(TooltipHints.moreInfo());
            }
        }
    }
}
