package com.hbm.commands;

import com.hbm.radiation.HbmRadiation;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** Permission-two source command; operates on the sender's dimension and cell. */
@EventBusSubscriber(modid = "hbm")
public final class CommandRadiation {
    private static final String USAGE = "/ntmrad <set/clear>";

    @SubscribeEvent public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ntmrad")
                .requires(source -> source.hasPermission(2))
                .executes(context -> usage(context.getSource()))
                .then(Commands.literal("clear")
                        .executes(context -> clear(context.getSource()))
                        .then(Commands.argument("extra", StringArgumentType.greedyString())
                                .executes(context -> usage(context.getSource()))))
                .then(Commands.literal("set")
                        .executes(context -> usage(context.getSource()))
                        .then(Commands.argument("amount", DoubleArgumentType.doubleArg(0D, 100_000D))
                                .executes(context -> set(context.getSource(), DoubleArgumentType.getDouble(context, "amount")))
                                .then(Commands.argument("extra", StringArgumentType.greedyString())
                                        .executes(context -> usage(context.getSource())))))
                .then(Commands.argument("invalid", StringArgumentType.greedyString())
                        .executes(context -> usage(context.getSource()))));
    }

    private static int clear(CommandSourceStack source) {
        HbmRadiation.clearSystem(source.getLevel());
        source.sendSystemMessage(Component.translatable("commands.hbm.radiation.cleared"));
        return 1;
    }

    private static int set(CommandSourceStack source, double amount) {
        var pos = BlockPos.containing(source.getPosition());
        HbmRadiation.setRadiation(source.getLevel(), pos.getX(), pos.getY(), pos.getZ(), (float) amount);
        source.sendSystemMessage(Component.translatable("commands.hbm.radiation.set"));
        return 1;
    }

    private static int usage(CommandSourceStack source) {
        source.sendSystemMessage(Component.literal(USAGE).withStyle(ChatFormatting.RED));
        return 0;
    }

    private CommandRadiation() {}
}
