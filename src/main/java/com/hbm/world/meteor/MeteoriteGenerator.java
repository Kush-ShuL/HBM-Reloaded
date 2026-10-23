package com.hbm.world.meteor;
import com.hbm.block.fluid.*;
import com.hbm.damage.HbmDamageSources;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
/** Literal original layered meteor shapes and all special outcomes; native block states avoid fake fluid BlockItems. */
public final class MeteoriteGenerator {

	private boolean safeMode = false;

	public void generate(ServerLevel world, RandomSource rand, int x, int y, int z, boolean safe, boolean allowSpecials, boolean damagingImpact) {
		safeMode = safe;

		if(replacables.isEmpty()) {
			generateReplacables();
		}

		if(damagingImpact) {
			List<Entity> list=world.getEntities((Entity)null,new AABB(x-7.5,y-7.5,z-7.5,x+7.5,y+7.5,z+7.5));

			for(Entity e : list) {
				e.hurt(HbmDamageSources.source(world,HbmDamageSources.meteorite),1000);
			}
		}

		if(MeteorConfig.SPECIAL.get() && MeteorConfig.SPECIAL_CHANCE.get()>0 && allowSpecials)
			switch(rand.nextInt(MeteorConfig.SPECIAL_CHANCE.get())) {
			case 0:
                if(!MeteorConfig.special(0))break;
				// Meteor-only tiny meteorite
				List<BlockState> list0 = new ArrayList<BlockState>();
				list0.add(state("block_meteor"));
				generateBox(world, rand, x, y, z, list0);
				return;
			case 1:
                if(!MeteorConfig.special(1))break;
				// Large ore-only meteorite
				List<BlockState> list1 = new ArrayList<BlockState>();
				list1.addAll(this.getRandomOre(rand));
				int i = list1.size();
				for(int j = 0; j < i; j++)
					list1.add(state("block_meteor_broken"));
				generateSphere7x7(world, rand, x, y, z, list1);
				return;
			case 2:
                if(!MeteorConfig.special(2))break;
				// Medium ore-only meteorite
				List<BlockState> list2 = new ArrayList<BlockState>();
				list2.addAll(this.getRandomOre(rand));
				int k = list2.size() / 2;
				for(int j = 0; j < k; j++)
					list2.add(state("block_meteor_broken"));
				generateSphere5x5(world, rand, x, y, z, list2);
				return;
			case 3:
                if(!MeteorConfig.special(3))break;
				// Small pure ore meteorite
				List<BlockState> list3 = new ArrayList<BlockState>();
				list3.addAll(this.getRandomOre(rand));
				generateBox(world, rand, x, y, z, list3);
				return;
			case 4:
                if(!MeteorConfig.special(4))break;
				// Bamboozle
				world.explode(null,x+.5,y+.5,z+.5,15F,false,safe?Level.ExplosionInteraction.NONE:Level.ExplosionInteraction.TNT);
				ExplosionLarge.spawnRubble(world, x, y, z, 25);
				return;
			case 5:
                if(!MeteorConfig.special(5))break;
				// Large treasure-only meteorite
				List<BlockState> list4 = new ArrayList<BlockState>();
				list4.add(state("block_meteor_treasure"));
				list4.add(state("block_meteor_broken"));
				generateSphere7x7(world, rand, x, y, z, list4);
				return;
			case 6:
                if(!MeteorConfig.special(6))break;
				// Medium treasure-only meteorite
				List<BlockState> list5 = new ArrayList<BlockState>();
				list5.add(state("block_meteor_treasure"));
				list5.add(state("block_meteor_treasure"));
				list5.add(state("block_meteor_broken"));
				generateSphere5x5(world, rand, x, y, z, list5);
				return;
			case 7:
                if(!MeteorConfig.special(7))break;
				// Small pure treasure meteorite
				List<BlockState> list6 = new ArrayList<BlockState>();
				list6.add(state("block_meteor_treasure"));
				generateBox(world, rand, x, y, z, list6);
				return;
			case 8:
                if(!MeteorConfig.special(8))break;
				// Large nuclear meteorite
				List<BlockState> list7 = new ArrayList<BlockState>();
				list7.add(state("block_meteor_treasure"));
				List<BlockState> list8 = new ArrayList<BlockState>();
				list8.add(state("toxic_block"));
				generateSphere7x7(world, rand, x, y, z, list7);
				generateSphere5x5(world, rand, x, y, z, list8);
				return;
			case 9:
                if(!MeteorConfig.special(9))break;
				// Giant ore meteorite
				List<BlockState> list9 = new ArrayList<BlockState>();
				list9.add(state("block_meteor_broken"));
				generateSphere9x9(world, rand, x, y, z, list9);
				generateSphere7x7(world, rand, x, y, z, this.getRandomOre(rand));
				return;
			case 10:
                if(!MeteorConfig.special(10))break;
				// Tainted Meteorite
				List<BlockState> list10 = new ArrayList<BlockState>();
				list10.add(state("block_meteor_broken"));
				generateSphere5x5(world, rand, x, y, z, list10);
				setBlock(world, x, y, z, block("taint"), 9, 2);
				return;
			case 12:
                if(!MeteorConfig.special(12))break;
				// Star Blaster
				world.explode(null,x+.5,y+.5,z+.5,10F,false,safe?Level.ExplosionInteraction.NONE:Level.ExplosionInteraction.TNT);
				ItemStack weapon = new ItemStack(HbmContent.item("gun_b92"));
				weapon.set(DataComponents.CUSTOM_NAME,Component.literal("Star Blaster").withStyle(net.minecraft.ChatFormatting.BLUE));
				ItemEntity blaster = new ItemEntity(world,x+.5,y+.5,z+.5,weapon);
				world.addFreshEntity(blaster);
				return;
			}

        generateLayers(world, rand, x, y, z);
    }

