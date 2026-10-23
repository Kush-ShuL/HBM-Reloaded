package com.hbm.world.structure.generated.algorithm;
import java.util.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import com.hbm.world.structure.generated.*;
import com.hbm.world.structure.generated.compat.*;
import com.hbm.world.structure.generated.algorithm.Component.*;
import com.hbm.world.structure.generated.algorithm.ProceduralStructureStart.ProceduralComponent;
/** Supplied X5846 algorithm; only native-version API boundary adapted. Source: legacy/src/main/java/com/hbm/world/gen/component/Component.java */
abstract public class Component extends StructureComponent {

	protected int hpos = -1;

	protected Component() {
		super(0);
	}

	protected Component(int componentType) {
		super(componentType);
	}

	protected Component(Random rand, int minX, int minY, int minZ, int maxX, int maxY, int maxZ ) {
		super(0);
		this.coordBaseMode = rand.nextInt(4);

		switch(this.coordBaseMode) {
		case 0:
			this.boundingBox = new StructureBoundingBox(minX, minY, minZ, minX + maxX, minY + maxY, minZ + maxZ);
			break;
		case 1:
			this.boundingBox = new StructureBoundingBox(minX, minY, minZ, minX + maxZ, minY + maxY, minZ + maxX);
			break;
		case 2:
			this.boundingBox = new StructureBoundingBox(minX, minY, minZ, minX + maxX, minY + maxY, minZ + maxZ);
			break;
		case 3:
			this.boundingBox = new StructureBoundingBox(minX, minY, minZ, minX + maxZ, minY + maxY, minZ + maxX);
			break;
		default:
			this.boundingBox = new StructureBoundingBox(minX, minY, minZ, minX + maxX, minY + maxY, minZ + maxZ);

		}
	}

	protected void func_143012_a(CompoundTag nbt) {
		nbt.putInt("HPos", this.hpos);
	}

	protected void func_143011_b(CompoundTag nbt) {
		this.hpos = nbt.getInt("HPos");
	}

	protected boolean setAverageHeight(World world, StructureBoundingBox box, int y) {

        // Native postProcess runs once per intersecting chunk. Resolve this
        // component's ground anchor once, not from each later clipped fragment.
        // Restore the shared baseline before the caller applies its source -1/-7
        // local offset; just returning true would accumulate that offset per chunk.
        if(this.hpos != -1) {
            this.boundingBox.offset(0, this.hpos - this.boundingBox.minY, 0);
            return true;
        }

		int total = 0;
		int iterations = 0;

		for(int z = this.boundingBox.minZ; z <= this.boundingBox.maxZ; z++) {
			for(int x = this.boundingBox.minX; x <= this.boundingBox.maxX; x++) {
				if(box.isVecInside(x, y, z)) {
					total += Math.max(world.getTopSolidOrLiquidBlock(x, z), world.provider.getAverageGroundLevel());
					iterations++;
				}
			}
		}

		if(iterations == 0)
			return false;

		this.hpos = total / iterations;
		this.boundingBox.offset(0, this.hpos - this.boundingBox.minY, 0);
		return true;
	}

	protected static int getAverageHeight(World world, StructureBoundingBox area, StructureBoundingBox box, int y) {

		int total = 0;
		int iterations = 0;

		for(int z = area.minZ; z <= area.maxZ; z++) {
			for(int x = area.minX; x <= area.maxX; x++) {
				if(box.isVecInside(x, y, z)) {
					total += Math.max(world.getTopSolidOrLiquidBlock(x, z), world.provider.getAverageGroundLevel());
					iterations++;
				}
			}
		}

		if(iterations == 0)
			return -1;

		return total / iterations;
	}

	public int getCoordMode() {
		return this.coordBaseMode;
	}

	protected int getPillarMeta(int metadata) {
		if(this.coordBaseMode % 2 != 0 && this.coordBaseMode != -1)
			metadata = metadata ^ 12;

		return metadata;
	}

	protected int getDecoMeta(int metadata) {
		switch(this.coordBaseMode) {
		case 0:
			switch(metadata) {
			case 2: return 2;
			case 3: return 3;
			case 4: return 4;
			case 5: return 5;
			}
		case 1:
			switch(metadata) {
			case 2: return 5;
			case 3: return 4;
			case 4: return 2;
			case 5: return 3;
			}
		case 2:
			switch(metadata) {
			case 2: return 3;
			case 3: return 2;
			case 4: return 5;
			case 5: return 4;
			}
		case 3:
			switch(metadata) {
			case 2: return 4;
			case 3: return 5;
			case 4: return 3;
			case 5: return 2;
			}
		}
		return 0;
	}

