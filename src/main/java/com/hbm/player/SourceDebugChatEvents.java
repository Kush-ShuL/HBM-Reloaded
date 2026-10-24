package com.hbm.player;

import com.hbm.config.GeneralModes;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;

/** Original opt-in author-only chat utility, using native item identities and components. */
@EventBusSubscriber(modid = "hbm")
public final class SourceDebugChatEvents {
    private static final UUID SOURCE_AUTHOR = UUID.fromString("192af5d7-ed0f-48d8-bd89-9d41af8524f8");

    @SubscribeEvent
    public static void chat(ServerChatEvent event) {
        var player = event.getPlayer();
        String message = event.getRawText();
        if (!GeneralModes.localFlag("debugModeEnabled") || !SOURCE_AUTHOR.equals(player.getUUID()) || !message.startsWith("!")) return;

        try {
            var reader = new StringReader(message);
            reader.skip();
            String command = reader.readUnquotedString().toLowerCase(Locale.US);
            if (command.equals("gv")) give(player, reader);
        } catch (Exception ignored) {
            // Invalid author input remains silent, with the same outer inventory/chat actions.
        } finally {
            player.inventoryMenu.broadcastChanges();
            event.setCanceled(true);
        }
    }

    private static void give(ServerPlayer player, StringReader reader) throws CommandSyntaxException {
        reader.skipWhitespace();
        var parsed = new ItemParser(player.registryAccess()).parse(reader);
        if (reader.canRead() && !Character.isWhitespace(reader.peek())) return;
        reader.skipWhitespace();
        int count = reader.canRead() ? reader.readInt() : 1;
        reader.skipWhitespace();
        if (count <= 0 || reader.canRead()) return;

        var input = new ItemInput(parsed.item(), parsed.components());
        var stack = input.createItemStack(count, false);
        // Source uses Inventory.add only: any unaccepted remainder is not dropped or granted elsewhere.
        player.getInventory().add(stack);
    }

    private SourceDebugChatEvents() {}
}
