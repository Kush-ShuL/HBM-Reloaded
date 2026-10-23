package com.hbm.world.structure.generated.algorithm.legacy;
import java.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.hbm.world.structure.generated.*;
import com.hbm.world.structure.generated.compat.*;
import com.hbm.world.structure.generated.algorithm.Component.*;
import com.hbm.world.structure.generated.algorithm.ProceduralStructureStart.ProceduralComponent;
import com.hbm.world.structure.generated.algorithm.legacy.TimedGenerator.ITimedJob;
/** Supplied X5846 algorithm; only native-version API boundary adapted. Source: legacy/src/main/java/com/hbm/world/feature/Dud.java */
public class Dud extends WorldGenerator {

	protected Block[] GetValidSpawnBlocks() {
		return new Block[] {
			Blocks.grass,
			Blocks.dirt,
			Blocks.stone,
			Blocks.sand,
			Blocks.sandstone,
		};
	}

	public boolean LocationIsValidSpawn(World world, int x, int y, int z) {

		Block checkBlock = world.getBlock(x, y - 1, z);
		Block blockAbove = world.getBlock(x, y, z);
		Block blockBelow = world.getBlock(x, y - 2, z);

		for(Block i : GetValidSpawnBlocks()) {
			if(!blockAbove.sameAs(Blocks.air)) {
				return false;
			}
			if(checkBlock.sameAs(i)) {
				return true;
			} else if(checkBlock.sameAs(Blocks.snow_layer) && blockBelow.sameAs(i)) {
				return true;
			} else if(checkBlock.getMaterial() == Material.plants && blockBelow.sameAs(i)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int i = rand.nextInt(1);

		if(i == 0) {
			generate_r0(world, rand, x, y, z);
		}

		return true;

	}

	public boolean generate_r0(World world, Random rand, int x, int y, int z) {
		if(!LocationIsValidSpawn(world, x, y, z))
			return false;

		world.setBlock(x, y, z, ModBlocks.crashed_balefire, rand.nextInt(4), 3);

		return true;

	}
}