	protected int getDecoModelMeta(int metadata) {

		switch(this.coordBaseMode) {
		default:
			break;
		case 1:
			if((metadata & 3) < 2)
				metadata = metadata ^ 3;
			else
				metadata = metadata ^ 2;
			break;
		case 2:
			metadata = metadata ^ 1;
			break;
		case 3:
			if((metadata & 3) < 2)
				metadata = metadata ^ 2;
			else
				metadata = metadata ^ 3;
			break;
		}

		return metadata << 2;
	}

	protected int getCRTMeta(int meta) {
		return (meta + this.coordBaseMode) % 4;
	}

	protected int getStairMeta(int metadata) {
		switch(this.coordBaseMode) {
		default:
			break;
		case 1:
			if((metadata & 3) < 2)
				metadata = metadata ^ 2;
			else
				metadata = metadata ^ 3;
			break;
		case 2:
			metadata = metadata ^ 1;
			break;
		case 3:
			if((metadata & 3) < 2)
				metadata = metadata ^ 3;
			else
				metadata = metadata ^ 2;
			break;
		}

		return metadata;
	}

	protected void placeDoor(World world, StructureBoundingBox box, Block door, int dirMeta, boolean opensRight, boolean isOpen, int featureX, int featureY, int featureZ) {
		int posX = this.getXWithOffset(featureX, featureZ);
		int posY = this.getYWithOffset(featureY);
		int posZ = this.getZWithOffset(featureX, featureZ);

		if(!box.isVecInside(posX, posY, posZ)) return;

		switch(this.coordBaseMode) {
		default:
			break;
		case 1:
			dirMeta = (dirMeta + 1) % 4; break;
		case 2:
			dirMeta ^= 2; break;
		case 3:
			dirMeta = (dirMeta + 3) % 4; break;
		}

		int metaTop = opensRight ? 0b1001 : 0b1000;
		int metaBottom = dirMeta | (isOpen ? 0b100 : 0);

		if(world.doesBlockHaveSolidTopSurface(world, posX, posY - 1, posZ)) {
			World.placeDoorHalves(world,posX,posY,posZ,door,metaBottom,metaTop,2);
		}
	}

	protected void placeLever(World world, StructureBoundingBox box, int dirMeta, boolean on, int featureX, int featureY, int featureZ) {
		int posX = this.getXWithOffset(featureX, featureZ);
		int posY = this.getYWithOffset(featureY);
		int posZ = this.getZWithOffset(featureX, featureZ);

		if(!box.isVecInside(posX, posY, posZ)) return;

		if(dirMeta <= 0 || dirMeta >= 7) {
			switch(this.coordBaseMode) {
			case 1: case 3:
				dirMeta ^= 0b111;
			}
		} else if(dirMeta >= 5) {
			switch(this.coordBaseMode) {
			case 1: case 3:
				dirMeta = (dirMeta + 1) % 2 + 5;
			}
		} else {
			dirMeta = getButtonMeta(dirMeta);
		}

		world.setBlock(posX, posY, posZ, Blocks.lever, on ? dirMeta | 8 : dirMeta, 2);
	}

	protected int getButtonMeta(int dirMeta) {
		switch(this.coordBaseMode) {
		case 1:
			if(dirMeta <= 2) return dirMeta + 2;
			else if(dirMeta < 4) return dirMeta - 1;
			else return dirMeta - 3;
		case 2:
			return dirMeta + (dirMeta % 2 == 0 ? -1 : 1);
		case 3:
			if(dirMeta <= 1) return dirMeta + 3;
			else if(dirMeta <= 2) return dirMeta + 1;
			else return dirMeta - 2;
		default:
			return dirMeta;
		}
	}

