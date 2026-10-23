package com.hbm.machine.generator.steamengine;
import com.hbm.item.BasicBlockItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
public final class SteamEngineItem extends BlockItem {
    public SteamEngineItem(Block block,Properties properties){super(block,properties);}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean()){for(String line:Component.translatable("tile.machine_steam_engine.desc").getString().split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));}else lines.add(Component.translatable("tooltip.hbm.steam_engine.more_info"));}
}
