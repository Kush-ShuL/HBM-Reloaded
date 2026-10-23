package com.hbm.block.phosphorvine;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.*;

/** Single real source plant. Native scheduled block ticks persist cold support checks, not legacy-save import data. */
public final class PhosphorVinesContent {
    private static final DeferredRegister.Blocks BLOCKS=DeferredRegister.createBlocks("hbm");
    private static final DeferredRegister.Items ITEMS=DeferredRegister.createItems("hbm");
    public static final DeferredBlock<PhosphorVineBlock> VINE=BLOCKS.register("vine_phosphor",PhosphorVineBlock::new);
    public static final DeferredItem<BlockItem> ITEM=ITEMS.register("vine_phosphor",()->new BlockItem(VINE.get(),new Item.Properties()));
    private static final Map<ServerLevel,Map<BlockPos,Pending>> PENDING=new WeakHashMap<>();
    private static final class Pending {final NuclearChunkAccess access=new NuclearChunkAccess();boolean executing;void close(){access.close();}}
    public static void register(IEventBus bus){BLOCKS.register(bus);ITEMS.register(bus);NeoForge.EVENT_BUS.addListener(PhosphorVinesContent::tick);NeoForge.EVENT_BUS.addListener(PhosphorVinesContent::unload);NeoForge.EVENT_BUS.addListener(PhosphorVinesContent::stop);}
    public static List<ItemStack> creativeStacks(String tab){return tab.equals("block")?List.of(new ItemStack(ITEM.get())):List.of();}
    private static boolean warm(ServerLevel level,BlockPos pos){for(int x=(pos.getX()>>4)-1;x<=(pos.getX()>>4)+1;x++)for(int z=(pos.getZ()>>4)-1;z<=(pos.getZ()>>4)+1;z++)if(level.getChunkSource().getChunkNow(x,z)==null)return false;return true;}
    static void checkSupport(ServerLevel level,BlockPos pos){
        try{check(level,pos);}catch(RuntimeException|Error failure){removed(level,pos);throw failure;}
    }
    private static void check(ServerLevel level,BlockPos pos){
        var state=level.getBlockState(pos);if(!state.is(VINE.get())||level.getBlockState(pos.above()).is(VINE.get())){removed(level,pos);return;}
        var map=PENDING.computeIfAbsent(level,unused->new LinkedHashMap<>());var pending=map.get(pos);
        if(pending!=null&&pending.executing)return;
        if(!warm(level,pos)){
            if(pending==null){pending=new Pending();map.put(pos.immutable(),pending);}
            // Actual native tick is saved with the chunk if the world is stopped while this callback waits.
            level.scheduleTick(pos,VINE.get(),1);
            if(pending.access.getWithNeighbors(level,pos.getX()>>4,pos.getZ()>>4)==null)return;
        }
        // Some modded sturdy-face queries inspect neighbours; do not call them before the same FULL boundary.
        if(state.canSurvive(level,pos)){removed(level,pos);return;}
        if(pending!=null)pending.executing=true;
        try{level.setBlock(pos,Blocks.AIR.defaultBlockState(),3);}finally{if(pending!=null){pending.executing=false;pending.close();map.remove(pos);}if(map.isEmpty())PENDING.remove(level);}
    }
    static void removed(ServerLevel level,BlockPos pos){var map=PENDING.get(level);if(map==null)return;var pending=map.get(pos);if(pending!=null&&!pending.executing){pending.close();map.remove(pos);}if(map.isEmpty())PENDING.remove(level);}
    private static void tick(LevelTickEvent.Post event){if(event.getLevel()instanceof ServerLevel level){var map=PENDING.get(level);if(map!=null)for(var pos:List.copyOf(map.keySet())){if(level.getChunkSource().getChunkNow(pos.getX()>>4,pos.getZ()>>4)==null)removed(level,pos);else checkSupport(level,pos);}}}
    private static void unload(LevelEvent.Unload event){if(event.getLevel()instanceof ServerLevel level){var map=PENDING.remove(level);if(map!=null)for(var pending:map.values())pending.close();}}
    private static void stop(ServerStoppedEvent event){for(var map:PENDING.values())for(var pending:map.values())pending.close();PENDING.clear();}
    private PhosphorVinesContent(){}
}
