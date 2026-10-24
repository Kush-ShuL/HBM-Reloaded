package com.hbm.commands;

import com.hbm.HbmReloaded;
import com.hbm.world.structure.NaturalStructureLocator;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/** Original player-only permission-two /ntmlocate, using native natural starts without loading chunks. */
@EventBusSubscriber(modid = HbmReloaded.MOD_ID)
public final class CommandLocate {
    private static final int VISITS_PER_TICK = 4096;
    private static final long SLICE_NANOS = 2_000_000L;
    // One resumable search per server. Native generation and resource replacement share this thread.
    private static final Map<MinecraftServer, Work> RUNNING = new IdentityHashMap<>();
    private record Work(CommandSourceStack source, ServerPlayer player, net.minecraft.server.level.ServerLevel level,
                        ResourceManager resources, NaturalStructureLocator.Search search,
                        NaturalStructureLocator.Search.Cursor cursor, AtomicBoolean cancelled) {}

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ntmlocate").requires(source -> source.hasPermission(2))
                .executes(context -> usage(context.getSource()))
                .then(Commands.literal("list").executes(context -> list(context.getSource()))
                        .then(Commands.argument("ignoredArguments", StringArgumentType.greedyString())
                                .executes(context -> list(context.getSource()))))
                .then(Commands.literal("structure").executes(context -> usage(context.getSource()))
                        .then(Commands.argument("name", StringArgumentType.word())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(NaturalStructureLocator.names(), builder))
                                .executes(context -> locate(context.getSource(), StringArgumentType.getString(context, "name")))
                                .then(Commands.argument("ignoredArguments", StringArgumentType.greedyString())
                                        .executes(context -> locate(context.getSource(), StringArgumentType.getString(context, "name")))))));
    }

    private static int usage(CommandSourceStack source) throws CommandSyntaxException {
        source.getPlayerOrException();
        source.sendFailure(Component.translatable("commands.hbm.locate.usage"));
        return 0;
    }

    private static int list(CommandSourceStack source) throws CommandSyntaxException {
        source.getPlayerOrException();
        source.sendSystemMessage(Component.literal(String.join(", ", NaturalStructureLocator.names())));
        return 1;
    }

    private static int locate(CommandSourceStack source, String name) throws CommandSyntaxException {
        var player = source.getPlayerOrException();
        if (!NaturalStructureLocator.names().contains(name)) {
            source.sendSystemMessage(Component.translatable("commands.locate.no_match").withStyle(ChatFormatting.RED));
            return 0;
        }
        var server = source.getServer();
        if (RUNNING.containsKey(server)) {
            source.sendSystemMessage(Component.translatable("commands.hbm.locate.busy").withStyle(ChatFormatting.RED));
            return 0;
        }
        var level = player.serverLevel();
        var captured = NaturalStructureLocator.capture(level);
        if (captured.isEmpty()) {
            source.sendSystemMessage(Component.translatable("commands.locate.none_found").withStyle(ChatFormatting.RED));
            return 0;
        }
        var search = captured.get();
        var resources = server.getResourceManager();
        // Preserve the source's floor(block)/16 origin, including truncation toward zero for negative blocks.
        int centerX = (int) Math.floor(player.getX()) / 16, centerZ = (int) Math.floor(player.getZ()) / 16;
        var work = new Work(source, player, level, resources, search, search.cursor(name, centerX, centerZ), new AtomicBoolean());
        RUNNING.put(server, work);
        return 1;
    }

    @SubscribeEvent
    public static void tick(ServerTickEvent.Post event) {
        var server = event.getServer();
        var work = RUNNING.get(server);
        if (work == null) return;
        // Check lifecycle/input changes before touching any generator, biome or shared template input.
        if (work.cancelled().get() || !work.player().connection.getConnection().isConnected()
                || work.player().serverLevel() != work.level()) {
            RUNNING.remove(server);
            return;
        }
        if (work.resources() != server.getResourceManager() || !work.search().settingsUnchanged()) {
            finish(server, work, new IllegalStateException("Structure generation inputs changed"));
            return;
        }
        try {
            if (work.cursor().advance(VISITS_PER_TICK, System.nanoTime() + SLICE_NANOS, work.cancelled()))
                finish(server, work, null);
        } catch (Exception failure) { finish(server, work, failure); }
    }

    private static void finish(MinecraftServer server, Work work, Exception failure) {
        if (RUNNING.get(server) != work) return;
        RUNNING.remove(server);
        if (work.cancelled().get() || !work.player().connection.getConnection().isConnected()
                || work.player().serverLevel() != work.level()) return;
        var source = work.source();
        if (failure != null || work.resources() != server.getResourceManager() || !work.search().settingsUnchanged()) {
            HbmReloaded.LOGGER.warn("NTM structure locate search failed or its generation inputs changed", failure);
            source.sendSystemMessage(Component.translatable("commands.hbm.locate.failed").withStyle(ChatFormatting.RED));
        } else {
            var position = work.cursor().result();
            if (position == null) source.sendSystemMessage(Component.translatable("commands.locate.none_found").withStyle(ChatFormatting.RED));
            else source.sendSystemMessage(Component.translatable("commands.locate.success.coordinates", work.cursor().name(),
                    position.getMinBlockX(), position.getMinBlockZ()).withStyle(ChatFormatting.GREEN));
        }
    }

    private static void cancel(MinecraftServer server, Work work) {
        work.cancelled().set(true);
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var server = player.getServer();
        var work = RUNNING.get(server);
        if (work != null && work.player() == player) cancel(server, work);
    }

    @SubscribeEvent
    public static void changedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        var work = RUNNING.get(player.getServer());
        if (work != null && work.player() == player) cancel(player.getServer(), work);
    }

    @SubscribeEvent
    public static void unload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof net.minecraft.server.level.ServerLevel level)) return;
        var work = RUNNING.get(level.getServer());
        if (work != null && work.level() == level) cancel(level.getServer(), work);
    }

    @SubscribeEvent
    public static void stopping(ServerStoppingEvent event) {
        var work = RUNNING.get(event.getServer());
        if (work != null) { cancel(event.getServer(), work); RUNNING.remove(event.getServer()); }
    }

    private CommandLocate() {}
}