	protected void placeBed(World world, StructureBoundingBox box, int meta, int featureX, int featureY, int featureZ) {
		int xOffset = 0;
		int zOffset = 0;

		switch(meta & 3) {
		default:
			zOffset = 1; break;
		case 1:
			xOffset = -1; break;
		case 2:
			zOffset = -1; break;
		case 3:
			xOffset = 1; break;
		}

		switch(this.coordBaseMode) {
		default:
			break;
		case 1:
			meta = (meta + 1) % 4; break;
		case 2:
			meta ^= 2; break;
		case 3:
			meta = (meta - 1) % 4; break;
		}

		placeBlockAtCurrentPosition(world, Blocks.bed, meta, featureX, featureY, featureZ, box);
		placeBlockAtCurrentPosition(world, Blocks.bed, meta + 8, featureX + xOffset, featureY, featureZ + zOffset, box);
	}

	protected int getTripwireMeta(int metadata) {
		switch(this.coordBaseMode) {
		default:
			return metadata;
		case 1:
			return (metadata + 1) % 4;
		case 2:
			return metadata ^ 2;
		case 3:
			return (metadata - 1) % 4;
		}
	}

	protected boolean generateInvContents(World world, StructureBoundingBox box, Random rand, Block block, int featureX, int featureY, int featureZ, GeneratedLoot.Pool content, int amount) {
		return generateInvContents(world, box, rand, block, 0, featureX, featureY, featureZ, content, amount);
	}

	protected boolean generateInvContents(World world, StructureBoundingBox box, Random rand, Block block, int meta, int featureX, int featureY, int featureZ, GeneratedLoot.Pool content, int amount) {
		int posX = this.getXWithOffset(featureX, featureZ);
		int posY = this.getYWithOffset(featureY);
		int posZ = this.getZWithOffset(featureX, featureZ);

		if(!box.isVecInside(posX, posY, posZ) || world.sameBlock(posX,posY,posZ,block))
			return true;

		this.placeBlockAtCurrentPosition(world, block, meta, featureX, featureY, featureZ, box);
		Container inventory = (Container)world.getTileEntity(posX, posY, posZ);

		if(inventory != null) {
			amount = (int)Math.floor(amount * StructureConfig.lootAmountFactor);
			GeneratedLoot.fill(world,rand, content, inventory, amount < 1 ? 1 : amount);
			return true;
		}

		return false;
	}

	protected boolean generateLockableContents(World world, StructureBoundingBox box, Random rand, Block block, int featureX, int featureY, int featureZ,
			GeneratedLoot.Pool content, int amount, double mod) {
		return generateLockableContents(world, box, rand, block, 0, featureX, featureY, featureZ, content, amount, mod);
	}

	protected boolean generateLockableContents(World world, StructureBoundingBox box, Random rand, Block block, int meta, int featureX, int featureY, int featureZ,
			GeneratedLoot.Pool content, int amount, double mod) {
		int posX = this.getXWithOffset(featureX, featureZ);
		int posY = this.getYWithOffset(featureY);
		int posZ = this.getZWithOffset(featureX, featureZ);

		if(!box.isVecInside(posX, posY, posZ) || world.sameBlock(posX,posY,posZ,block))
			return false;

		this.placeBlockAtCurrentPosition(world, block, meta, featureX, featureY, featureZ, box);
		BlockEntity tile = world.getTileEntity(posX, posY, posZ);
		GeneratedLoot.Lockable lock=GeneratedLoot.lockable(tile);
		Container inventory = (Container) tile;

		if(inventory != null && lock != null) {
			amount = (int)Math.floor(amount * StructureConfig.lootAmountFactor);
			GeneratedLoot.fill(world,rand, content, inventory, amount < 1 ? 1 : amount);

			lock.setPins(rand.nextInt(999) + 1);
			lock.setMod(mod);
			lock.lock();
			return true;
		}

		return false;
	}

	protected void generateLoreBook(World world, StructureBoundingBox box, int featureX, int featureY, int featureZ, int slot, ItemStack stack) {
		int posX = this.getXWithOffset(featureX, featureZ);
		int posY = this.getYWithOffset(featureY);
		int posZ = this.getZWithOffset(featureX, featureZ);

		if(!box.isVecInside(posX, posY, posZ)) return;

		Container inventory = (Container) world.getTileEntity(posX, posY, posZ);

		if(inventory != null) {
			inventory.setItem(slot, stack);
		}
	}

