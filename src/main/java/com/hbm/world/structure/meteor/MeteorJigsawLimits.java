package com.hbm.world.structure.meteor;

import java.util.List;
import java.util.Map;
import java.util.IdentityHashMap;
import java.util.Collections;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;

/** Scoped source count/range policy; all actual connections stay in vanilla JigsawPlacement. */
public final class MeteorJigsawLimits {
    private record Limits(BlockPos corner,List<PoolElementStructurePiece> pieces,Map<PoolElementStructurePiece,BlockPos> entrances,Set<Object> queues,RandomSource random){}
    private static final ThreadLocal<Limits> CURRENT=new ThreadLocal<>();
    public interface Scope extends AutoCloseable {@Override void close();}
    public static Scope enter(BlockPos corner,List<PoolElementStructurePiece> pieces,RandomSource random){
        var previous=CURRENT.get();CURRENT.set(new Limits(corner,pieces,new IdentityHashMap<>(),Collections.newSetFromMap(new IdentityHashMap<>()),random));
        //Vanilla processes its root directly; the source first draws from its one-entry queue.
        random.nextInt(1);
        return ()->{if(previous==null)CURRENT.remove();else CURRENT.set(previous);MeteorWeightedTemplates.reset();};
    }
    public static boolean active(){return CURRENT.get()!=null;}
    public static void registerQueue(Object queue){var limits=CURRENT.get();if(limits!=null)limits.queues().add(queue);}
    public static RandomSource queueRandom(Object queue){var limits=CURRENT.get();return limits!=null&&limits.queues().contains(queue)?limits.random():null;}
    public static BlockPos entrance(PoolElementStructurePiece piece){var limits=CURRENT.get();return limits==null?null:limits.entrances().get(piece);}
    public static void rememberEntrance(PoolElementStructurePiece piece,BlockPos local){
        var limits=CURRENT.get();if(limits!=null)limits.entrances().put(piece,local.rotate(piece.getRotation()).offset(piece.getPosition()));
    }
    public static boolean mayDrawFallback(PoolElementStructurePiece parent,Vec3i target,boolean forced){
        var limits=CURRENT.get();if(limits==null||forced)return true;
        for(var piece:limits.pieces())if(piece!=parent&&target!=null&&piece.getBoundingBox().isInside(target))return false;
        return true;
    }
    /** Primary pieces ignore their parent; fallback only checks the target voxel outside all other pieces. */
    public static boolean canPlace(PoolElementStructurePiece parent,BoundingBox candidate,boolean fallback,boolean fallbackOnly,Vec3i target){
        var limits=CURRENT.get();if(limits==null)return true;
        if(fallbackOnly)return true;
        if(fallback)return mayDrawFallback(parent,target,false);
        for(var piece:limits.pieces()){
            if(piece==parent)continue;
            if(piece.getBoundingBox().intersects(candidate))return false;
        }
        return true;
    }
    public static boolean fallbackOnly(PoolElementStructurePiece parent){
        var limits=CURRENT.get();if(limits==null)return false;
        var bounds=parent.getBoundingBox();
        //1.7 StructureBoundingBox#getCenterX uses minX+((maxX-minX+1)/2).
        int x=bounds.minX()+bounds.getXSpan()/2,z=bounds.minZ()+bounds.getZSpan()/2;
        int distance=Math.max(Math.abs(x-limits.corner().getX()),Math.abs(z-limits.corner().getZ()));
        return limits.pieces().size()>=128||distance>=128||limits.pieces().size()>1024;
    }
    private MeteorJigsawLimits(){}
}
