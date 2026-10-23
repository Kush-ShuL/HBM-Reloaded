package com.hbm.world.structure.generated;
import com.hbm.world.structure.generated.compat.Block;
import com.hbm.world.structure.generated.compat.Blocks;
import com.hbm.world.structure.generated.compat.World;
import java.util.Random;
import java.util.Set;
/** Entire original Geyser.java block algorithm, not a reduced approximate shell. */
public final class SourceChlorineGeyser {

	public static boolean generate(World world, Random rand, int x, int y, int z) {
		int i = rand.nextInt(1);

		if (i == 0) {
			generate_r0(world, rand, x, y, z);
		}

		return true;

	}

	public static boolean generate_r0(World world, Random rand, int x, int y, int z) {

		x -= 2;
		y -= 11;
		z -= 2;
		world.setBlock(x + 1, y + 5, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 5, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 5, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 5, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 5, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 5, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 5, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 4, y + 5, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 5, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 5, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 5, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 5, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 4, y + 5, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 5, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 5, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 5, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 5, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 4, y + 5, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 5, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 5, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 5, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 6, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 6, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 6, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 6, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 6, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 6, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 6, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 4, y + 6, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 6, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 6, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 6, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 6, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 4, y + 6, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 6, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 6, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 6, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 6, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 4, y + 6, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 6, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 6, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 6, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 7, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 7, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 7, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 7, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 7, z + 1, Blocks.water, 0, 3);
		world.setBlock(x + 2, y + 7, z + 1, Block.source("hbm:block_yellowcake"), 0, 3);
		world.setBlock(x + 3, y + 7, z + 1, Blocks.water, 0, 3);
		world.setBlock(x + 4, y + 7, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 7, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 7, z + 2, Block.source("hbm:block_yellowcake"), 0, 3);
		world.setBlock(x + 2, y + 7, z + 2, Blocks.water, 0, 3);
		world.setBlock(x + 3, y + 7, z + 2, Blocks.water, 0, 3);
		world.setBlock(x + 4, y + 7, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 7, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 7, z + 3, Blocks.water, 0, 3);
		world.setBlock(x + 2, y + 7, z + 3, Block.source("hbm:block_yellowcake"), 0, 3);
		world.setBlock(x + 3, y + 7, z + 3, Block.source("hbm:block_yellowcake"), 0, 3);
		world.setBlock(x + 4, y + 7, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 7, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 7, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 7, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 8, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 8, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 8, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 8, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 8, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 8, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 8, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 8, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 8, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 8, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 8, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 8, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 8, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 8, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 8, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 8, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 8, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 8, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 8, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 8, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 8, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 9, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 9, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 9, z + 0, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 9, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 9, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 9, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 4, y + 9, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 9, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 9, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 9, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 9, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 9, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 9, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 9, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 9, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 4, y + 9, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 9, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 9, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 9, z + 4, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 10, z + 0, Blocks.grass, 0, 3);
		world.setBlock(x + 2, y + 10, z + 0, Blocks.grass, 0, 3);
		world.setBlock(x + 3, y + 10, z + 0, Blocks.grass, 0, 3);
		world.setBlock(x + 0, y + 10, z + 1, Blocks.grass, 0, 3);
		world.setBlock(x + 1, y + 10, z + 1, Blocks.gravel, 0, 3);
		world.setBlock(x + 2, y + 10, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 3, y + 10, z + 1, Blocks.grass, 0, 3);
		world.setBlock(x + 4, y + 10, z + 1, Blocks.stone, 0, 3);
		world.setBlock(x + 0, y + 10, z + 2, Blocks.stone, 0, 3);
		world.setBlock(x + 1, y + 10, z + 2, Blocks.grass, 0, 3);
		world.setBlock(x + 2, y + 10, z + 2, Block.source("hbm:geysir_chlorine"), 0, 3);
		world.setBlock(x + 3, y + 10, z + 2, Blocks.grass, 0, 3);
		world.setBlock(x + 4, y + 10, z + 2, Blocks.gravel, 0, 3);
		world.setBlock(x + 0, y + 10, z + 3, Blocks.grass, 0, 3);
		world.setBlock(x + 1, y + 10, z + 3, Blocks.stone, 0, 3);
		world.setBlock(x + 2, y + 10, z + 3, Blocks.grass, 0, 3);
		world.setBlock(x + 3, y + 10, z + 3, Blocks.gravel, 0, 3);
		world.setBlock(x + 4, y + 10, z + 3, Blocks.grass, 0, 3);
		world.setBlock(x + 1, y + 10, z + 4, Blocks.grass, 0, 3);
		world.setBlock(x + 2, y + 10, z + 4, Blocks.grass, 0, 3);
		world.setBlock(x + 3, y + 10, z + 4, Blocks.grass, 0, 3);
		return true;

	}


 public static void natural(World world,Random random,int i,int j,int frequency,Set<String> selected){if(frequency<=0||!world.getBiomeGenForCoords(i,j).biome().equals("minecraft:plains")||random.nextInt(frequency)!=0)return;int x=i+random.nextInt(16),z=j+random.nextInt(16),y=world.getHeightValue(x,z);if(world.sameBlock(x,y-1,z,Blocks.grass)){GeneratedDependencies.forNames(java.util.List.of("old:ChlorineGeyser")).requireRegistryAndHooks();generate(world,random,x,y,z);selected.add("old:ChlorineGeyser");}}
}