	protected void placeRandomBobble(World world, StructureBoundingBox box, Random rand, int featureX, int featureY, int featureZ) {
		int posX = this.getXWithOffset(featureX, featureZ);
		int posY = this.getYWithOffset(featureY);
		int posZ = this.getZWithOffset(featureX, featureZ);

		placeBlockAtCurrentPosition(world, ModBlocks.bobblehead, rand.nextInt(16), featureX, featureY, featureZ, box);
		BlockEntity tile = world.getTileEntity(posX, posY, posZ);

		if(GeneratedAttachments.isBobble(tile)) {
			GeneratedAttachments.Bobble bobble=GeneratedAttachments.bobble(world,tile);
			bobble.type = GeneratedAttachments.BobbleType.values()[rand.nextInt(GeneratedAttachments.BobbleType.values().length - 1) + 1];
			bobble.markDirty();
		}
	}

	protected void placeFoundationUnderneath(World world, Block placeBlock, int meta, int minX, int minZ, int maxX, int maxZ, int featureY, StructureBoundingBox box) {

		for(int featureX = minX; featureX <= maxX; featureX++) {
			for(int featureZ = minZ; featureZ <= maxZ; featureZ++) {
				int posX = this.getXWithOffset(featureX, featureZ);
				int posY = this.getYWithOffset(featureY);
				int posZ = this.getZWithOffset(featureX, featureZ);

				if(box.isVecInside(posX, posY, posZ)) {
					Block block = world.getBlock(posX, posY, posZ);
					int brake = 0;

					while ((world.isAirBlock(posX, posY, posZ) ||
							!block.getMaterial().isSolid() ||
							(block.isFoliage(world, posX, posY, posZ) || block.getMaterial() == Material.leaves)) &&
							posY > 1 && brake <= 15) {
						world.setBlock(posX, posY, posZ, placeBlock, meta, 2);
						block = world.getBlock(posX, --posY, posZ);

						brake++;
					}
				}
			}
		}
	}

	protected void placeBlocksOnTop(World world, StructureBoundingBox box, Block block, int minX, int minZ, int maxX, int maxZ, int height) {

		for(int x = minX; x <= maxX; x++) {
			for(int z = minZ; z <= maxZ; z++) {
				int posX = this.getXWithOffset(x, z);
				int posZ = this.getZWithOffset(x, z);
				int topHeight = world.getTopSolidOrLiquidBlock(posX, posZ);

				if(!world.getBlock(posX, topHeight, posZ).getMaterial().isLiquid()) {

					for(int i = 0; i < height; i++) {
						int posY = topHeight + i;

						world.setBlock(posX, posY, posZ, block, 0, 2);
					}
				}
			}
		}
	}

	@Override
	public int getXWithOffset(int x, int z) {
		switch(this.coordBaseMode) {
		case 0:
			return this.boundingBox.minX + x;
		case 1:
			return this.boundingBox.maxX - z;
		case 2:
			return this.boundingBox.maxX - x;
		case 3:
			return this.boundingBox.minX + z;
		default:
			return x;
		}
	}

	@Override
	public int getZWithOffset(int x, int z) {
		switch(this.coordBaseMode) {
		case 0:
			return this.boundingBox.minZ + z;
		case 1:
			return this.boundingBox.minZ + x;
		case 2:
			return this.boundingBox.maxZ - z;
		case 3:
			return this.boundingBox.maxZ - x;
		default:
			return z;
		}
	}

