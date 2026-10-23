package com.hbm.item.powernettool;
import com.hbm.energy.*;
import com.hbm.multiblock.DummyableBlock;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
/** Actual ItemPowerNetTool: read-only diagnostics of the one real HE network. */
public final class PowerNetToolItem extends Item {
    public PowerNetToolItem() { super(new Properties().stacksTo(1)); }
    @Override public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel(); var player = context.getPlayer(); if (player == null) return InteractionResult.PASS;
        BlockPos pos = context.getClickedPos();
        if (level.getBlockState(pos).getBlock() instanceof DummyableBlock machine) { var core = machine.findCore(level,pos); if (core != null) pos = core; }
        var entity = level.getBlockEntity(pos);
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(entity instanceof EnergyConductor) || !(level instanceof ServerLevel server)) return InteractionResult.PASS;
        var node = HbmEnergy.nodespace(server).getNode(pos);
        if (node == null || !node.hasValidNet()) { player.displayClientMessage(Component.translatable("item.power_net_tool.error").withStyle(ChatFormatting.RED),false); return InteractionResult.CONSUME; }
        var net = node.network(); String id = Integer.toHexString(net.hashCode());
        player.displayClientMessage(Component.translatable("item.power_net_tool.start",id).withStyle(ChatFormatting.GOLD),false);
        player.displayClientMessage(Component.translatable("item.power_net_tool.links",net.nodes().size()).withStyle(ChatFormatting.YELLOW),false);
        player.displayClientMessage(Component.translatable("item.power_net_tool.providers",net.providers().size()).withStyle(ChatFormatting.YELLOW),false);
        player.displayClientMessage(Component.translatable("item.power_net_tool.receivers",net.receivers().size()).withStyle(ChatFormatting.YELLOW),false);
        player.displayClientMessage(Component.translatable("item.power_net_tool.flow",String.format(Locale.US,"%,d",net.getTransferredExact())).withStyle(ChatFormatting.YELLOW),false);
        player.displayClientMessage(Component.translatable("item.power_net_tool.end",id).withStyle(ChatFormatting.GOLD),false);
        for (PowerLink link : net.nodes()) if (link instanceof PowerNode real) for (BlockPos at : real.positions()) PowerNetToolNotice.send(server,at,id);
        return InteractionResult.CONSUME;
    }
    @Override public void appendHoverText(ItemStack stack,TooltipContext context,List<Component> text,TooltipFlag flag) {
        for (int i=0;i<7;i++) text.add(Component.translatable("item.power_net_tool.desc."+i).withStyle(ChatFormatting.RED));
    }
}
