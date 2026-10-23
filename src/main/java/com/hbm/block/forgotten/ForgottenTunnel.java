package com.hbm.block.forgotten;

import com.hbm.bomb.nuclear.NuclearChunkAccess;
import com.hbm.registry.HbmSounds;
import com.hbm.world.structure.generated.compat.ForgeDirection;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Only original BlockForgottenLock.generate's375 cells. Native SavedData retains a cold source callback, sole NCA owns readiness. */
public final class ForgottenTunnel extends SavedData {
    private static final Factory<ForgottenTunnel> FACTORY=new Factory<>(ForgottenTunnel::new,ForgottenTunnel::load);
    private static final Map<ServerLevel,ForgottenTunnel> OPEN=new WeakHashMap<>();
    private final List<Work> pending=new ArrayList<>();
    private static final class Work {
        final BlockPos origin;
        final Direction side;
        final double[] sound;
        int cursor;
        boolean executing,tracked;
        NuclearChunkAccess access;
        Work(BlockPos pos,Direction side,double[] sound){origin=pos.immutable();this.side=side;this.sound=sound==null?null:sound.clone();}
        BlockPos cell(int cursor){int width=cursor/75-2,height=cursor/15%5-2,depth=cursor%15;var dir=ForgeDirection.getOrientation(side.get3DDataValue());var rotation=dir.getRotation(ForgeDirection.UP);return origin.offset(-dir.offsetX*depth+rotation.offsetX*width,height,-dir.offsetZ*depth+rotation.offsetZ*width);}
        boolean shell(int cursor){int width=cursor/75-2,height=cursor/15%5-2,depth=cursor%15;return width==-2||width==2||height==-2||height==2||depth==14;}
        void close(){try{if(access!=null)access.close();}finally{access=null;}}
        boolean ready(ServerLevel level){
            var columns=new LinkedHashSet<Long>();for(int i=cursor;i<375;i++){var pos=cell(i);columns.add(ChunkPos.asLong(pos.getX()>>4,pos.getZ()>>4));}
            boolean warm=true;for(long column:columns)if(!ForgottenBricksContent.haloLoaded(level,new BlockPos(ChunkPos.getX(column)<<4,origin.getY(),ChunkPos.getZ(column)<<4)))warm=false;
            if(warm)return true;if(access==null)access=new NuclearChunkAccess();boolean ready=true;for(long column:columns)if(access.getWithNeighbors(level,ChunkPos.getX(column),ChunkPos.getZ(column))==null)ready=false;return ready;
        }
    }
    private static ForgottenTunnel data(ServerLevel level){return OPEN.computeIfAbsent(level,key->key.getDataStorage().computeIfAbsent(FACTORY,"hbm_forgotten_lock_tunnels"));}
    public static boolean pendingAt(ServerLevel level,BlockPos pos){for(var work:data(level).pending)if(work.origin.equals(pos))return true;return false;}
    /** Source generate itself does not need a key or sound; source interaction supplies optional original avatar-position sound tail. */
    public static void generate(ServerLevel level,BlockPos origin,Direction side,double[] sound){
        if(side.getAxis()==Direction.Axis.Y)throw new IllegalArgumentException("Source forgotten lock active producer only accepts horizontal clicked sides");
        var data=data(level);var work=new Work(origin,side,sound);if(data.attempt(level,work))return;work.tracked=true;data.pending.add(work);data.setDirty();
    }
    private boolean attempt(ServerLevel level,Work work){
        if(work.executing)return false;
        if(level.getChunkSource().getChunkNow(work.origin.getX()>>4,work.origin.getZ()>>4)==null){work.close();return false;}
        try{
            if(!work.ready(level))return false;work.executing=true;
            try{
                while(work.cursor<375){level.setBlock(work.cell(work.cursor),work.shell(work.cursor)?ForgottenBricksContent.BRICK.get().defaultBlockState():Blocks.AIR.defaultBlockState(),3);work.cursor++;if(work.tracked)setDirty();}
                if(work.sound!=null)level.playSound(null,work.sound[0],work.sound[1],work.sound[2],HbmSounds.get("block.lockOpen"),SoundSource.PLAYERS,1,1);
                return true;
            }finally{work.executing=false;work.close();}
        }catch(RuntimeException|Error failure){work.close();throw failure;}
    }
    @Override public CompoundTag save(CompoundTag tag,HolderLookup.Provider lookup){var list=new ListTag();for(var work:pending){var row=new CompoundTag();row.putLong("origin",work.origin.asLong());row.putByte("side",(byte)work.side.get3DDataValue());row.putInt("cursor",work.cursor);if(work.sound!=null){row.putDouble("soundX",work.sound[0]);row.putDouble("soundY",work.sound[1]);row.putDouble("soundZ",work.sound[2]);}list.add(row);}tag.put("pending",list);return tag;}
    private static ForgottenTunnel load(CompoundTag tag,HolderLookup.Provider lookup){var data=new ForgottenTunnel();for(var value:tag.getList("pending",Tag.TAG_COMPOUND)){var row=(CompoundTag)value;var work=new Work(BlockPos.of(row.getLong("origin")),Direction.from3DDataValue(row.getByte("side")),row.contains("soundX")?new double[]{row.getDouble("soundX"),row.getDouble("soundY"),row.getDouble("soundZ")}:null);work.cursor=row.getInt("cursor");work.tracked=true;data.pending.add(work);}return data;}
    static void loaded(LevelEvent.Load event){if(event.getLevel()instanceof ServerLevel level)data(level);}
    static void tick(LevelTickEvent.Post event){if(event.getLevel()instanceof ServerLevel level){var data=data(level);for(var work:List.copyOf(data.pending))if(data.pending.contains(work)&&data.attempt(level,work)){data.pending.remove(work);data.setDirty();}}}
    static void unload(LevelEvent.Unload event){if(event.getLevel()instanceof ServerLevel level){var data=OPEN.remove(level);if(data!=null)for(var work:data.pending)work.close();}}
    static void stopped(ServerStoppedEvent event){for(var data:OPEN.values())for(var work:data.pending)work.close();OPEN.clear();}
}
