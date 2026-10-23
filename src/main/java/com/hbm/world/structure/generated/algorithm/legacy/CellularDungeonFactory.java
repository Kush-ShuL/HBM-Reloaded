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
/** Supplied X5846 algorithm; only native-version API boundary adapted. Source: legacy/src/main/java/com/hbm/world/generator/CellularDungeonFactory.java */
@Deprecated
public class CellularDungeonFactory {

	public static CellularDungeon jungle;

	public static void init() {

		jungle = new JungleDungeon(5, 5, 25, 25, 700, 6);
		for(int i = 0; i < 10; i++) jungle.rooms.add(new JungleDungeonRoom(jungle));
		jungle.rooms.add(new JungleDungeonRoomArrow(jungle));
		jungle.rooms.add(new JungleDungeonRoomArrowFire(jungle));
		jungle.rooms.add(new JungleDungeonRoomFire(jungle));
		jungle.rooms.add(new JungleDungeonRoomMagic(jungle));
		jungle.rooms.add(new JungleDungeonRoomMine(jungle));
		jungle.rooms.add(new JungleDungeonRoomPillar(jungle));
		jungle.rooms.add(new JungleDungeonRoomPoison(jungle));
		jungle.rooms.add(new JungleDungeonRoomRad(jungle));
		jungle.rooms.add(new JungleDungeonRoomRubble(jungle));
		jungle.rooms.add(new JungleDungeonRoomSlowness(jungle));
		jungle.rooms.add(new JungleDungeonRoomSpiders(jungle));
		jungle.rooms.add(new JungleDungeonRoomSpikes(jungle));
		jungle.rooms.add(new JungleDungeonRoomWeakness(jungle));
		jungle.rooms.add(new JungleDungeonRoomWeb(jungle));
		jungle.rooms.add(new JungleDungeonRoomZombie(jungle));
	}

}
