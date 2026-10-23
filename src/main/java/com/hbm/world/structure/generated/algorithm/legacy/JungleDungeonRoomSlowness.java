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
/** Supplied X5846 algorithm; only native-version API boundary adapted. Source: legacy/src/main/java/com/hbm/world/generator/room/JungleDungeonRoomSlowness.java */
public class JungleDungeonRoomSlowness extends JungleDungeonRoom {

	public JungleDungeonRoomSlowness(CellularDungeon parent) {
		super(parent);
	}

	public void generateMain(final World world, final int x, final int y, final int z) {
		super.generateMain(world, x, y, z);

		ITimedJob job = new ITimedJob() {

			@Override
			public void work() {

				for(int a = 1; a < 4; a++) {
					for(int b = 1; b < 4; b++) {

						if(world.rand.nextInt(2) == 0) {
							Block bl = world.getBlock(x + a, y, z + b);
							if(bl.sameAs(ModBlocks.brick_jungle) || bl.sameAs(ModBlocks.brick_jungle_cracked) || bl.sameAs(ModBlocks.brick_jungle_lava)) {
								world.setBlock(x + a, y, z + b, ModBlocks.brick_jungle_trap, Trap.SLOWNESS.ordinal(), 3);
							}
						}
					}
				}
			}
		};

		TimedGenerator.addOp(world, job);
	}
}
