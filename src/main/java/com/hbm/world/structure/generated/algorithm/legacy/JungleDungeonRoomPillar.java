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
/** Supplied X5846 algorithm; only native-version API boundary adapted. Source: legacy/src/main/java/com/hbm/world/generator/room/JungleDungeonRoomPillar.java */
public class JungleDungeonRoomPillar extends JungleDungeonRoom {

	public JungleDungeonRoomPillar(CellularDungeon parent) {
		super(parent);
	}

	public void generateMain(final World world, final int x, final int y, final int z) {
		super.generateMain(world, x, y, z);

		ITimedJob job = new ITimedJob() {

			@Override
			public void work() {

				for(int a = 0; a < 3; a++) {
					for(int b = 0; b < 3; b++) {

						if(a == 1 && b == 1)
							continue;

						Block bl = world.getBlock(x + 1 + a, y + 4, z + 1 + b);

						if(bl.sameAs(ModBlocks.brick_jungle) || bl.sameAs(ModBlocks.brick_jungle_cracked) || bl.sameAs(ModBlocks.brick_jungle_lava) || bl.sameAs(ModBlocks.brick_jungle_trap)) {
							world.setBlock(x + 1 + a, y + 4, z + 1 + b, ModBlocks.brick_jungle_trap, Trap.PILLAR.ordinal(), 3);
						}
					}
				}
			}
		};

		TimedGenerator.addOp(world, job);
	}
}
