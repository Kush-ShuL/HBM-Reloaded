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
/** Supplied X5846 algorithm; only native-version API boundary adapted. Source: legacy/src/main/java/com/hbm/world/dungeon/LibraryDungeon.java */
public class LibraryDungeon extends WorldGenerator
{

	public boolean LocationIsValidSpawn(World world, int x, int y, int z)
 {

		Block blockAbove = world.getBlock(x, y  + 8, z);
		Block blockBelow = world.getBlock(x, y - 1, z);

		if(blockAbove.getMaterial().isSolid() && blockBelow.getMaterial().isSolid() && y - 1 > 4)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z)
	{
		int i = rand.nextInt(1);

		if(i == 0)
		{
		    generate_r0(world, rand, x, y, z);
		}

       return true;

	}

	public boolean generate_r0(World world, Random rand, int x, int y, int z)
	{
		if(!LocationIsValidSpawn(world, x, y, z) || !LocationIsValidSpawn(world, x + 8, y, z) || !LocationIsValidSpawn(world, x + 8, y, z + 10) || !LocationIsValidSpawn(world, x, y, z + 10))
		{
			return false;
		}

		world.setBlock(x + 0, y + 0, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 0, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 0, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 0, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 0, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 0, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 0, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 0, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 0, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 0, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 0, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 0, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 0, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 0, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 0, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 0, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 0, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 0, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 0, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 0, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 0, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 0, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 0, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 0, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 0, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 0, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 0, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 0, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 0, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 0, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 0, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 0, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 0, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 0, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 0, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 0, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 0, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 0, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 0, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 0, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 0, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 0, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 0, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 0, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 0, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 0, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 0, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 0, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 0, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 0, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 0, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 0, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 0, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 0, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 0, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 0, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 0, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 0, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 0, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 0, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 0, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 0, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 0, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 0, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 0, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 0, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 0, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 0, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 0, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 0, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 0, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 0, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 0, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 0, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 0, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 0, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 0, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 0, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 0, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 0, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 0, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 0, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 0, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 0, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 0, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 0, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 0, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 0, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 0, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 0, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 0, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 0, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 0, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 0, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 0, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 0, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 0, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 0, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 0, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 1, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 1, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 1, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 1, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 1, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 1, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 1, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 1, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 1, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 1, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 1, z + 1, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 1, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 1, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 1, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 1, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 1, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 1, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 1, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 1, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 1, z + 2, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 1, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 1, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 1, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 1, z + 2, Blocks.air, 0, 3);

		world.setBlock(x + 6, y + 1, z + 2, Blocks.mob_spawner, 0, 2);
        LegacyVanillaDungeon.Spawner tileentitymobspawner = LegacyVanillaDungeon.spawner(world,x + 6, y + 1, z + 2);

        if (tileentitymobspawner != null)
        {
            tileentitymobspawner.func_145881_a().setEntityName(this.pickMobSpawner(rand));
        }
        else
        {
            System.err.println("Failed to fetch mob spawner entity at (" + (x + 6) + ", " + (y + 1) + ", " + (z + 2) + ")");
        }
		world.setBlock(x + 7, y + 1, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 1, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 1, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 1, z + 3, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 1, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 1, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 1, z + 3, Blocks.fence, 0, 3);
		world.setBlock(x + 5, y + 1, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 1, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 1, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 1, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 1, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 1, z + 4, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 1, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 1, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 1, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 1, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 1, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 1, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 1, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 1, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 1, z + 5, Blocks.chest, 5, 3);
        Container tileentitychest = (Container)world.getTileEntity(x + 1, y + 1, z + 5);

        if (tileentitychest != null)
        {
            GeneratedLoot.fill(world,rand, GeneratedLoot.pool("DUNGEON_CHEST"), tileentitychest, LegacyVanillaDungeon.count(rand));
        }
		world.setBlock(x + 2, y + 1, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 1, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 1, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 1, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 1, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 1, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 1, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 1, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 1, z + 6, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 1, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 1, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 1, z + 6, Blocks.fence, 0, 3);
		world.setBlock(x + 5, y + 1, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 1, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 1, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 1, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 1, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 1, z + 7, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 1, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 1, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 1, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 1, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 1, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 1, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 1, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 1, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 1, z + 8, Blocks.chest, 5, 3);
        Container tileentitychest1 = (Container)world.getTileEntity(x + 1, y + 1, z + 8);

        if (tileentitychest1 != null)
        {
            GeneratedLoot.fill(world,rand, GeneratedLoot.pool("DUNGEON_CHEST"), tileentitychest1, LegacyVanillaDungeon.count(rand));
        }
		world.setBlock(x + 2, y + 1, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 1, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 1, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 1, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 1, z + 8, Blocks.bedrock, 0, 3);

		world.setBlock(x + 6, y + 1, z + 8, Blocks.mob_spawner, 0, 2);
        LegacyVanillaDungeon.Spawner tileentitymobspawner1 = LegacyVanillaDungeon.spawner(world,x + 6, y + 1, z + 8);

        if (tileentitymobspawner1 != null)
        {
            tileentitymobspawner1.func_145881_a().setEntityName(this.pickMobSpawner(rand));
        }
        else
        {
            System.err.println("Failed to fetch mob spawner entity at (" + (x + 6) + ", " + (y + 1) + ", " + (z + 8) + ")");
        }
		world.setBlock(x + 7, y + 1, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 1, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 1, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 1, z + 9, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 1, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 1, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 1, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 1, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 1, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 1, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 1, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 1, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 1, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 1, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 1, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 1, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 1, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 1, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 1, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 1, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 2, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 2, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 2, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 2, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 2, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 2, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 2, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 2, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 2, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 2, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 2, z + 1, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 2, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 2, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 2, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 2, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 2, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 2, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 2, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 2, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 2, z + 2, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 2, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 2, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 2, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 2, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 2, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 2, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 2, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 2, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 2, z + 3, Blocks.chest, 5, 3);
        Container tileentitychest2 = (Container)world.getTileEntity(x + 1, y + 2, z + 3);

        if (tileentitychest2 != null)
        {
            GeneratedLoot.fill(world,rand, GeneratedLoot.pool("DUNGEON_CHEST"), tileentitychest2, LegacyVanillaDungeon.count(rand));
        }
		world.setBlock(x + 2, y + 2, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 2, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 2, z + 3, Blocks.fence, 0, 3);
		world.setBlock(x + 5, y + 2, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 2, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 2, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 2, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 2, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 2, z + 4, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 2, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 2, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 2, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 2, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 2, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 2, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 2, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 2, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 2, z + 5, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 2, y + 2, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 2, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 2, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 2, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 2, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 2, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 2, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 2, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 2, z + 6, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 2, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 2, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 2, z + 6, Blocks.fence, 0, 3);
		world.setBlock(x + 5, y + 2, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 2, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 2, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 2, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 2, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 2, z + 7, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 2, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 2, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 2, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 2, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 2, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 2, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 2, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 2, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 2, z + 8, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 2, y + 2, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 2, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 2, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 2, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 2, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 2, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 2, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 2, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 2, z + 9, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 2, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 2, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 2, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 2, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 2, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 2, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 2, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 2, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 2, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 2, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 2, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 2, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 2, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 2, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 2, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 2, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 3, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 3, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 3, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 3, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 3, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 3, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 3, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 3, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 3, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 3, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 3, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 3, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 3, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 3, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 3, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 3, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 3, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 3, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 3, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 3, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 3, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 3, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 3, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 3, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 3, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 3, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 3, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 3, z + 3, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 2, y + 3, z + 3, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 3, y + 3, z + 3, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 4, y + 3, z + 3, Blocks.double_wooden_slab, 0, 3);
		world.setBlock(x + 6, y + 3, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 3, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 3, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 3, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 3, z + 4, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 2, y + 3, z + 4, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 3, y + 3, z + 4, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 4, y + 3, z + 4, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 5, y + 3, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 3, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 3, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 3, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 3, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 3, z + 5, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 2, y + 3, z + 5, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 3, y + 3, z + 5, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 4, y + 3, z + 5, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 5, y + 3, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 3, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 3, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 3, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 3, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 3, z + 6, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 2, y + 3, z + 6, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 3, y + 3, z + 6, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 4, y + 3, z + 6, Blocks.double_wooden_slab, 0, 3);
		world.setBlock(x + 6, y + 3, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 3, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 3, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 3, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 3, z + 7, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 2, y + 3, z + 7, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 3, y + 3, z + 7, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 4, y + 3, z + 7, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 5, y + 3, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 3, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 3, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 3, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 3, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 3, z + 8, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 2, y + 3, z + 8, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 3, y + 3, z + 8, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 4, y + 3, z + 8, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 5, y + 3, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 3, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 3, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 3, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 3, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 3, z + 9, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 2, y + 3, z + 9, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 3, y + 3, z + 9, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 4, y + 3, z + 9, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 5, y + 3, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 3, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 3, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 3, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 3, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 3, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 3, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 3, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 3, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 3, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 3, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 3, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 3, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 4, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 4, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 4, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 4, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 4, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 4, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 4, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 4, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 4, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 4, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 4, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 4, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 4, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 4, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 4, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 4, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 4, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 4, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 4, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 4, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 4, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 4, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 4, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 4, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 4, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 4, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 4, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 4, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 4, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 4, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 4, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 4, z + 3, Blocks.fence, 0, 3);
		world.setBlock(x + 5, y + 4, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 4, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 4, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 4, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 4, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 4, z + 4, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 4, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 4, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 4, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 4, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 4, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 4, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 4, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 4, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 4, z + 5, Blocks.chest, 5, 3);
        Container tileentitychest3 = (Container)world.getTileEntity(x + 1, y + 4, z + 5);

        if (tileentitychest3 != null)
        {
            GeneratedLoot.fill(world,rand, GeneratedLoot.pool("DUNGEON_CHEST"), tileentitychest3, LegacyVanillaDungeon.count(rand));
        }
		world.setBlock(x + 2, y + 4, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 4, z + 5, Blocks.bedrock, 0, 3);

		world.setBlock(x + 3, y + 4, z + 5, Blocks.mob_spawner, 0, 2);
        LegacyVanillaDungeon.Spawner tileentitymobspawner2 = LegacyVanillaDungeon.spawner(world,x + 3, y + 4, z + 5);

        if (tileentitymobspawner2 != null)
        {
            tileentitymobspawner2.func_145881_a().setEntityName(this.pickMobSpawner(rand));
        }
        else
        {
            System.err.println("Failed to fetch mob spawner entity at (" + (x + 3) + ", " + (y + 4) + ", " + (z + 5) + ")");
        }
		world.setBlock(x + 4, y + 4, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 4, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 4, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 4, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 4, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 4, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 4, z + 6, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 4, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 4, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 4, z + 6, Blocks.fence, 0, 3);
		world.setBlock(x + 5, y + 4, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 4, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 4, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 4, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 4, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 4, z + 7, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 4, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 4, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 4, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 4, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 4, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 4, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 4, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 4, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 4, z + 8, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 4, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 4, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 4, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 4, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 4, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 4, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 4, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 4, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 4, z + 9, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 4, z + 9, Blocks.chest, 2, 3);
        Container tileentitychest4 = (Container)world.getTileEntity(x + 2, y + 4, z + 9);

        if (tileentitychest4 != null)
        {
            GeneratedLoot.fill(world,rand, GeneratedLoot.pool("DUNGEON_CHEST"), tileentitychest4, LegacyVanillaDungeon.count(rand));
        }
		world.setBlock(x + 3, y + 4, z + 9, getShelf(rand), 0, 3);
		world.setBlock(x + 4, y + 4, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 4, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 4, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 4, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 4, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 4, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 4, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 4, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 4, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 4, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 4, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 4, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 4, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 4, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 5, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 5, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 5, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 5, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 5, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 5, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 5, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 5, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 5, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 5, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 5, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 5, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 5, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 5, z + 1, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 5, y + 5, z + 1, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 6, y + 5, z + 1, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 7, y + 5, z + 1, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 8, y + 5, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 5, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 5, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 5, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 5, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 5, z + 2, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 5, y + 5, z + 2, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 6, y + 5, z + 2, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 7, y + 5, z + 2, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 8, y + 5, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 5, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 5, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 5, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 5, z + 3, Blocks.double_wooden_slab, 0, 3);
		world.setBlock(x + 5, y + 5, z + 3, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 6, y + 5, z + 3, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 7, y + 5, z + 3, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 8, y + 5, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 5, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 5, z + 4, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 5, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 5, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 5, z + 4, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 5, y + 5, z + 4, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 6, y + 5, z + 4, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 7, y + 5, z + 4, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 8, y + 5, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 5, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 5, z + 5, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 2, y + 5, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 5, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 5, z + 5, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 5, y + 5, z + 5, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 6, y + 5, z + 5, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 7, y + 5, z + 5, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 8, y + 5, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 5, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 5, z + 6, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 5, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 5, z + 6, Blocks.double_wooden_slab, 0, 3);
		world.setBlock(x + 5, y + 5, z + 6, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 6, y + 5, z + 6, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 7, y + 5, z + 6, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 8, y + 5, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 5, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 5, z + 7, Blocks.chest, 5, 3);
        Container tileentitychest5 = (Container)world.getTileEntity(x + 1, y + 5, z + 7);

        if (tileentitychest5 != null)
        {
            GeneratedLoot.fill(world,rand, GeneratedLoot.pool("DUNGEON_CHEST"), tileentitychest5, LegacyVanillaDungeon.count(rand));
        }
		world.setBlock(x + 2, y + 5, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 5, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 5, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 5, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 5, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 5, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 5, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 5, z + 8, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 5, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 5, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 5, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 5, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 5, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 5, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 5, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 5, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 5, z + 9, getShelf(rand), 0, 3);
		world.setBlock(x + 2, y + 5, z + 9, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 3, y + 5, z + 9, getShelf(rand), 0, 3);
		world.setBlock(x + 4, y + 5, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 5, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 5, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 5, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 5, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 5, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 5, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 5, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 5, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 5, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 5, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 5, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 5, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 5, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 6, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 6, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 6, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 6, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 6, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 6, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 6, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 6, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 6, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 6, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 6, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 6, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 6, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 6, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 6, z + 1, getShelf(rand), 0, 3);
		world.setBlock(x + 6, y + 6, z + 1, Blocks.chest, 3, 3);
        Container tileentitychest6 = (Container)world.getTileEntity(x + 6, y + 6, z + 1);

        if (tileentitychest6 != null)
        {
            GeneratedLoot.fill(world,rand, GeneratedLoot.pool("DUNGEON_CHEST"), tileentitychest6, LegacyVanillaDungeon.count(rand));
        }
		world.setBlock(x + 7, y + 6, z + 1, getShelf(rand), 0, 3);
		world.setBlock(x + 8, y + 6, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 6, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 6, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 6, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 6, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 6, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 6, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 6, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 6, z + 2, getShelf(rand), 0, 3);
		world.setBlock(x + 8, y + 6, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 6, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 6, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 6, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 6, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 6, z + 3, Blocks.fence, 0, 3);
		world.setBlock(x + 5, y + 6, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 6, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 6, z + 3, getShelf(rand), 0, 3);
		world.setBlock(x + 8, y + 6, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 6, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 6, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 6, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 6, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 6, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 6, z + 4, Blocks.bedrock, 0, 3);

		world.setBlock(x + 5, y + 6, z + 4, Blocks.mob_spawner, 0, 2);
        LegacyVanillaDungeon.Spawner tileentitymobspawner3 = LegacyVanillaDungeon.spawner(world,x + 5, y + 6, z + 4);

        if (tileentitymobspawner3 != null)
        {
            tileentitymobspawner3.func_145881_a().setEntityName(this.pickMobSpawner(rand));
        }
        else
        {
            System.err.println("Failed to fetch mob spawner entity at (" + (x + 5) + ", " + (y + 6) + ", " + (z + 4) + ")");
        }
		world.setBlock(x + 6, y + 6, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 6, z + 4, Blocks.chest, 4, 3);
        Container tileentitychest7 = (Container)world.getTileEntity(x + 7, y + 6, z + 4);

        if (tileentitychest7 != null)
        {
            GeneratedLoot.fill(world,rand, GeneratedLoot.pool("DUNGEON_CHEST"), tileentitychest7, LegacyVanillaDungeon.count(rand));
        }
		world.setBlock(x + 8, y + 6, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 6, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 6, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 6, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 6, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 6, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 6, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 6, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 6, z + 5, getShelf(rand), 0, 3);
		world.setBlock(x + 8, y + 6, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 6, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 6, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 6, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 6, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 6, z + 6, Blocks.fence, 0, 3);
		world.setBlock(x + 5, y + 6, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 6, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 6, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 6, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 6, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 6, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 6, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 6, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 6, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 6, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 6, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 6, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 6, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 6, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 6, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 6, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 6, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 6, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 6, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 6, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 6, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 6, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 6, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 6, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 6, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 6, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 6, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 6, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 6, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 6, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 6, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 6, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 6, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 6, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 6, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 6, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 6, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 6, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 6, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 6, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 7, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 7, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 7, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 7, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 7, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 7, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 7, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 7, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 7, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 7, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 7, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 7, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 7, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 7, z + 1, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 7, z + 1, getShelf(rand), 0, 3);
		world.setBlock(x + 6, y + 7, z + 1, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 7, y + 7, z + 1, getShelf(rand), 0, 3);
		world.setBlock(x + 8, y + 7, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 7, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 7, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 7, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 7, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 7, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 7, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 7, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 7, z + 2, getShelf(rand), 0, 3);
		world.setBlock(x + 8, y + 7, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 7, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 7, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 7, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 7, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 7, z + 3, Blocks.fence, 0, 3);
		world.setBlock(x + 5, y + 7, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 7, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 7, z + 3, getShelf(rand), 0, 3);
		world.setBlock(x + 8, y + 7, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 7, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 7, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 7, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 7, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 7, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 7, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 7, z + 4, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 7, z + 4, Blocks.wooden_slab, 8, 3);
		world.setBlock(x + 8, y + 7, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 7, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 7, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 7, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 7, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 7, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 7, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 7, z + 5, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 7, z + 5, getShelf(rand), 0, 3);
		world.setBlock(x + 8, y + 7, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 7, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 7, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 7, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 7, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 7, z + 6, Blocks.fence, 0, 3);
		world.setBlock(x + 5, y + 7, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 7, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 7, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 7, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 7, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 7, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 7, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 7, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 7, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 7, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 7, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 7, z + 7, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 7, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 7, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 7, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 7, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 7, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 7, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 7, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 7, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 7, z + 8, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 7, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 7, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 7, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 2, y + 7, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 7, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 7, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 7, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 6, y + 7, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 7, y + 7, z + 9, Blocks.air, 0, 3);
		world.setBlock(x + 8, y + 7, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 7, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 7, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 7, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 7, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 7, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 7, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 7, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 7, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 7, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 8, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 8, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 8, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 8, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 8, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 8, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 8, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 8, z + 0, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 8, z + 0, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 0, y + 8, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 8, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 8, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 8, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 8, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 8, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 8, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 8, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 8, z + 1, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 8, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 8, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 8, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 8, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 8, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 8, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 8, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 8, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 8, z + 2, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 8, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 8, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 8, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 8, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 8, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 8, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 8, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 8, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 8, z + 3, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 8, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 8, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 8, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 8, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 8, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 8, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 8, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 8, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 8, z + 4, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 8, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 8, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 8, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 8, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 8, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 8, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 8, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 8, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 8, z + 5, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 8, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 8, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 8, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 8, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 8, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 8, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 8, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 8, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 8, z + 6, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 8, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 8, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 8, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 8, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 8, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 8, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 8, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 8, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 8, z + 7, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 8, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 8, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 8, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 8, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 8, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 8, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 8, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 8, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 8, z + 8, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 8, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 1, y + 8, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 8, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 8, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 8, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 8, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 8, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 8, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 8, z + 9, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 0, y + 8, z + 10, Blocks.stonebrick, 3, 3);
		world.setBlock(x + 1, y + 8, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 2, y + 8, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 3, y + 8, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 4, y + 8, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 5, y + 8, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 6, y + 8, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 7, y + 8, z + 10, Blocks.stonebrick, getBrick(rand), 3);
		world.setBlock(x + 8, y + 8, z + 10, Blocks.stonebrick, 3, 3);

		generate_r02_last(world, rand, x, y, z);
		return true;

	}
	public boolean generate_r02_last(World world, Random rand, int x, int y, int z)
	{

		world.setBlock(x + 4, y + 3, z + 2, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 3, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 5, y + 3, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 5, z + 3, Blocks.air, 0, 3);
		world.setBlock(x + 3, y + 5, z + 6, Blocks.air, 0, 3);
		world.setBlock(x + 4, y + 5, z + 7, Blocks.air, 0, 3);

		return true;

	}
	public int getBrick(Random rand) {
		return rand.nextInt(3);

	}
	public Block getShelf(Random rand) {
		int i = rand.nextInt(2);
		if(i == 0)
		{
			return Blocks.planks;
		}
		return Blocks.bookshelf;
	}
    private String pickMobSpawner(Random p_76543_1_)
    {
        return LegacyVanillaDungeon.mob(p_76543_1_);
    }

}
