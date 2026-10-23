package com.hbm.world.structure.tandem;

import net.minecraft.util.RandomSource;

/** Only an actual source structure build supplies its condition. Ordinary authoring LOAD stays null. */
public final class TandemSourceScope {
    private record Context(String condition,RandomSource random){}
    private static final ThreadLocal<Context> CURRENT=new ThreadLocal<>();
    public interface Scope extends AutoCloseable {@Override void close();}
    public static boolean active(){return CURRENT.get()!=null;}
    public static String condition(){var context=CURRENT.get();return context==null?null:context.condition;}
    public static RandomSource sourceRandom(){var context=CURRENT.get();return context==null?null:context.random;}
    public static Scope using(String condition,RandomSource random){
        var previous=CURRENT.get();CURRENT.set(new Context(condition,java.util.Objects.requireNonNull(random)));
        return ()->{if(previous==null)CURRENT.remove();else CURRENT.set(previous);};
    }
    private TandemSourceScope(){}
}
