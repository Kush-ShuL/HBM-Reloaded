package com.hbm.world.structure.generated.algorithm.legacy;
import com.hbm.world.structure.generated.compat.*;
/** Original Library.rand is independent of both generator rand and world.rand. */
public final class LegacyBuildingLibrary {private static final java.util.Random RANDOM=new java.util.Random();public static synchronized Block getRandomConcrete(){int i=RANDOM.nextInt(20);if(i<=1)return ModBlocks.brick_concrete_broken;if(i<=4)return ModBlocks.brick_concrete_cracked;if(i<=10)return ModBlocks.brick_concrete_mossy;return ModBlocks.brick_concrete;}}
