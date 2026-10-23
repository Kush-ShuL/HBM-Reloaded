package com.hbm.world.structure.generated;
import java.util.Random;
import net.minecraft.util.RandomSource;
/** One source state transition per next(bits), without a second seed or discarded random stream. */
public final class SourceRandom extends Random {
    private final RandomSource source;
    public SourceRandom(RandomSource source){super(0);this.source=source;}
    @Override protected int next(int bits){return source.nextInt()>>>(32-bits);}
}
