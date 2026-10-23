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
/** Supplied X5846 algorithm; only native-version API boundary adapted. Source: legacy/src/main/java/com/hbm/world/generator/room/JungleDungeonRoomMagic.java */
public class JungleDungeonRoomMagic extends JungleDungeonRoom {

	public JungleDungeonRoomMagic(CellularDungeon parent) {
		super(parent);
	}

	public void generateMain(final World world, final int x, final int y, final int z) {
		super.generateMain(world, x, y, z);

		ITimedJob job = new ITimedJob() {

			@Override
			public void work() {

				int ix = world.rand.nextInt(3) + 1;
				int iz = world.rand.nextInt(3) + 1;

				Block bl = world.getBlock(x + ix, y, z + iz);
				if(bl.sameAs(ModBlocks.brick_jungle) || bl.sameAs(ModBlocks.brick_jungle_cracked) || bl.sameAs(ModBlocks.brick_jungle_lava)) {
					world.setBlock(x + ix, y, z + iz, ModBlocks.brick_jungle_trap, Trap.MAGIC_CONVERSTION.ordinal(), 3);
				}
			}
		};

		TimedGenerator.addOp(world, job);
	}
}