    /** The original HbmWorldGen calls generate(false,false,false): normal layers only. */
    public void generateNatural(LevelAccessor world, RandomSource rand, int x, int y, int z) {
        safeMode = false;
        generateLayers(world, rand, x, y, z);
    }

    private void generateLayers(LevelAccessor world, RandomSource rand, int x, int y, int z) {
		switch(MeteorConfig.shape(rand)) {
		case 0:
			generateLarge(world, rand, x, y, z);
			break;
		case 1:
			generateMedium(world, rand, x, y, z);
			break;
		case 2:
			generateSmall(world, rand, x, y, z);
			break;
		}
	}

	public void generateLarge(LevelAccessor world, RandomSource rand, int x, int y, int z) {
		// 0 - Molten
		// 1 - Cobble
		// 2 - Broken
		// 3 - Mix
		int hull = rand.nextInt(4);

		// 0 - Cobble
		// 1 - Broken
		// 2 - Mix
		int outerPadding = 0;

		if(hull == 2)
			outerPadding = 1 + rand.nextInt(2);
		else if(hull == 3)
			outerPadding = 2;

		// 0 - Broken
		// 1 - Stone
		// 2 - Netherrack
		int innerPadding = rand.nextInt(hull == 0 ? 3 : 2);

		// 0 - Meteor
		// 1 - Treasure
		// 2 - Ore
		int core = rand.nextInt(2);
		if(innerPadding > 0)
			core = 2;

		List<BlockState> hullL = new ArrayList<BlockState>();
		switch(hull) {
		case 0:
			hullL.add(state("block_meteor_molten"));
			break;
		case 1:
			hullL.add(state("block_meteor_cobble"));
			break;
		case 2:
			for(int i = 0; i < 99; i++)
				hullL.add(state("block_meteor_broken"));
			hullL.add(state("block_meteor_treasure"));
			break;
		case 3:
			hullL.add(state("block_meteor_molten"));
			hullL.add(state("block_meteor_broken"));
			break;
		}

		List<BlockState> opL = new ArrayList<BlockState>();
		switch(outerPadding) {
		case 0:
			opL.add(state("block_meteor_cobble"));
			break;
		case 1:
			for(int i = 0; i < 99; i++)
				opL.add(state("block_meteor_broken"));
			opL.add(state("block_meteor_treasure"));
			break;
		case 2:
			opL.add(state("block_meteor_cobble"));
			opL.add(state("block_meteor_broken"));
			break;
		}

		List<BlockState> ipL = new ArrayList<BlockState>();
		switch(innerPadding) {
		case 0:
			for(int i = 0; i < 99; i++)
				ipL.add(state("block_meteor_broken"));
			ipL.add(state("block_meteor_treasure"));
			break;
		case 1:
			ipL.add(state("block_meteor_broken"));
			break;
		case 2:
			ipL.add(state("block_meteor_cobble"));
			break;
		}

		List<BlockState> coreL = new ArrayList<BlockState>();
		switch(core) {
		case 0:
			coreL.add(state("block_meteor"));
			break;
		case 1:
			coreL.add(state("block_meteor_treasure"));
			break;
		case 2:
			coreL.addAll(this.getRandomOre(rand));
			break;
		}

		switch(rand.nextInt(5)) {
		case 0:
			genL1(world, rand, x, y, z, hullL, opL, ipL, coreL);
			break;
		case 1:
			genL2(world, rand, x, y, z, hullL, opL, ipL, coreL);
			break;
		case 2:
			genL3(world, rand, x, y, z, hullL, opL, ipL, coreL);
			break;
		case 3:
			genL4(world, rand, x, y, z, hullL, opL, ipL, coreL);
			break;
		case 4:
			genL5(world, rand, x, y, z, hullL, opL, ipL, coreL);
			break;
		}
	}

