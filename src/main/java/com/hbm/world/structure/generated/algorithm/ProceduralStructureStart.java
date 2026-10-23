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
/** Supplied X5846 algorithm; only native-version API boundary adapted. Source: legacy/src/main/java/com/hbm/world/gen/ProceduralStructureStart.java */
public class ProceduralStructureStart extends StructureStart {

	public List<StructureComponent> queuedComponents = new ArrayList();

	protected List<Weight> componentWeightList;

	public ProceduralStructureStart() { }

	public ProceduralStructureStart(int chunkX, int chunkZ) {
		super(chunkX, chunkZ);
	}

	public ProceduralStructureStart buildStart(World world, Random rand, StructureComponent starter, Weight...weights) {
		prepareWeights(weights);

		components.add(starter);
		queuedComponents.add(starter);
		while(!queuedComponents.isEmpty()) {
			final int i = rand.nextInt(queuedComponents.size());
			StructureComponent component = queuedComponents.remove(i);
			if(component instanceof ProceduralComponent)
				((ProceduralComponent) component).buildComponent(this, rand);
		}

		this.updateBoundingBox();
		return this;
	}

	public void prepareWeights(Weight...weights) {
		componentWeightList = new ArrayList(weights.length);

		for(int i = 0; i < weights.length; i++) {
			weights[i].instancesSpawned = 0;
			componentWeightList.add(weights[i]);
		}
	}

	@Override
	public void func_143017_b(CompoundTag nbt) {

	}

	@Override
	public void func_143022_a(CompoundTag nbt) {

	}

	protected int getTotalWeight() {
		boolean flag = false;
		int totalWeight = 0;
		Weight weight;

		for(Iterator iterator = componentWeightList.iterator(); iterator.hasNext(); totalWeight += weight.weight) {
			weight = (Weight) iterator.next();

			if(weight.instanceLimit >= 0 && weight.instancesSpawned < weight.instanceLimit)
				flag = true;
		}

		return flag ? totalWeight : -1;
	}

	protected StructureComponent getWeightedComponent(StructureComponent last, Random rand, int minX, int minY, int minZ, int coordMode, int componentType) {
		int totalWeight = getTotalWeight();

		if(totalWeight < 0)
			return null;

		for(int i = 0; i < 5; i++) {
			int value = rand.nextInt(totalWeight);
			Iterator iterator = componentWeightList.iterator();

			while(iterator.hasNext()) {
				Weight weight = (Weight)iterator.next();
				value -= weight.weight;

				if(value < 0) {
					if(!weight.canSpawnStructure(componentType, coordMode, last))
						break;

					StructureComponent component = weight.lambda.findValidPlacement(components, rand, minX, minY, minZ, coordMode, componentType);

					if(component != null) {
						weight.instancesSpawned++;

						if(!weight.canSpawnMoreStructures())
							componentWeightList.remove(weight);

						return component;
					}

				}
			}
		}

		return null;
	}

	protected int sizeLimit = 50;
	protected int distanceLimit = 64;

	protected StructureComponent getNextValidComponent(StructureComponent last, Random rand, int minX, int minY, int minZ, int coordMode, int componentType) {

		if(components.size() > sizeLimit)
			return null;

		if(Math.abs(minX - (func_143019_e() * 16 + 8)) <= distanceLimit && Math.abs(minZ - (func_143018_f() * 16 + 8)) <= distanceLimit) {

			StructureComponent structure = getWeightedComponent(last, rand, minX, minY, minZ, coordMode, componentType + 1);

			if(structure != null) {
				this.components.add(structure);
				this.queuedComponents.add(structure);
			}

			return structure;
		}

		return null;
	}

	public static StructureBoundingBox getComponentToAddBoundingBox(int posX, int posY, int posZ, int offsetX, int offsetY, int offsetZ, int maxX, int maxY, int maxZ, int coordMode) {
		switch(coordMode) {
		default:
		case 0: return new StructureBoundingBox(posX + offsetX, posY + offsetY, posZ + offsetZ, posX + maxX - 1 + offsetX, posY + maxY - 1 + offsetY, posZ + maxZ - 1 + offsetZ);
		case 1: return new StructureBoundingBox(posX - maxZ + 1 - offsetZ, posY + offsetY, posZ + offsetX, posX - offsetZ, posY + maxY - 1 + offsetY, posZ + maxX - 1 + offsetX);
		case 2: return new StructureBoundingBox(posX - maxX + 1 - offsetX, posY + offsetY, posZ - maxZ + 1 - offsetZ, posX - offsetX, posY + maxY - 1 + offsetY, posZ + offsetZ);
		case 3: return new StructureBoundingBox(posX + offsetZ, posY + offsetY, posZ - maxX + 1 - offsetX, posX + maxZ - 1 + offsetZ, posY + maxY - 1 + offsetY, posZ - offsetX);
		}
	}

