package com.hbm.commands;

import com.hbm.config.ServerSettingsAccess;
import com.hbm.config.ServerSettingsAccess.Binding;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** The source command permits every player and excludes console senders. */
@EventBusSubscriber(modid = "hbm")
public final class CommandReloadServer {
    @SubscribeEvent public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ntmserver")
                .requires(source -> source.getEntity() instanceof ServerPlayer)
                .executes(context -> usage(context.getSource()))
                .then(Commands.argument("arguments", StringArgumentType.greedyString())
                        .suggests((context, builder) -> suggest(builder))
                        .executes(context -> execute(context.getSource(), StringArgumentType.getString(context, "arguments")))));
    }

    private static CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder) {
        String remaining = builder.getRemaining();
        int firstSpace = remaining.indexOf(' ');
        if (firstSpace < 0) return SharedSuggestionProvider.suggest(List.of("list", "reload", "get", "set"), builder);
        String operation = remaining.substring(0, firstSpace);
        String rest = remaining.substring(firstSpace + 1);
        if (("get".equals(operation) || "set".equals(operation)) && rest.indexOf(' ') < 0)
            return SharedSuggestionProvider.suggest(ServerSettingsAccess.keys(), builder.createOffset(builder.getStart() + firstSpace + 1));
        return builder.buildFuture();
    }

    private static int execute(CommandSourceStack source, String raw) {
        String[] args = raw.trim().split("\\s+");
        String operation = args[0];
        if ("help".equals(operation)) return help(source, args);
        if ("list".equals(operation)) {
            source.sendSystemMessage(Component.translatable("commands.hbm.server.title").withStyle(ChatFormatting.RED));
            try {
                for (String key : ServerSettingsAccess.keys()) show(source, ServerSettingsAccess.binding(key), "  ");
                return 1;
            } catch (Exception failure) { return failed(source, "commands.hbm.server.access_failed", failure); }
        }
        if ("reload".equals(operation)) {
            try {
                ServerSettingsAccess.reload();
                message(source, "commands.hbm.server.loaded");
                return 1;
            } catch (Exception failure) { return failed(source, "commands.hbm.server.reload_failed", failure); }
        }
        if (args.length < 2) return usage(source);
        if ("get".equals(operation)) {
            Binding binding = ServerSettingsAccess.binding(args[1]);
            if (binding == null) return missing(source);
            try { show(source, binding, ""); return 1; }
            catch (Exception failure) { return failed(source, "commands.hbm.server.access_failed", failure); }
        }
        if (args.length < 3) return usage(source);
        if ("set".equals(operation)) {
            Binding binding = ServerSettingsAccess.binding(args[1]);
            if (binding == null) return missing(source);
            try {
                ServerSettingsAccess.set(binding, args[2]);
                message(source, "commands.hbm.server.updated");
                fixedDamageNote(source, binding);
                return 1;
            } catch (Exception failure) {
                source.sendFailure(Component.translatable("commands.hbm.server.parse_failed", binding.kind().typeName(), detail(failure)));
                return 0;
            }
        }
        return usage(source);
    }

    private static void show(CommandSourceStack source, Binding binding, String prefix) {
        source.sendSystemMessage(Component.literal(prefix + binding.key() + ": ").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(ServerSettingsAccess.read(binding)).withStyle(ChatFormatting.YELLOW)));
        fixedDamageNote(source, binding);
    }
    private static void fixedDamageNote(CommandSourceStack source, Binding binding) {
        if (binding.fixedDamage()) message(source, "commands.hbm.server.fixed_damage_note");
    }
    private static int help(CommandSourceStack source, String[] args) {
        if (args.length >= 2) {
            if (List.of("help", "list", "reload", "get", "set").contains(args[1]))
                message(source, "commands.hbm.server.help." + args[1]);
        } else {
            helpLine(source, "help ", "command");
            helpLine(source, "list", null);
            helpLine(source, "reload", null);
            helpLine(source, "get ", "name");
            helpLine(source, "set ", "name_value");
        }
        return 1;
    }
    private static void helpLine(CommandSourceStack source, String operation, String placeholder) {
        var text = Component.literal("/ntmserver ").withStyle(ChatFormatting.YELLOW)
                .append(Component.literal(operation).withStyle(ChatFormatting.GOLD));
        if (placeholder != null) text.append(Component.translatable("commands.hbm.server.placeholder." + placeholder).withStyle(ChatFormatting.RED));
        source.sendSystemMessage(text);
    }
    private static void message(CommandSourceStack source, String key) {
        source.sendSystemMessage(Component.translatable(key).withStyle(ChatFormatting.YELLOW));
    }
    private static int missing(CommandSourceStack source) {
        source.sendFailure(Component.translatable("commands.hbm.server.missing"));
        return 0;
    }
    private static int failed(CommandSourceStack source, String key, Exception failure) {
        source.sendFailure(Component.translatable(key, detail(failure)));
        return 0;
    }
    private static String detail(Exception failure) {
        return failure.getLocalizedMessage() == null ? failure.getClass().getSimpleName() : failure.getLocalizedMessage();
    }
    private static int usage(CommandSourceStack source) {
        source.sendFailure(Component.literal("/ntmserver help"));
        return 0;
    }
    private CommandReloadServer() {}
}
