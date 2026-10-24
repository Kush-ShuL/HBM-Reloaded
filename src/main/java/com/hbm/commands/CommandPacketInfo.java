package com.hbm.commands;

import com.hbm.network.dispatch.PacketDispatch;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** The five source operations; no aliases, implicit reset or persisted toggle. */
public final class CommandPacketInfo {
    public static void register(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("ntmpackets").requires(source->source.hasPermission(4))
                .executes(context->usage(context.getSource()))
                .then(Commands.argument("arguments",StringArgumentType.greedyString())
                        .executes(context->run(context.getSource(),StringArgumentType.getString(context,"arguments")))));
    }

    private static int run(CommandSourceStack source,String arguments){
        String operation=arguments.trim().split("\\s+",2)[0];
        // Unknown first arguments print the same red usage without touching the pool.
        if(!operation.equals("info")&&!operation.equals("resetState")&&!operation.equals("toggleThreadingStatus")&&!operation.equals("forceLock")&&!operation.equals("forceUnlock"))return usage(source);
        var pool=PacketDispatch.pool(source.getServer());
        switch(operation){
            case "info"->pool.info(source);
            case "resetState"->pool.resetState();
            case "toggleThreadingStatus"->source.sendSystemMessage(Component.translatable("commands.hbm.packet_dispatch.toggled",pool.toggleThreadingStatus()).withStyle(ChatFormatting.GREEN));
            case "forceLock"->{pool.forceLock(source.getTextName());source.sendSystemMessage(Component.translatable("commands.hbm.packet_dispatch.locked").withStyle(ChatFormatting.RED));}
            case "forceUnlock"->pool.forceUnlock(source.getTextName());
        }
        return 1;
    }

    private static int usage(CommandSourceStack source){source.sendSystemMessage(Component.translatable("commands.hbm.packet_dispatch.usage").withStyle(ChatFormatting.RED));return 0;}
    private CommandPacketInfo(){}
}