	public void generateMedium(LevelAccessor world, RandomSource rand, int x, int y, int z) {
		// 0 - Molten
		// 1 - Cobble
		// 2 - Broken
		// 3 - Mix
		int hull = rand.nextInt(4);

		// 0 - Cobble
		// 1 - Broken
		// 2 - Mix
		int outerPadding = 0;

		if(hull == 2)
			outerPadding = 1 + rand.nextInt(2);
		else if(hull == 3)
			outerPadding = 2;

		// 0 - Broken
		// 1 - Stone
		// 2 - Netherrack
		int innerPadding = rand.nextInt(hull == 0 ? 3 : 2);

		// 0 - Meteor
		// 1 - Treasure
		// 2 - Ore
		int core = rand.nextInt(2);
		if(innerPadding > 0)
			core = 2;

		List<BlockState> hullL = new ArrayList<BlockState>();
		switch(hull) {
		case 0:
			hullL.add(state("block_meteor_molten"));
			break;
		case 1:
			hullL.add(state("block_meteor_cobble"));
			break;
		case 2:
			for(int i = 0; i < 99; i++)
				hullL.add(state("block_meteor_broken"));
			hullL.add(state("block_meteor_treasure"));
			break;
		case 3:
			hullL.add(state("block_meteor_molten"));
			hullL.add(state("block_meteor_broken"));
			break;
		}

		List<BlockState> opL = new ArrayList<BlockState>();
		switch(outerPadding) {
		case 0:
			opL.add(state("block_meteor_cobble"));
			break;
		case 1:
			for(int i = 0; i < 99; i++)
				opL.add(state("block_meteor_broken"));
			opL.add(state("block_meteor_treasure"));
			break;
		case 2:
			opL.add(state("block_meteor_cobble"));
			opL.add(state("block_meteor_broken"));
			break;
		}

		List<BlockState> ipL = new ArrayList<BlockState>();
		switch(innerPadding) {
		case 0:
			for(int i = 0; i < 99; i++)
				ipL.add(state("block_meteor_broken"));
			ipL.add(state("block_meteor_treasure"));
			break;
		case 1:
			ipL.add(state("block_meteor_broken"));
			break;
		case 2:
			ipL.add(state("block_meteor_cobble"));
			break;
		}

		List<BlockState> coreL = new ArrayList<BlockState>();
		switch(core) {
		case 0:
			coreL.add(state("block_meteor"));
			break;
		case 1:
			coreL.add(state("block_meteor_treasure"));
			break;
		case 2:
			coreL.addAll(this.getRandomOre(rand));
			break;
		}

		List<BlockState> sCore = new ArrayList<BlockState>();
		switch(core) {
		case 0:
			sCore.add(state("block_meteor"));
			break;
		case 1:
			sCore.add(state("block_meteor_treasure"));
			break;
		case 2:
			sCore.add(state("block_meteor_treasure"));
			sCore.add(state("block_meteor"));
			break;
		}

		switch(rand.nextInt(6)) {
		case 0:
			genM1(world, rand, x, y, z, hullL, opL, ipL, sCore);
			break;
		case 1:
			genM2(world, rand, x, y, z, hullL, opL, ipL, coreL);
			break;
		case 2:
			genM3(world, rand, x, y, z, hullL, opL, ipL, coreL);
			break;
		case 3:
			genM4(world, rand, x, y, z, hullL, opL, ipL, coreL);
			break;
		case 4:
			genM5(world, rand, x, y, z, hullL, opL, ipL, coreL);
			break;
		case 5:
			genM6(world, rand, x, y, z, hullL, opL, ipL, coreL);
			break;
		}
	}

