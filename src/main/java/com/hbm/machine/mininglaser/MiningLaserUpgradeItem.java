package com.hbm.machine.mininglaser;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

/** The five genuine ItemMachineUpgrade() SPECIAL identities. They do not form an invented mutex or tiered upgrade family. */
public final class MiningLaserUpgradeItem extends Item {
    private final String kind;
    public MiningLaserUpgradeItem(String kind){super(new Properties().stacksTo(1));this.kind=kind;}
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> lines,TooltipFlag flag){
        lines.add(Component.translatable("tooltip.hbm.mining_laser.upgrade").withStyle(ChatFormatting.RED));
        lines.add(Component.translatable("tooltip.hbm.mining_laser."+kind));
        if(kind.equals("nullifier"))lines.add(Component.translatable("tooltip.hbm.mining_laser.nullifier2"));
    }
}
