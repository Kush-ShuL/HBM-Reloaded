package com.hbm.commands;

import com.hbm.HbmReloaded;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

/** Explicit source diagnostics only: registered on the client and never invoked by startup or tests. */
@EventBusSubscriber(modid = "hbm", value = Dist.CLIENT)
public final class SuicideThreadDump {
    @SubscribeEvent public static void register(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("dumpthreadsandcrashgame")
                .requires(source -> source.getEntity() instanceof LocalPlayer)
                .executes(context -> {
                    context.getSource().sendFailure(Component.translatable("commands.hbm.thread_dump.usage"));
                    return 0;
                })
                .then(Commands.literal("dump").executes(context -> execute(context.getSource(), false)))
                .then(Commands.literal("crash").executes(context -> execute(context.getSource(), true))));
    }

    private static int execute(CommandSourceStack source, boolean crash) {
        for (ThreadInfo info : ManagementFactory.getThreadMXBean().dumpAllThreads(true, true)) {
            HbmReloaded.LOGGER.error("===========================================");
            HbmReloaded.LOGGER.error("Thread: {} PID: {}", info.getThreadName(), info.getThreadId());
            HbmReloaded.LOGGER.error("Suspended: {}", info.isSuspended());
            HbmReloaded.LOGGER.error("Blocked: {}ms, {}x", info.getBlockedTime(), info.getBlockedCount());
            HbmReloaded.LOGGER.error("Runs Native: {}", info.isInNative());
            HbmReloaded.LOGGER.error("State: {}", info.getThreadState().name());
            HbmReloaded.LOGGER.error("-------------------------------------------");
            if (info.getLockedMonitors().length != 0) {
                HbmReloaded.LOGGER.error("Following locks found:");
                for (var monitor : info.getLockedMonitors()) HbmReloaded.LOGGER.error("- {}", monitor.getLockedStackFrame());
                HbmReloaded.LOGGER.error("-------------------------------------------");
            }
            HbmReloaded.LOGGER.error("Stacktrace:");
            for (var line : info.getStackTrace()) HbmReloaded.LOGGER.error("- {}", line);
            HbmReloaded.LOGGER.error("===========================================");
        }
        // The original hardExit=true branch terminates only after an explicit /... crash invocation.
        if (crash) Runtime.getRuntime().halt(0);
        return 1;
    }

    private SuicideThreadDump() {}
}
