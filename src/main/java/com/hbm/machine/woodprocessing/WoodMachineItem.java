package com.hbm.machine.woodprocessing;
import com.hbm.hazard.HbmHazards;
import com.hbm.item.*;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
/** Source standard shift lore and ItemBlockMulti's damaged-blade value1 only for the sawmill. */
public final class WoodMachineItem extends BlockItem {
    private final String id;
    public WoodMachineItem(Block block,String id){super(block,id.equals("machine_sawmill")?new Properties().component(HbmHazards.LEGACY_METADATA.get(),0):new Properties());this.id=id;}
    public static boolean broken(ItemStack stack){return stack.getOrDefault(HbmHazards.LEGACY_METADATA.get(),0)==1;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){super.appendHoverText(stack,context,lines,flag);if(BasicBlockItem.leftShiftHeld.getAsBoolean())for(var line:Component.translatable("tile."+id+".desc").getString().split("\\$"))lines.add(Component.literal(line).withStyle(ChatFormatting.YELLOW));else lines.add(TooltipHints.moreInfo());}
}