	public void generateSmall(LevelAccessor world, RandomSource rand, int x, int y, int z) {
		// 0 - Molten
		// 1 - Cobble
		// 2 - Broken
		// 3 - Mix
		int hull = rand.nextInt(4);

		// 0 - Meteor
		// 1 - Treasure
		// 2 - Ore
		int core = rand.nextInt(3);

		List<BlockState> hullL = new ArrayList<BlockState>();
		switch(hull) {
		case 0:
			hullL.add(state("block_meteor_molten"));
			break;
		case 1:
			hullL.add(state("block_meteor_cobble"));
			break;
		case 2:
			for(int i = 0; i < 99; i++)
				hullL.add(state("block_meteor_broken"));
			hullL.add(state("block_meteor_treasure"));
			break;
		case 3:
			hullL.add(state("block_meteor_molten"));
			hullL.add(state("block_meteor_broken"));
			break;
		}

		List<BlockState> sCore = new ArrayList<BlockState>();
		switch(core) {
		case 0:
			sCore.add(state("block_meteor"));
			break;
		case 1:
			sCore.add(state("block_meteor_treasure"));
			break;
		case 2:
			sCore.add(state("block_meteor_treasure"));
			sCore.add(state("block_meteor"));
			break;
		}

		generateBox(world, rand, x, y, z, hullL);
		BlockState stack = choose(sCore,rand);
		setBlock(world, x, y, z, stack, 2);
	}

	public void genL1(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
		generateSphere7x7(world, rand, x, y, z, hull);
		generateStar5x5(world, rand, x, y, z, op);
		generateStar3x3(world, rand, x, y, z, ip);
		BlockState stack = choose(core,rand);
		setBlock(world, x, y, z, stack, 2);
	}

	public void genL2(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
		generateSphere7x7(world, rand, x, y, z, hull);
		generateSphere5x5(world, rand, x, y, z, op);
		generateStar3x3(world, rand, x, y, z, ip);
		BlockState stack = choose(core,rand);
		setBlock(world, x, y, z, stack, 2);
	}

	public void genL3(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
		generateSphere7x7(world, rand, x, y, z, hull);
		generateSphere5x5(world, rand, x, y, z, op);
		generateBox(world, rand, x, y, z, ip);
		BlockState stack = choose(core,rand);
		setBlock(world, x, y, z, stack, 2);
	}

	public void genL4(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
		generateSphere7x7(world, rand, x, y, z, hull);
		generateSphere5x5(world, rand, x, y, z, op);
		generateBox(world, rand, x, y, z, ip);
		generateStar3x3(world, rand, x, y, z, this.getRandomOre(rand));
		BlockState stack = choose(core,rand);
		setBlock(world, x, y, z, stack, 2);
	}

	public void genL5(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
		generateSphere7x7(world, rand, x, y, z, hull);
		generateSphere5x5(world, rand, x, y, z, op);
		generateStar5x5(world, rand, x, y, z, ip);
		generateStar3x3(world, rand, x, y, z, this.getRandomOre(rand));
		BlockState stack = choose(core,rand);
		setBlock(world, x, y, z, stack, 2);
	}

	public void genM1(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
		generateSphere5x5(world, rand, x, y, z, hull);
		BlockState stack = choose(core,rand);
		setBlock(world, x, y, z, stack, 2);
	}

