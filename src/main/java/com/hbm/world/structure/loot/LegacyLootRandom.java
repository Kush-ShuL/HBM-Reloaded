package com.hbm.world.structure.loot;
import java.util.Random;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
/** Retains the generator's existing java.util.Random stream, including its cached Gaussian. */
public final class LegacyLootRandom implements RandomSource {
    private final Random source;
    public LegacyLootRandom(Random source){this.source=source;}
    @Override public RandomSource fork(){return new LegacyRandomSource(source.nextLong());}
    @Override public PositionalRandomFactory forkPositional(){return new LegacyRandomSource(source.nextLong()).forkPositional();}
    @Override public void setSeed(long seed){source.setSeed(seed);}
    @Override public int nextInt(){return source.nextInt();}
    @Override public int nextInt(int bound){return source.nextInt(bound);}
    @Override public long nextLong(){return source.nextLong();}
    @Override public boolean nextBoolean(){return source.nextBoolean();}
    @Override public float nextFloat(){return source.nextFloat();}
    @Override public double nextDouble(){return source.nextDouble();}
    @Override public double nextGaussian(){return source.nextGaussian();}
}