	protected void fillWithAir(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {

		if(getYWithOffset(minY) < box.minY || getYWithOffset(maxY) > box.maxY)
			return;

		for(int x = minX; x <= maxX; x++) {

			for(int z = minZ; z <= maxZ; z++) {
				int posX = getXWithOffset(x, z);
				int posZ = getZWithOffset(x, z);

				if(posX >= box.minX && posX <= box.maxX && posZ >= box.minZ && posZ <= box.maxZ) {
					for(int y = minY; y <= maxY; y++) {
						int posY = getYWithOffset(y);

						world.setBlock(posX, posY, posZ, Blocks.air, 0, 2);
					}
				}
			}
		}
	}

	@Override
	protected void fillWithBlocks(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block block, Block replaceBlock, boolean onlyReplace) {

		if(getYWithOffset(minY) < box.minY || getYWithOffset(maxY) > box.maxY)
			return;

		for(int x = minX; x <= maxX; x++) {

			for(int z = minZ; z <= maxZ; z++) {
				int posX = getXWithOffset(x, z);
				int posZ = getZWithOffset(x, z);

				if(posX >= box.minX && posX <= box.maxX && posZ >= box.minZ && posZ <= box.maxZ) {
					for(int y = minY; y <= maxY; y++) {
						int posY = getYWithOffset(y);

						if(!onlyReplace || world.getBlock(posX, posY, posZ).getMaterial() != Material.air) {
							if(x != minX && x != maxX && y != minY && y != maxY && z != minZ && z != maxZ)
								world.setBlock(posX, posY, posZ, replaceBlock, 0, 2);
							else
								world.setBlock(posX, posY, posZ, block, 0, 2);
						}
					}
				}
			}
		}
	}

	protected void fillWithBlocks(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block block) {

		if(getYWithOffset(minY) < box.minY || getYWithOffset(maxY) > box.maxY)
			return;

		for(int x = minX; x <= maxX; x++) {

			for(int z = minZ; z <= maxZ; z++) {
				int posX = getXWithOffset(x, z);
				int posZ = getZWithOffset(x, z);

				if(posX >= box.minX && posX <= box.maxX && posZ >= box.minZ && posZ <= box.maxZ) {
					for(int y = minY; y <= maxY; y++) {
						int posY = getYWithOffset(y);

						world.setBlock(posX, posY, posZ, block, 0, 2);
					}
				}
			}
		}
	}

	@Override
	protected void fillWithMetadataBlocks(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block block, int meta, Block replaceBlock, int replaceMeta, boolean onlyReplace) {

		if(getYWithOffset(minY) < box.minY || getYWithOffset(maxY) > box.maxY)
			return;

		for(int x = minX; x <= maxX; x++) {

			for(int z = minZ; z <= maxZ; z++) {
				int posX = getXWithOffset(x, z);
				int posZ = getZWithOffset(x, z);

				if(posX >= box.minX && posX <= box.maxX && posZ >= box.minZ && posZ <= box.maxZ) {
					for(int y = minY; y <= maxY; y++) {
						int posY = getYWithOffset(y);

						if(!onlyReplace || world.getBlock(posX, posY, posZ).getMaterial() != Material.air) {
							if(x != minX && x != maxX && y != minY && y != maxY && z != minZ && z != maxZ)
								world.setBlock(posX, posY, posZ, replaceBlock, replaceMeta, 2);
							else
								world.setBlock(posX, posY, posZ, block, meta, 2);
						}
					}
				}
			}
		}
	}

	protected void fillWithMetadataBlocks(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block block, int meta) {

		if(getYWithOffset(minY) < box.minY || getYWithOffset(maxY) > box.maxY)
			return;

		for(int x = minX; x <= maxX; x++) {

			for(int z = minZ; z <= maxZ; z++) {
				int posX = getXWithOffset(x, z);
				int posZ = getZWithOffset(x, z);

				if(posX >= box.minX && posX <= box.maxX && posZ >= box.minZ && posZ <= box.maxZ) {
					for(int y = minY; y <= maxY; y++) {
						int posY = getYWithOffset(y);

						world.setBlock(posX, posY, posZ, block, meta, 2);
					}
				}
			}
		}
	}

	@Override
	protected void fillWithRandomizedBlocks(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean onlyReplace, Random rand, BlockSelector selector) {

		if(getYWithOffset(minY) < box.minY || getYWithOffset(maxY) > box.maxY)
			return;

		for(int x = minX; x <= maxX; x++) {

			for(int z = minZ; z <= maxZ; z++) {
				int posX = getXWithOffset(x, z);
				int posZ = getZWithOffset(x, z);

				if(posX >= box.minX && posX <= box.maxX && posZ >= box.minZ && posZ <= box.maxZ) {
					for(int y = minY; y <= maxY; y++) {
						int posY = getYWithOffset(y);

						if(!onlyReplace || world.getBlock(posX, posY, posZ).getMaterial() != Material.air) {
							selector.selectBlocks(rand, posX, posY, posZ, x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ);
							world.setBlock(posX, posY, posZ, selector.func_151561_a(), selector.getSelectedBlockMetaData(), 2);
						}
					}
				}
			}
		}
	}

	protected void fillWithRandomizedBlocks(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Random rand, BlockSelector selector) {

		if(getYWithOffset(minY) < box.minY || getYWithOffset(maxY) > box.maxY)
			return;

		for(int x = minX; x <= maxX; x++) {

			for(int z = minZ; z <= maxZ; z++) {
				int posX = getXWithOffset(x, z);
				int posZ = getZWithOffset(x, z);

				if(posX >= box.minX && posX <= box.maxX && posZ >= box.minZ && posZ <= box.maxZ) {
					for(int y = minY; y <= maxY; y++) {
						int posY = getYWithOffset(y);

						selector.selectBlocks(rand, posX, posY, posZ, false);
						world.setBlock(posX, posY, posZ, selector.func_151561_a(), selector.getSelectedBlockMetaData(), 2);
					}
				}
			}
		}
	}

	protected void fillWithRandomizedBlocksMeta(World world, StructureBoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Random rand, BlockSelector selector, int meta) {

		if(getYWithOffset(minY) < box.minY || getYWithOffset(maxY) > box.maxY)
			return;

		for(int x = minX; x <= maxX; x++) {

			for(int z = minZ; z <= maxZ; z++) {
				int posX = getXWithOffset(x, z);
				int posZ = getZWithOffset(x, z);

				if(posX >= box.minX && posX <= box.maxX && posZ >= box.minZ && posZ <= box.maxZ) {
					for(int y = minY; y <= maxY; y++) {
						int posY = getYWithOffset(y);

						selector.selectBlocks(rand, posX, posY, posZ, false);
						world.setBlock(posX, posY, posZ, selector.func_151561_a(), meta | selector.getSelectedBlockMetaData(), 2);
					}
				}
			}
		}
	}

	@Override
	protected void randomlyFillWithBlocks(World world, StructureBoundingBox box, Random rand, float randLimit, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block block, Block replaceBlock, boolean onlyReplace) {

		if(getYWithOffset(minY) < box.minY || getYWithOffset(maxY) > box.maxY)
			return;

		for(int x = minX; x <= maxX; x++) {

			for(int z = minZ; z <= maxZ; z++) {
				int posX = getXWithOffset(x, z);
				int posZ = getZWithOffset(x, z);

				if(posX >= box.minX && posX <= box.maxX && posZ >= box.minZ && posZ <= box.maxZ) {
					for(int y = minY; y <= maxY; y++) {
						int posY = getYWithOffset(y);

						if(rand.nextFloat() <= randLimit && (!onlyReplace || world.getBlock(posX, posY, posZ).getMaterial() != Material.air)) {
							if(x != minX && x != maxX && y != minY && y != maxY && z != minZ && z != maxZ)
								world.setBlock(posX, posY, posZ, replaceBlock, 0, 2);
							else
								world.setBlock(posX, posY, posZ, block, 0, 2);
						}
					}
				}
			}
		}
	}

	protected void randomlyFillWithBlocks(World world, StructureBoundingBox box, Random rand, float randLimit, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block block) {

		if(getYWithOffset(minY) < box.minY || getYWithOffset(maxY) > box.maxY)
			return;

		for(int x = minX; x <= maxX; x++) {

			for(int z = minZ; z <= maxZ; z++) {
				int posX = getXWithOffset(x, z);
				int posZ = getZWithOffset(x, z);

				if(posX >= box.minX && posX <= box.maxX && posZ >= box.minZ && posZ <= box.maxZ) {
					for(int y = minY; y <= maxY; y++) {
						int posY = getYWithOffset(y);

						if(rand.nextFloat() <= randLimit)
							world.setBlock(posX, posY, posZ, block, 0, 2);
					}
				}
			}
		}
	}

	protected void randomlyFillWithBlocks(World world, StructureBoundingBox box, Random rand, float randLimit, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block block, int meta) {

		if(getYWithOffset(minY) < box.minY || getYWithOffset(maxY) > box.maxY)
			return;

		for(int x = minX; x <= maxX; x++) {

			for(int z = minZ; z <= maxZ; z++) {
				int posX = getXWithOffset(x, z);
				int posZ = getZWithOffset(x, z);

				if(posX >= box.minX && posX <= box.maxX && posZ >= box.minZ && posZ <= box.maxZ) {
					for(int y = minY; y <= maxY; y++) {
						int posY = getYWithOffset(y);

						if(rand.nextFloat() <= randLimit)
							world.setBlock(posX, posY, posZ, block, meta, 2);
					}
				}
			}
		}
	}

	protected ForgeDirection getDirection(ForgeDirection dir) {
		switch(coordBaseMode) {
		default:
			return dir;
		case 1:
			return dir.getRotation(ForgeDirection.UP);
		case 2:
			return dir.getOpposite();
		case 3:
			return dir.getRotation(ForgeDirection.DOWN);
		}
	}

	protected void placeCore(World world, StructureBoundingBox box, Block block, ForgeDirection dir, int x, int y, int z) {
		int posX = getXWithOffset(x, z);
		int posZ = getZWithOffset(x, z);
		int posY = getYWithOffset(y);

		if(!box.isVecInside(posX, posY, posZ)) return;

		if(dir == null)
			dir = ForgeDirection.NORTH;

		dir = getDirection(dir.getOpposite());
		world.setBlock(posX, posY, posZ, block, dir.ordinal() + 10, 2);
	}

	protected void fillSpace(World world, StructureBoundingBox box, int x, int y, int z, int[] dim, Block block, ForgeDirection dir) {

		if(getYWithOffset(y - dim[1]) < box.minY || getYWithOffset(y + dim[0]) > box.maxY)
			return;

		if(dir == null)
			dir = ForgeDirection.NORTH;

		dir = getDirection(dir.getOpposite());

		int count = 0;

		int[] rot = World.rotateDimensions(dim,dir);

		int posX = getXWithOffset(x, z);
		int posZ = getZWithOffset(x, z);
		int posY = getYWithOffset(y);

		world.beginSafeRemoval();

		for(int a = posX - rot[4]; a <= posX + rot[5]; a++) {
			for(int c = posZ - rot[2]; c <= posZ + rot[3]; c++) {

				if(a >= box.minX && a <= box.maxX && c >= box.minZ && c <= box.maxZ) {
					for(int b = posY - rot[1]; b <= posY + rot[0]; b++) {

						int meta = 0;

						if(b < posY) {
							meta = ForgeDirection.DOWN.ordinal();
						} else if(b > posY) {
							meta = ForgeDirection.UP.ordinal();
						} else if(a < posX) {
							meta = ForgeDirection.WEST.ordinal();
						} else if(a > posX) {
							meta = ForgeDirection.EAST.ordinal();
						} else if(c < posZ) {
							meta = ForgeDirection.NORTH.ordinal();
						} else if(c > posZ) {
							meta = ForgeDirection.SOUTH.ordinal();
						} else {
							continue;
						}

						world.setBlock(a, b, c, block, meta, 2);

						count++;

						if(count > 2000) {
							System.out.println("component's fillspace: ded " + a + " " + b + " " + c + " " + x + " " + y + " " + z);

							world.endSafeRemoval();
							return;
						}
					}
				}
			}
		}

		world.endSafeRemoval();
	}

		public void makeExtra(World world, StructureBoundingBox box, Block block, int x, int y, int z) {
			int posX = getXWithOffset(x, z);
			int posZ = getZWithOffset(x, z);
			int posY = getYWithOffset(y);

			if(!box.isVecInside(posX, posY, posZ))
				return;

			if(!world.sameBlock(posX,posY,posZ,block))
				return;

			int meta = world.getBlockMetadata(posX, posY, posZ);

			if(meta > 5)
				return;

			world.beginSafeRemoval();
			world.setBlock(posX, posY, posZ, block, meta + 6, 3);
			world.endSafeRemoval();
		}

	static class Sandstone extends StructureComponent.BlockSelector {

		Sandstone() { }

		@Override
		public void selectBlocks(Random rand, int posX, int posY, int posZ, boolean notInterior) {
			float chance = rand.nextFloat();

			if(chance > 0.6F) {
				this.field_151562_a = Blocks.sandstone;
			} else if (chance < 0.5F ) {
				this.field_151562_a = ModBlocks.reinforced_sand;
			} else {
				this.field_151562_a = Blocks.sand;
			}
		}
	}

	static class ConcreteBricks extends StructureComponent.BlockSelector {

		ConcreteBricks() { }

		@Override
		public void selectBlocks(Random rand, int posX, int posY, int posZ, boolean notInterior) {
			float chance = rand.nextFloat();

			if(chance < 0.4F) {
				this.field_151562_a = ModBlocks.brick_concrete;
			} else if (chance < 0.7F) {
				this.field_151562_a = ModBlocks.brick_concrete_mossy;
			} else if (chance < 0.9F) {
				this.field_151562_a = ModBlocks.brick_concrete_cracked;
			} else {
				this.field_151562_a = ModBlocks.brick_concrete_broken;
			}
		}
	}

	static class ConcreteBricksStairs extends StructureComponent.BlockSelector {

		ConcreteBricksStairs() {
			this.selectedBlockMetaData = 0;
		}

		@Override
		public void selectBlocks(Random rand, int posX, int posY, int posZ, boolean notInterior) {
			float chance = rand.nextFloat();

			if(chance < 0.4F) {
				this.field_151562_a = ModBlocks.brick_concrete_stairs;
			} else if (chance < 0.7F) {
				this.field_151562_a = ModBlocks.brick_concrete_mossy_stairs;
			} else if (chance < 0.9F) {
				this.field_151562_a = ModBlocks.brick_concrete_cracked_stairs;
			} else {
				this.field_151562_a = ModBlocks.brick_concrete_broken_stairs;
			}
		}
	}

	static class ConcreteBricksSlabs extends StructureComponent.BlockSelector {

		ConcreteBricksSlabs() {
			this.field_151562_a = ModBlocks.concrete_brick_slab;
			this.selectedBlockMetaData = 0;
		}

		@Override
		public void selectBlocks(Random rand, int posX, int posY, int posZ, boolean notInterior) {
			float chance = rand.nextFloat();

			if (chance >= 0.4F && chance < 0.7F) {
				this.selectedBlockMetaData |= 1;
			} else if (chance < 0.9F) {
				this.selectedBlockMetaData |= 2;
			} else {
				this.selectedBlockMetaData |= 3;
			}
		}
	}

	static class LabTiles extends StructureComponent.BlockSelector {

		LabTiles() { }

		@Override
		public void selectBlocks(Random rand, int posX, int posY, int posZ, boolean notInterior) {
			float chance = rand.nextFloat();

			if(chance < 0.5F) {
				this.field_151562_a = ModBlocks.tile_lab;
			} else if (chance < 0.9F) {
				this.field_151562_a = ModBlocks.tile_lab_cracked;
			} else {
				this.field_151562_a = ModBlocks.tile_lab_broken;
			}
		}
	}

	static class SuperConcrete extends StructureComponent.BlockSelector {

		SuperConcrete() {
			this.field_151562_a = ModBlocks.concrete_super;
		}

		@Override
		public void selectBlocks(Random rand, int posX, int posY, int posZ, boolean notInterior) {
			this.selectedBlockMetaData = rand.nextInt(6) + 10;
		}
	}

	public static class MeteorBricks extends BlockSelector {

		@Override
		public void selectBlocks(Random rand, int posX, int posY, int posZ, boolean notInterior) {
			float chance = rand.nextFloat();

			if(chance < 0.4F) {
				this.field_151562_a = ModBlocks.meteor_brick;
			} else if (chance < 0.7F) {
				this.field_151562_a = ModBlocks.meteor_brick_mossy;
			} else {
				this.field_151562_a = ModBlocks.meteor_brick_cracked;
			}
		}

	}

	public static class SupplyCrates extends BlockSelector {

		@Override
		public void selectBlocks(Random rand, int posX, int posY, int posZ, boolean notInterior) {
			float chance = rand.nextFloat();

			if(chance < 0.6F) {
				this.field_151562_a = Blocks.air;
			} else if(chance < 0.8F) {
				this.field_151562_a = ModBlocks.crate_ammo;
			} else if(chance < 0.9F) {
				this.field_151562_a = ModBlocks.crate_can;
			} else {
				this.field_151562_a = ModBlocks.crate;
			}
		}

	}

	public static class CrabSpawners extends BlockSelector {

		@Override
		public void selectBlocks(Random rand, int posX, int posY, int posZ, boolean notInterior) {
			float chance = rand.nextFloat();

			if(chance < 0.8F) {
				this.field_151562_a = ModBlocks.meteor_brick;
			} else {
				this.field_151562_a = ModBlocks.meteor_spawner;
			}
		}

	}

	public static class GreenOoze extends BlockSelector {

		@Override
		public void selectBlocks(Random rand, int posX, int posY, int posZ, boolean notInterior) {
			float chance = rand.nextFloat();

			if(chance < 0.8F) {
				this.field_151562_a = ModBlocks.toxic_block;
			} else {
				this.field_151562_a = ModBlocks.meteor_polished;
			}
		}

	}

}