	public void genM2(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
		generateSphere5x5(world, rand, x, y, z, hull);
		generateStar3x3(world, rand, x, y, z, op);
		BlockState stack = choose(core,rand);
		setBlock(world, x, y, z, stack, 2);
	}

	public void genM3(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
		generateSphere5x5(world, rand, x, y, z, hull);
		generateBox(world, rand, x, y, z, op);
		BlockState stack = choose(core,rand);
		setBlock(world, x, y, z, stack, 2);
	}

	public void genM4(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
		generateSphere5x5(world, rand, x, y, z, hull);
		generateBox(world, rand, x, y, z, op);
		generateStar3x3(world, rand, x, y, z, ip);
		BlockState stack = choose(core,rand);
		setBlock(world, x, y, z, stack, 2);
	}

	public void genM5(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
		generateSphere5x5(world, rand, x, y, z, hull);
		generateBox(world, rand, x, y, z, ip);
		BlockState stack = choose(core,rand);
		setBlock(world, x, y, z, stack, 2);
	}

	public void genM6(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> hull, List<BlockState> op, List<BlockState> ip, List<BlockState> core) {
		generateSphere5x5(world, rand, x, y, z, hull);
		generateBox(world, rand, x, y, z, ip);
		generateStar3x3(world, rand, x, y, z, this.getRandomOre(rand));
		BlockState stack = choose(core,rand);
		setBlock(world, x, y, z, stack, 2);
	}

