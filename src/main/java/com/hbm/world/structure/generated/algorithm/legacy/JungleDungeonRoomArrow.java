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
/** Supplied X5846 algorithm; only native-version API boundary adapted. Source: legacy/src/main/java/com/hbm/world/generator/room/JungleDungeonRoomArrow.java */
public class JungleDungeonRoomArrow extends JungleDungeonRoom {

	public JungleDungeonRoomArrow(CellularDungeon parent) {
		super(parent);
	}

	public void generateMain(final World world, final int x, final int y, final int z) {
		super.generateMain(world, x, y, z);

		ITimedJob job = new ITimedJob() {

			@Override
			public void work() {

				ITimedJob job = new ITimedJob() {

					@Override
					public void work() {

						for(int i = 1; i < 4; i++) {
							Block bl = world.getBlock(x + parent.width / 2, y + i, z);
							if(bl.sameAs(ModBlocks.brick_jungle) || bl.sameAs(ModBlocks.brick_jungle_cracked) || bl.sameAs(ModBlocks.brick_jungle_lava)) {
								world.setBlock(x + parent.width / 2, y + i, z, ModBlocks.brick_jungle_trap, Trap.ARROW.ordinal(), 3);
							}
						}

						for(int i = 1; i < 4; i++) {
							Block bl = world.getBlock(x + parent.width / 2, y + i, z + parent.width - 1);
							if(bl.sameAs(ModBlocks.brick_jungle) || bl.sameAs(ModBlocks.brick_jungle_cracked) || bl.sameAs(ModBlocks.brick_jungle_lava)) {
								world.setBlock(x + parent.width / 2, y + i, z + parent.width - 1, ModBlocks.brick_jungle_trap, Trap.ARROW.ordinal(), 3);
							}
						}

						for(int i = 1; i < 4; i++) {
							Block bl = world.getBlock(x, y + i, z + parent.width / 2);
							if(bl.sameAs(ModBlocks.brick_jungle) || bl.sameAs(ModBlocks.brick_jungle_cracked) || bl.sameAs(ModBlocks.brick_jungle_lava)) {
								world.setBlock(x, y + i, z + parent.width / 2, ModBlocks.brick_jungle_trap, Trap.ARROW.ordinal(), 3);
							}
						}

						for(int i = 1; i < 4; i++) {
							Block bl = world.getBlock(x + parent.width - 1, y + i, z + parent.width / 2);
							if(bl.sameAs(ModBlocks.brick_jungle) || bl.sameAs(ModBlocks.brick_jungle_cracked) || bl.sameAs(ModBlocks.brick_jungle_lava)) {
								world.setBlock(x + parent.width - 1, y + i, z + parent.width / 2, ModBlocks.brick_jungle_trap, Trap.ARROW.ordinal(), 3);
							}
						}
					}
				};

				TimedGenerator.addOp(world, job);
			}
		};

		TimedGenerator.addOp(world, job);
	}
}
