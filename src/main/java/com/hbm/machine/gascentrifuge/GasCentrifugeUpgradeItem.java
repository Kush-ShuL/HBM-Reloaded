package com.hbm.machine.gascentrifuge;
import java.util.List;import net.minecraft.ChatFormatting;import net.minecraft.network.chat.Component;import net.minecraft.world.item.*;
/** Original SPECIAL upgrade, not the ordinary SPEED tier family. */
public final class GasCentrifugeUpgradeItem extends Item {public GasCentrifugeUpgradeItem(){super(new Properties().stacksTo(1));}@Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){lines.add(Component.translatable("upgrade.hbm.gascentrifuge.title").withStyle(ChatFormatting.RED));lines.add(Component.translatable("upgrade.hbm.gascentrifuge.separation"));lines.add(Component.translatable("upgrade.hbm.gascentrifuge.speed").withStyle(ChatFormatting.YELLOW));}}
