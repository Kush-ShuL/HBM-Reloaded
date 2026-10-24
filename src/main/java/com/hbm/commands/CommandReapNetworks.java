package com.hbm.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** Actual source administrative reap; all private nodespaces are scoped to this server. */
@EventBusSubscriber(modid="hbm")
public final class CommandReapNetworks {
    @SubscribeEvent public static void register(RegisterCommandsEvent event){
        event.getDispatcher().register(Commands.literal("ntmreapnetworks").requires(source->source.hasPermission(4))
                .executes(context->run(context.getSource()))
                .then(Commands.argument("ignored",StringArgumentType.greedyString()).executes(context->run(context.getSource()))));
    }
    private static int run(CommandSourceStack source){
        try{
            var server=source.getServer();
            com.hbm.energy.HbmEnergy.clearServer(server);
            com.hbm.fluid.network.FluidNetworks.clearServer(server);
            com.hbm.machine.foundry.FoundryNetwork.clearServer(server);
            com.hbm.fusion.plasma.PlasmaNetworks.clearServer(server);
            com.hbm.fusion.klystron.KlystronNetworks.clearServer(server);
            com.hbm.pneumatic.PneumaticNetworks.clearServer(server);
            com.hbm.block.rebar.RebarNodespace.clearServer(server);
            source.sendSystemMessage(Component.translatable("commands.hbm.network_reap.cleared").withStyle(ChatFormatting.YELLOW));
            return 1;
        }catch(RuntimeException error){
            separator(source);
            source.sendSystemMessage(Component.translatable("commands.hbm.network_reap.failed").withStyle(ChatFormatting.RED));
            source.sendSystemMessage(Component.literal(String.valueOf(error.getLocalizedMessage())).withStyle(ChatFormatting.RED));
            var trace=error.getStackTrace();
            source.sendSystemMessage(Component.literal(trace.length==0?error.toString():trace[0].toString()).withStyle(ChatFormatting.RED));
            separator(source);
            throw error;
        }
    }
    private static void separator(CommandSourceStack source){source.sendSystemMessage(Component.literal("----------------------------------").withStyle(ChatFormatting.GRAY));}
    private CommandReapNetworks(){}
}