	public void generateSphere7x7(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> set) {
		for(int a = -3; a < 4; a++)
			for(int b = -1; b < 2; b++)
				for(int c = -1; c < 2; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
		for(int a = -1; a < 2; a++)
			for(int b = -3; b < 4; b++)
				for(int c = -1; c < 2; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
		for(int a = -1; a < 2; a++)
			for(int b = -1; b < 2; b++)
				for(int c = -3; c < 4; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}

		for(int a = -2; a < 3; a++)
			for(int b = -2; b < 3; b++)
				for(int c = -1; c < 2; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
		for(int a = -1; a < 2; a++)
			for(int b = -2; b < 3; b++)
				for(int c = -2; c < 3; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
		for(int a = -2; a < 3; a++)
			for(int b = -1; b < 2; b++)
				for(int c = -2; c < 3; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
	}

	public void generateSphere5x5(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> set) {
		for(int a = -2; a < 3; a++)
			for(int b = -1; b < 2; b++)
				for(int c = -1; c < 2; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
		for(int a = -1; a < 2; a++)
			for(int b = -2; b < 3; b++)
				for(int c = -1; c < 2; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
		for(int a = -1; a < 2; a++)
			for(int b = -1; b < 2; b++)
				for(int c = -2; c < 3; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
	}

	public void generateSphere9x9(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> set) {
		for(int a = -4; a < 5; a++)
			for(int b = -1; b < 2; b++)
				for(int c = -1; c < 2; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
		for(int a = -1; a < 2; a++)
			for(int b = -4; b < 5; b++)
				for(int c = -1; c < 2; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
		for(int a = -1; a < 2; a++)
			for(int b = -1; b < 2; b++)
				for(int c = -4; c < 5; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}

		for(int a = -1; a < 2; a++)
			for(int b = -3; b < 4; b++)
				for(int c = -3; c < 4; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
		for(int a = -3; a < 4; a++)
			for(int b = -1; b < 2; b++)
				for(int c = -3; c < 4; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
		for(int a = -3; a < 4; a++)
			for(int b = -3; b < 4; b++)
				for(int c = -1; c < 2; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}

		for(int a = -3; a < 4; a++)
			for(int b = -2; b < 3; b++)
				for(int c = -2; c < 3; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
		for(int a = -2; a < 3; a++)
			for(int b = -3; b < 4; b++)
				for(int c = -2; c < 3; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
		for(int a = -2; a < 3; a++)
			for(int b = -2; b < 3; b++)
				for(int c = -3; c < 4; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
	}

	public void generateBox(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> set) {
		for(int a = -1; a < 2; a++)
			for(int b = -1; b < 2; b++)
				for(int c = -1; c < 2; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}
	}

	public void generateStar5x5(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> set) {
		for(int a = -1; a < 2; a++)
			for(int b = -1; b < 2; b++)
				for(int c = -1; c < 2; c++) {
					BlockState stack = choose(set,rand);
					setBlock(world, x + a, y + b, z + c, stack, 2);
				}

		BlockState stack = choose(set,rand);
		setBlock(world, x + 2, y, z, stack, 2);
		stack = choose(set,rand);
		setBlock(world, x - 2, y, z, stack, 2);
		stack = choose(set,rand);
		setBlock(world, x, y + 2, z, stack, 2);
		stack = choose(set,rand);
		setBlock(world, x, y - 2, z, stack, 2);
		stack = choose(set,rand);
		setBlock(world, x, y, z + 2, stack, 2);
		stack = choose(set,rand);
		setBlock(world, x, y, z - 2, stack, 2);
	}

	public void generateStar3x3(LevelAccessor world, RandomSource rand, int x, int y, int z, List<BlockState> set) {

		BlockState stack = choose(set,rand);
		setBlock(world, x, y, z, stack, 2);
		stack = choose(set,rand);
		setBlock(world, x + 1, y, z, stack, 2);
		stack = choose(set,rand);
		setBlock(world, x - 1, y, z, stack, 2);
		stack = choose(set,rand);
		setBlock(world, x, y + 1, z, stack, 2);
		stack = choose(set,rand);
		setBlock(world, x, y - 1, z, stack, 2);
		stack = choose(set,rand);
		setBlock(world, x, y, z + 1, stack, 2);
		stack = choose(set,rand);
		setBlock(world, x, y, z - 1, stack, 2);
	}

	public List<BlockState> getRandomOre(RandomSource rand) {
		List<BlockState> ores = new ArrayList<BlockState>();
		for(int meta=0;meta<5;meta++)for(int weight=0;weight<MeteorConfig.oreWeight(meta);weight++)ores.add(state("ore_meteor").setValue(com.hbm.block.ore.LegacyOreBlock.LEGACY_META,meta));
		return ores;
	}

	private void setBlock(LevelAccessor world, int x, int y, int z, Block b, int meta, int flag) {
        var pos=new BlockPos(x,y,z);if(world.isOutsideBuildHeight(pos))return;var target=world.getBlockState(pos);
        if(safeMode&&!LegacyMaterials.of(target).isReplaceable()&&!replacables.contains(target.getBlock()))return;
        float hardness=target.getDestroySpeed(world,pos);if(hardness!=-1&&hardness<10_000)world.setBlock(pos,metadata(b.defaultBlockState(),meta),flag);
	}

	public static HashSet<Block> replacables = new HashSet();

	public static void generateReplacables() {
		replacables.add(block("block_meteor"));
		replacables.add(block("block_meteor_broken"));
		replacables.add(block("block_meteor_cobble"));
		replacables.add(block("block_meteor_molten"));
		replacables.add(block("block_meteor_treasure"));
		replacables.add(block("ore_meteor"));
	}

 private static BlockState choose(List<BlockState> pool,RandomSource random){return pool.isEmpty()?null:pool.get(random.nextInt(pool.size()));}
 private void setBlock(LevelAccessor world,int x,int y,int z,BlockState state,int flag){if(state!=null)setBlock(world,x,y,z,state.getBlock(),metadata(state),flag);}
 private static Block block(String name){return name.equals("toxic_block")?HbmWorldFluids.block(name):HbmContent.block(name);}
 private static BlockState state(String name){return block(name).defaultBlockState();}
 private static int metadata(BlockState state){for(var p:state.getProperties())if(p instanceof IntegerProperty integer&&(p.getName().equals("legacy_meta")||p.getName().equals("level")))return state.getValue(integer);return 0;}
 private static BlockState metadata(BlockState state,int meta){for(var p:state.getProperties())if(p instanceof IntegerProperty integer&&(p.getName().equals("legacy_meta")||p.getName().equals("level"))&&integer.getPossibleValues().contains(meta))return state.setValue(integer,meta);return state;}
}
