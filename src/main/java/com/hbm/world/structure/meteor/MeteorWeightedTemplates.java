package com.hbm.world.structure.meteor;

import com.hbm.world.structure.meteor.mixin.MeteorTemplatePoolAccess;
import com.mojang.datafixers.util.Pair;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

/** Keep vanilla placement while retaining source weighted draws at the moment of each attempt. */
public final class MeteorWeightedTemplates {
    private static final ThreadLocal<Boolean> FALLBACK=new ThreadLocal<>();
    private static final ThreadLocal<StructurePoolElement> CANDIDATE=new ThreadLocal<>();

    public static void reset(){FALLBACK.remove();CANDIDATE.remove();}
    public static boolean isFallback(){return Boolean.TRUE.equals(FALLBACK.get());}
    public static StructurePoolElement candidate(){return CANDIDATE.get();}
    public static ArrayList<StructurePoolElement> collectingList(){return new DeferredList();}
    public static List<StructurePoolElement> candidates(StructureTemplatePool pool,RandomSource random,boolean fallback){
        return new Choices(((MeteorTemplatePoolAccess)pool).hbm$rawTemplates(),random,fallback);
    }

    private static final class Choices extends AbstractList<StructurePoolElement> {
        private final ArrayList<Pair<StructurePoolElement,Integer>> remaining;
        private final ArrayList<StructurePoolElement> attempted=new ArrayList<>();
        private final RandomSource random;
        private final boolean fallback;
        private final int count;
        private int totalWeight;
        Choices(List<Pair<StructurePoolElement,Integer>> source,RandomSource random,boolean fallback){
            this.remaining=new ArrayList<>(source);this.random=random;this.fallback=fallback;
            this.count=fallback?Math.min(source.size(),1):source.size();
            for(var row:source)totalWeight+=row.getSecond();
        }
        @Override public int size(){return count;}
        @Override public StructurePoolElement get(int index){
            if(index<0||index>=count)throw new IndexOutOfBoundsException(index);
            while(attempted.size()<=index){
                int draw=random.nextInt(totalWeight);
                for(int i=0;i<remaining.size();i++){
                    var row=remaining.get(i);draw-=row.getSecond();
                    if(draw<0){attempted.add(row.getFirst());remaining.remove(i);totalWeight-=row.getSecond();break;}
                }
            }
            var result=attempted.get(index);FALLBACK.set(fallback);CANDIDATE.set(result);return result;
        }
    }

    /** Vanilla adds both pool lists before its loop; do not eagerly spend their random draws. */
    private static final class DeferredList extends ArrayList<StructurePoolElement> {
        private final List<Collection<? extends StructurePoolElement>> segments=new ArrayList<>();
        @Override public boolean addAll(Collection<? extends StructurePoolElement> values){segments.add(values);return !values.isEmpty();}
        @Override public int size(){int count=0;for(var values:segments)count+=values.size();return count;}
        @Override public boolean isEmpty(){return size()==0;}
        @Override public Iterator<StructurePoolElement> iterator(){
            return new Iterator<>(){
                private int segment;
                private Iterator<? extends StructurePoolElement> current=List.<StructurePoolElement>of().iterator();
                @Override public boolean hasNext(){
                    while(!current.hasNext()&&segment<segments.size())current=segments.get(segment++).iterator();
                    return current.hasNext();
                }
                @Override public StructurePoolElement next(){if(!hasNext())throw new NoSuchElementException();return current.next();}
            };
        }
    }
    private MeteorWeightedTemplates(){}
}