	public static interface ProceduralComponent {

		public default void buildComponent(ProceduralStructureStart start, Random rand) { }

		public default StructureComponent getNextComponentNormal(ProceduralStructureStart start, StructureComponent caller, int coordMode, Random rand, int offset, int offsetY) {
			StructureBoundingBox box = caller.getBoundingBox();
			switch(coordMode) {
			case 0: return start.getNextValidComponent(caller, rand, box.minX + offset, box.minY + offsetY, box.maxZ + 1, coordMode, caller.getComponentType());
			case 1: return start.getNextValidComponent(caller, rand, box.minX - 1, box.minY + offsetY, box.minZ + offset, coordMode, caller.getComponentType());
			case 2: return start.getNextValidComponent(caller, rand, box.maxX - offset, box.minY + offsetY, box.minZ - 1, coordMode, caller.getComponentType());
			case 3: return start.getNextValidComponent(caller, rand, box.maxX + 1, box.minY + offsetY, box.maxZ - offset, coordMode, caller.getComponentType());
			default: return null;
			}
		}

		public default StructureComponent getNextComponentAntiNormal(ProceduralStructureStart start, StructureComponent caller, int coordMode, Random rand, int offset, int offsetY) {
			StructureBoundingBox box = caller.getBoundingBox();
			switch(coordMode) {
			case 0: return start.getNextValidComponent(caller, rand, box.maxX - offset, box.minY + offsetY, box.minZ - 1, 2, caller.getComponentType());
			case 1: return start.getNextValidComponent(caller, rand, box.maxX + 1, box.minY + offsetY, box.maxZ - offset, 3, caller.getComponentType());
			case 2: return start.getNextValidComponent(caller, rand, box.minX + offset, box.minY + offsetY, box.maxZ + 1, 0, caller.getComponentType());
			case 3: return start.getNextValidComponent(caller, rand, box.minX - 1, box.minY + offsetY, box.minZ + offset, 1, caller.getComponentType());
			default: return null;
			}
		}

		public default StructureComponent getNextComponentWest(ProceduralStructureStart start, StructureComponent caller, int coordMode, Random rand, int offset, int offsetY) {
			StructureBoundingBox box = caller.getBoundingBox();
			switch(coordMode) {
			case 0: return start.getNextValidComponent(caller, rand, box.minX - 1, box.minY + offsetY, box.minZ + offset, 1, caller.getComponentType());
			case 1: return start.getNextValidComponent(caller, rand, box.maxX - offset, box.minY + offsetY, box.minZ - 1, 2, caller.getComponentType());
			case 2: return start.getNextValidComponent(caller, rand, box.maxX + 1, box.minY + offsetY, box.maxZ - offset, 3, caller.getComponentType());
			case 3: return start.getNextValidComponent(caller, rand, box.minX + offset, box.minY + offsetY, box.maxZ + 1, 0, caller.getComponentType());
			default: return null;
			}
		}

		public default StructureComponent getNextComponentEast(ProceduralStructureStart start, StructureComponent caller, int coordMode, Random rand, int offset, int offsetY) {
			StructureBoundingBox box = caller.getBoundingBox();
			switch(coordMode) {
			case 0: return start.getNextValidComponent(caller, rand, box.maxX + 1, box.minY + offsetY, box.maxZ - offset, 3, caller.getComponentType());
			case 1: return start.getNextValidComponent(caller, rand, box.minX + offset, box.minY + offsetY, box.maxZ + 1, 0, caller.getComponentType());
			case 2: return start.getNextValidComponent(caller, rand, box.minX - 1, box.minY + offsetY, box.minZ + offset, 1, caller.getComponentType());
			case 3: return start.getNextValidComponent(caller, rand, box.maxX - offset, box.minY + offsetY, box.minZ - 1, 2, caller.getComponentType());
			default: return null;
			}
		}
	}

	@FunctionalInterface
	protected static interface instantiateStructure {
		StructureComponent findValidPlacement(List components, Random rand, int minX, int minY, int minZ, int coordMode, int componentType);
	}

	protected static class Weight {

		public final instantiateStructure lambda;

		public final int weight;
		public int instancesSpawned;
		public int instanceLimit;

		public Weight(int weight, int limit, instantiateStructure lambda) {
			this.weight = weight;
			this.instanceLimit = limit;
			this.lambda = lambda;
		}

		public boolean canSpawnStructure(int componentAmount, int coordMode, StructureComponent component) {
			return this.instanceLimit < 0 || this.instancesSpawned < this.instanceLimit;
		}

		public boolean canSpawnMoreStructures() {
			return this.instanceLimit < 0 || this.instancesSpawned < this.instanceLimit;
		}

	}
}
