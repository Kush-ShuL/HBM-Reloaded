package com.hbm.item;

import java.util.List;
import java.util.function.BooleanSupplier;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/** The source no-spawn hint and ITooltipProvider's exact left-shift convention. */
public final class BasicBlockItem extends BlockItem {
    public static BooleanSupplier leftShiftHeld = () -> false;
    private final String sourceClass;
    private final String id;

    public BasicBlockItem(Block block, Properties properties, String sourceClass, String id) {
        super(block, sourceProperties(properties, id));
        this.sourceClass = sourceClass;
        this.id = id;
    }

    private static Properties sourceProperties(Properties properties, String id) {
        return switch (id) {
            case "gravel_diamond" -> properties.rarity(Rarity.RARE);
            case "block_euphemium", "block_euphemium_cluster" -> properties.rarity(Rarity.EPIC);
            default -> properties;
        };
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
        // The source ItemBlockLore and ItemPrototypeBlock wrappers belong to these actual items.
        if (id.equals("meteor_battery")) {
            lines.add(Component.translatable("tooltip.hbm.block.meteor_battery.tesla"));
        } else if (id.equals("gravel_diamond")) {
            for (int line = 0; line < 16; line++) {
                lines.add(line == 2 || line == 7 || line == 14 ? Component.empty()
                        : Component.translatable("tooltip.hbm.block.gravel_diamond." + line));
            }
        } else if (id.equals("nuke_prototype")) {
            lines.add(Component.translatable("tooltip.hbm.block.nuke_prototype.regret"));
            lines.add(Component.empty());
            lines.add(Component.translatable("tooltip.hbm.block.nuke_prototype.monster"));
        }
    }
}
