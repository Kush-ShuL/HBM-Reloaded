package com.hbm.commands;

import com.hbm.damage.DamageResistanceHandler;
import com.hbm.fluid.FluidContainerRegistry;
import com.hbm.fluid.sync.FluidDefinitionSync;
import com.hbm.recipe.SourceRecipeReload;
import com.hbm.world.structure.logic.LogicProgram;
import com.hbm.world.structure.loot.LegacyStructureLoot;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateRecipesPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** Original permission-two server reload command; extra arguments are ignored. */
@EventBusSubscriber(modid="hbm")
public final class CommandReloadRecipes {
    private static final java.util.Set<net.minecraft.server.MinecraftServer> RELOADING=new java.util.HashSet<>();
    @SubscribeEvent public static void stopped(net.neoforged.neoforge.event.server.ServerStoppedEvent event){RELOADING.remove(event.getServer());}
    @SubscribeEvent public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ntmreload")
            .requires(source -> source.hasPermission(2))
            .executes(context -> run(context.getSource()))
            .then(Commands.argument("ignoredArguments", StringArgumentType.greedyString())
                .executes(context -> run(context.getSource()))));
    }

    private static int run(CommandSourceStack source) {
        var server = source.getServer();
        if(!RELOADING.add(server)){source.sendSystemMessage(Component.translatable("commands.hbm.reload.busy").withStyle(ChatFormatting.RED));return 0;}
        try {
            // clear -> reload fluids/listeners -> register defaults, once.
            FluidContainerRegistry.reload();
            SourceRecipeReload.reload(server);
            LegacyStructureLoot.reload(server);
            DamageResistanceHandler.load();
            LogicProgram.initialize();

            // Each stage completes only after client MAIN application/ACK.
            // Never block the server thread waiting for a network response.
            FluidDefinitionSync.sendToAll(server).whenComplete((ignored,fluidFailure)->server.execute(()->{
                if(fluidFailure!=null){finish(source,fluidFailure);return;}
                try{com.hbm.machine.annihilator.AnnihilatorRecipeSync.sendToAll(server)
                    .whenComplete((applied,milestoneFailure)->server.execute(()->finish(source,milestoneFailure)));
                }catch(Exception error){finish(source,error);}
            }));
            return 1;
        } catch (Exception error) {
            RELOADING.remove(server);
            reportFailure(source,error);
            if (error instanceof RuntimeException runtime) throw runtime;
            throw new IllegalStateException(error);
        }
    }

    private static void finish(CommandSourceStack source,Throwable failure){
        var server=source.getServer();
        try{
            if(failure!=null){reportAsyncFailure(source,failure);return;}
            var recipes=new ClientboundUpdateRecipesPacket(server.getRecipeManager().getOrderedRecipes());
            for(var player:server.getPlayerList().getPlayers()){
                com.hbm.machine.custommachine.CustomMachineNetwork.send(player);
                player.connection.send(recipes);
                player.getRecipeBook().sendInitialRecipeBook(player);
            }
            source.sendSystemMessage(Component.translatable("commands.hbm.reload.complete").withStyle(ChatFormatting.YELLOW));
        }catch(Exception error){reportAsyncFailure(source,error);}
        finally{RELOADING.remove(server);}
    }
    private static void reportAsyncFailure(CommandSourceStack source,Throwable failure){
        while(failure instanceof java.util.concurrent.CompletionException&&failure.getCause()!=null)failure=failure.getCause();
        reportFailure(source,failure);
        com.hbm.HbmReloaded.LOGGER.error("Original ntmreload client synchronization failed",failure);
    }
    private static void reportFailure(CommandSourceStack source,Throwable error){
        source.sendSystemMessage(Component.literal("----------------------------------").withStyle(ChatFormatting.GRAY));
        source.sendSystemMessage(Component.translatable("commands.hbm.reload.failed").withStyle(ChatFormatting.RED));
        source.sendSystemMessage(Component.literal(String.valueOf(error.getLocalizedMessage())).withStyle(ChatFormatting.RED));
        source.sendSystemMessage(Component.literal(error.getStackTrace()[0].toString()).withStyle(ChatFormatting.RED));
        source.sendSystemMessage(Component.literal("----------------------------------").withStyle(ChatFormatting.GRAY));
    }

    private CommandReloadRecipes() {}
}
