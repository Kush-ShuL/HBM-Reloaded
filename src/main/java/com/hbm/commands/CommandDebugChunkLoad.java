package com.hbm.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/** Source diagnostic only: raw current-format NBT, never a chunk load or disk repair. */
@EventBusSubscriber(modid="hbm")
public final class CommandDebugChunkLoad {
    @SubscribeEvent public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("ntmloadchunk").requires(source->source.hasPermission(2))
                .executes(context->0).then(Commands.argument("coordinates",StringArgumentType.greedyString())
                        .executes(context->run(context.getSource(),StringArgumentType.getString(context,"coordinates")))));
    }

    private static int run(CommandSourceStack source,String arguments) throws CommandSyntaxException {
        String[] args=arguments.trim().split("\\s+");
        if(args.length!=2)return 0; // Original arity check precedes integer parsing and performs no work.
        int x=integer(args[0]),z=integer(args[1]);
        var level=source.getLevel();var server=source.getServer();var pos=new ChunkPos(x>>4,z>>4);
        if(level.getChunkSource().getChunkNow(pos.x,pos.z)!=null){failure(source,"loaded");return 0;}
        try {
            level.getChunkSource().chunkMap.read(pos).whenComplete((data,error)->server.execute(()->{
                if(error!=null){readFailure(source,error);return;}
                if(level.getChunkSource().getChunkNow(pos.x,pos.z)!=null){failure(source,"loaded");return;}
                if(data.isEmpty()){failure(source,"no_data",pos.x,pos.z);return;}
                var nbt=data.get();
                if(!nbt.contains("block_entities",Tag.TAG_LIST)){failure(source,"missing_list");return;}
                var entries=nbt.getList("block_entities",Tag.TAG_COMPOUND);
                if(entries.isEmpty()){failure(source,"empty_list");return;}
                for(int i=0;i<entries.size();i++){
                    var entry=entries.getCompound(i);
                    int localX=entry.getInt("x")-pos.x*16,y=entry.getInt("y"),localZ=entry.getInt("z")-pos.z*16;
                    boolean invalid=localX<0||localX>15||y<level.getMinBuildHeight()||y>=level.getMaxBuildHeight()||localZ<0||localZ>15;
                    source.sendSystemMessage(Component.literal(entry.getString("id")+" "+localX+" "+y+" "+localZ)
                            .withStyle(invalid?ChatFormatting.RED:ChatFormatting.GREEN));
                }
            }));
            return 1; // Read scheduled; completion feedback comes back on the server executor.
        }catch(RuntimeException error){readFailure(source,error);return 0;}
    }

    private static int integer(String value) throws CommandSyntaxException {
        try{return Integer.parseInt(value);}catch(NumberFormatException error){throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt().create(value);}
    }
    private static void failure(CommandSourceStack source,String key,Object... args){source.sendFailure(Component.translatable("commands.hbm.chunk_diagnostic."+key,args));}
    private static void readFailure(CommandSourceStack source,Throwable error){
        var cause=error.getCause()==null?error:error.getCause();var message=cause.getLocalizedMessage();
        failure(source,"read_failed",message==null?cause.getClass().getSimpleName():message);
    }
    private CommandDebugChunkLoad(){}
}
