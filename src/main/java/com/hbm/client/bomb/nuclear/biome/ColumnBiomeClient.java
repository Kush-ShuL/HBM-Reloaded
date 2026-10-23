package com.hbm.client.bomb.nuclear.biome;

import com.hbm.bomb.nuclear.biome.*;
import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Exact biome changes use the native grass/foliage/water resolvers and batched local cache invalidation. */
@EventBusSubscriber(modid="hbm",value=Dist.CLIENT)
public final class ColumnBiomeClient {
    private static ClientLevel currentLevel;
    private static final Set<Long> DIRTY=new HashSet<>();
    public static void receive(ColumnBiomePacket packet){var level=Minecraft.getInstance().level;if(level==null||!level.dimension().equals(packet.dimension()))return;if(currentLevel!=level){currentLevel=level;DIRTY.clear();}ColumnBiomes.receive(level,packet);DIRTY.add(ChunkPos.asLong(packet.chunkX(),packet.chunkZ()));}
    @SubscribeEvent public static void tick(ClientTickEvent.Post event){
        var client=Minecraft.getInstance();var level=client.level;if(currentLevel!=level){currentLevel=level;DIRTY.clear();}if(level==null||DIRTY.isEmpty())return;
        var sections=new HashSet<Long>();for(long value:DIRTY){var pos=new ChunkPos(value);((ColumnBiomeTintAccess)level).hbm$invalidateBiomeTint(pos.x,pos.z);for(int x=-1;x<=1;x++)for(int z=-1;z<=1;z++)sections.add(ChunkPos.asLong(pos.x+x,pos.z+z));}DIRTY.clear();
        for(long value:sections){var pos=new ChunkPos(value);for(int y=level.getMinSection();y<level.getMaxSection();y++)client.levelRenderer.setSectionDirty(pos.x,y,pos.z);}
    }
    private ColumnBiomeClient(){}
}
