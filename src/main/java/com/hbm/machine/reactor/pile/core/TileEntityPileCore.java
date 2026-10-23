package com.hbm.machine.reactor.pile.core;
import java.util.*;
import com.hbm.machine.reactor.pile.PileFuelItem;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.weapon.sedna.BulletConfig;
import com.hbm.particle.LegacyFluidEffects;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
public class TileEntityPileCore extends PileTickingBlockEntity {
	
	/*
	 *  ____,__,___
	 * |\   I, I,   \
	 * | \   I  I    \
	 * -- \___________\
	 * |--|           |
	 * -- |  U  U  U  |
	 * \--|     X     |
	 *  \ |  U  U  U  |
	 *   \|___________|
	 *   
	 *       BEHOLD
	 *      THE CUBE
	 */
	
	/**
	 * Orientation is kind of important now. The "direction" the pile is facing in is now decided
	 * by the side the core is on, i.e. the side it was assembled from. Any channel that goes along
	 * with the orientation is automatically considered a fuel channel. Any channel perpendicular
	 * to the orientation is for ventilation. Vertical channels are always for control rods.
	 */
	public TileEntityPileCore(BlockPos pos,BlockState state){super(PileCoreContent.CORE.get(),pos,state);}
	public PileOrientation orientation = PileOrientation.NEITHER;
	
	public int height;
	public int width;
	public int depth;

	public List<PileChannel> fuelChannels = new ArrayList();
	public List<PileChannel> ventilationChannels = new ArrayList();
	public List<PileChannel> controlChannels = new ArrayList();
	
	public int left;
	public int right;
	public int up;
	/**
	 * Segments are fuel and control channels grouped by vertical slices viewed from the front, this makes the simulation part easier.
	 */
	public PileSegment[] segments = new PileSegment[0];
	
	public double highestHeat;
	public static final int MAX_HEAT = 800;
	public static boolean meltingDown = false;
	
	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		height = nbt.getInt("height");
		width = nbt.getInt("width");
		depth = nbt.getInt("depth");
		left = nbt.getInt("left");
		right = nbt.getInt("right");
		up = nbt.getInt("up");
		
		orientation = PileOrientation.values()[Math.abs(nbt.getInt("orientation") % PileOrientation.values().length)];
		
		segments = new PileSegment[width];

		fuelChannels.clear();
		ventilationChannels.clear();
		controlChannels.clear();
		
		int fuelCount = nbt.getByte("fc");
		int ventCount = nbt.getByte("vc");
		int contCount = nbt.getByte("cc");

		for(int i = 0; i < fuelCount; i++) fuelChannels.add(readChannelFromNBT(nbt, "f" + i));
		for(int i = 0; i < ventCount; i++) ventilationChannels.add(readChannelFromNBT(nbt, "v" + i));
		for(int i = 0; i < contCount; i++) controlChannels.add(readChannelFromNBT(nbt, "c" + i));
		
		this.recalculateSegments();
	}
	
	@Override
	public void writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);
		nbt.putInt("height", height);
		nbt.putInt("width", width);
		nbt.putInt("depth", depth);
		nbt.putInt("left", left);
		nbt.putInt("right", right);
		nbt.putInt("up", up);
		
		nbt.putInt("orientation", orientation.ordinal());
		
		int fuelCount = fuelChannels.size();
		int ventCount = ventilationChannels.size();
		int contCount = controlChannels.size();

		nbt.putByte("fc", (byte) fuelCount);
		nbt.putByte("vc", (byte) ventCount);
		nbt.putByte("cc", (byte) contCount);

		for(int i = 0; i < fuelCount; i++) fuelChannels.get(i).writeChannelToNBT(nbt, "f" + i);
		for(int i = 0; i < ventCount; i++) ventilationChannels.get(i).writeChannelToNBT(nbt, "v" + i);
		for(int i = 0; i < contCount; i++) controlChannels.get(i).writeChannelToNBT(nbt, "c" + i);
	}

	public PileChannel getFuelChannel(int x, int y, int z) { return getChannel(x, y, z, fuelChannels); }
	public PileChannel getVentilationChannel(int x, int y, int z) { return getChannel(x, y, z, ventilationChannels); }
	public PileChannel getControlChannel(int x, int y, int z) { return getChannel(x, y, z, controlChannels); }
	
	public PileChannel getChannel(int x, int y, int z, List<PileChannel> list) {
		for(PileChannel channel : list) if(channel.entry.compare(x, y, z)) return channel;
		return null;
	}

	public int getFuelChannelNum(PileChannel chan) { return getChannelNum(chan, fuelChannels); }
	public int getVentilationChannelNum(PileChannel chan) { return getChannelNum(chan, ventilationChannels); }
	public int getControlChannelNum(PileChannel chan) { return getChannelNum(chan, controlChannels); }
	
	public int getChannelNum(PileChannel chan, List<PileChannel> list) {
		return list.indexOf(chan); // more reliable when channels change and not that big of a deal because we have lists in single digit length
	}
	
	public TileEntityPileCore setupSize(int up, int down, int l, int r, int d) {
		this.height = up + 1 + down;
		this.width = l + 1 + r;
		this.depth = d;
		this.left = l;
		this.right = r;
		this.up = up;
		this.segments = new PileSegment[width];
		return this;
	}
	
	public List<PileChannel> getChannelList(PileChannelType type) {
		if(type == PileChannelType.FUEL) return this.fuelChannels;
		if(type == PileChannelType.VENTILATION) return this.ventilationChannels;
		return this.controlChannels;
	}
	
	public boolean drillChannel(int x, int y, int z, PileDirection dir, Player player) {
		int startMeta = worldObj.getBlockMetadata(x, y, z);
		PileChannelType type = PileChannelType.getChannelType(dir, orientation);
		
		int size =
				type == PileChannelType.CONTROL ? height :
				type == PileChannelType.FUEL ? depth : width;
		
		List<PileChannel> list = getChannelList(type);
		
		if(startMeta == PileBodyBlock.META_FUEL_IN || startMeta == PileBodyBlock.META_AIR_IN || startMeta == PileBodyBlock.META_CONTROL) {
			for(int i = 0; i < list.size(); i++) {
				PileChannel chan = list.get(i);
				if(chan.entry.compare(x, y, z) && chan.entry.getDir() == dir) {
					if(chan.type == chan.type.FUEL) chan.ejectAll();
					list.remove(i);
					for(int j = 0; j < size; j++) {
						int iX = x + dir.offsetX * j;
						int iY = y + dir.offsetY * j;
						int iZ = z + dir.offsetZ * j;
						worldObj.setBlockMetadataWithNotify(iX, iY, iZ, PileBodyBlock.META_DUMMY, 3);
					}
					worldObj.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, SoundEvents.ITEM_BREAK, 1F, 0.75F);
					recalculateSegments();
					return true;
				}
			}
		}
		
		boolean error = false;
		for(int i = 0; i < size; i++) {
			int iX = x + dir.offsetX * i;
			int iY = y + dir.offsetY * i;
			int iZ = z + dir.offsetZ * i;
			if(worldObj.getBlock(iX, iY, iZ) != PileCoreContent.BODY.get()) { PileErrors.send(worldObj.nativeLevel, iX, iY, iZ, "foreign", player); error = true; }
			int meta = worldObj.getBlockMetadata(iX, iY, iZ);
			if(meta == PileBodyBlock.META_EDGE) { PileErrors.send(worldObj.nativeLevel, iX, iY, iZ, "edge", player); error = true; }
			else if(meta == PileBodyBlock.META_CORE) { PileErrors.send(worldObj.nativeLevel, iX, iY, iZ, "core", player); error = true; }
			else if(meta == PileBodyBlock.META_CHANNEL) { PileErrors.send(worldObj.nativeLevel, iX, iY, iZ, "channel", player); error = true; }
			else if(meta != PileBodyBlock.META_DUMMY) { PileErrors.send(worldObj.nativeLevel, iX, iY, iZ, "io", player); error = true; }
		}
		
		if(error) return false;
		
		for(int i = 0; i < size; i++) {
			int iX = x + dir.offsetX * i;
			int iY = y + dir.offsetY * i;
			int iZ = z + dir.offsetZ * i;
			if(i == 0) {
				if(type == PileChannelType.FUEL) worldObj.setBlockMetadataWithNotify(iX, iY, iZ, PileBodyBlock.META_FUEL_IN, 3);
				if(type == PileChannelType.VENTILATION) worldObj.setBlockMetadataWithNotify(iX, iY, iZ, PileBodyBlock.META_AIR_IN, 3);
				if(type == PileChannelType.CONTROL) worldObj.setBlockMetadataWithNotify(iX, iY, iZ, PileBodyBlock.META_CONTROL, 3);
			} else if(i == size - 1) {
				if(type == PileChannelType.FUEL) worldObj.setBlockMetadataWithNotify(iX, iY, iZ, PileBodyBlock.META_FUEL_OUT, 3);
				if(type == PileChannelType.VENTILATION) worldObj.setBlockMetadataWithNotify(iX, iY, iZ, PileBodyBlock.META_AIR_OUT, 3);
				if(type == PileChannelType.CONTROL) worldObj.setBlockMetadataWithNotify(iX, iY, iZ, PileBodyBlock.META_CONTROL, 3);
			} else {
				worldObj.setBlockMetadataWithNotify(iX, iY, iZ, PileBodyBlock.META_CHANNEL, 3);
			}
		}
		
		list.add(new PileChannel(x, y, z, dir, size, type));

		worldObj.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, SoundEvents.ITEM_BREAK, 1F, 1.25F);
		this.markChanged();
		
		recalculateSegments();
		
		return true;
	}

	@Override
	public void updateEntity() {
		
		if(!worldObj.isRemote) {
			
			this.runSimulation();
			this.handleVentilation();
			this.handleMeltdown();
			
			this.networkPackNT(25);
            this.setChanged();
		}
	}
	
	public void ejectAll() {
		for(PileChannel chan : this.fuelChannels) chan.ejectAll();
	}

	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeDouble(this.highestHeat);
	}

	@Override
	public void deserialize(ByteBuf buf) {
		super.deserialize(buf);
		this.highestHeat = buf.readDouble();
	}
	
	protected void runSimulation() {
		
		// reaction
		for(PileChannel chan : this.fuelChannels) {
			if(chan.length <= 0) continue;
			double producedNeutrons = 0;
			
			for(int i = 0; i < chan.rods.length; i++) {
				ItemStack stack = chan.rods[i];
				if(stack != null && stack.getItem() instanceof PileFuelItem) {
					double neut = PileFuelItem.getReactivity(stack, chan.incomingNeutrons / chan.length);
					producedNeutrons += neut;
					chan.heat += neut * PileFuelItem.getHeatPerNeutron(stack);
					chan.rods[i] = PileFuelItem.react(stack, neut);
				}
			}
			chan.outgoingNeutrons = producedNeutrons;
			chan.incomingNeutrons = 0;
		}
		
		// intra-segment propagation
		for(PileSegment seg : this.segments) {
			if(seg == null || seg.segType != seg.segType.FUEL) continue;
			double outgoing = 0D;
			for(PileChannel chan : seg.channels) outgoing += chan.outgoingNeutrons;
			for(PileChannel chan : seg.channels) chan.incomingNeutrons += outgoing;
		}
		
		// inter-segment propagation
		for(int i = 1; i < this.segments.length - 1; i++) { // bound chosen because edges can't have channels
			PileSegment seg = this.segments[i];
			if(seg == null || seg.segType != seg.segType.FUEL) continue;
			double outgoing = 0D;
			for(PileChannel chan : seg.channels) outgoing += chan.outgoingNeutrons;
			
			double mult = 1D;
			for(int j = i - 1; j >= 1; j--) { // center to left
				PileSegment neighbor = this.segments[j];
				if(neighbor == null) continue;
				mult *= neighbor.getNeutronMult(this);
				if(neighbor.segType == neighbor.segType.FUEL) {
					for(PileChannel chan : neighbor.channels) chan.incomingNeutrons += outgoing * mult;
				}
			}
			
			mult = 1D;
			for(int j = i + 1; j < this.segments.length - 1; j++) { // center to right
				PileSegment neighbor = this.segments[j];
				if(neighbor == null) continue;
				mult *= neighbor.getNeutronMult(this);
				if(neighbor.segType == neighbor.segType.FUEL) {
					for(PileChannel chan : neighbor.channels) chan.incomingNeutrons += outgoing * mult;
				}
			}
		}
	}
	
	protected void handleVentilation() {
		
		for(PileChannel chan : this.ventilationChannels) {
			if(chan.air <= 0) continue;
			
			double airCap = (double) chan.air / (double) chan.MAX_AIR;
			
			for(PileChannel fuel : this.fuelChannels) {
				if(Math.abs(fuel.entry.getY() - chan.entry.getY()) <= 1) {
					fuel.heat *= (1D - airCap * 0.05D); // channel can hold up to 1000mB, turning into a 0.95 heat mult for connected fuel channels
				}
			}
			
			int toUse = (int) Math.ceil(airCap * 5D);
			chan.air -= toUse;
			
			if(worldObj.getTotalWorldTime() % 3 != 0) continue;

			double x = chan.entry.getX() + 0.5 + chan.entry.getDir().offsetX * (this.width - 0.375);
			double y = chan.entry.getY() + 0.5;
			double z = chan.entry.getZ() + 0.5 + chan.entry.getDir().offsetZ * (this.width - 0.375);
			var rand = worldObj.rand;
			
			LegacyFluidEffects.tower(getLevel(),x,y,z,new LegacyFluidEffects.Tower(1F,(.125F+rand.nextFloat()*.125F)*(float)airCap,(float)airCap,20+rand.nextInt(30)).strafe(.0025F).withoutWind().color(0xa0a0a0));
		}
		
		for(PileChannel chan : this.fuelChannels) {
			chan.heat *= 0.999;
			if(chan.heat < 20) chan.heat = 20;
		}
	}
	
	protected void handleMeltdown() {
		
		this.highestHeat = 0;
		for(PileChannel chan : this.fuelChannels) {
			if(chan.heat > this.highestHeat) this.highestHeat = chan.heat;
		}
		
		if(this.highestHeat > this.MAX_HEAT) {
			this.destroy();
			double avgX = 0;
			double avgZ = 0;
			for(PileChannel chan : this.fuelChannels) {
				avgX += chan.entry.getX() + 0.5 + chan.entry.getDir().offsetX * (chan.length - 1) / 2D;
				avgZ += chan.entry.getZ() + 0.5 + chan.entry.getDir().offsetZ * (chan.length - 1) / 2D;
			}
			avgX /= this.fuelChannels.size();
			avgZ /= this.fuelChannels.size();
			meltingDown = true;
			worldObj.newExplosion(null, avgX, yCoord + up, avgZ, 15F, true, true);
			meltingDown= false;
			
			for(int i = 0; i < 15; i++) {
				double mY = worldObj.rand.nextDouble() * 0.5 + 1D;
				EntityBulletBaseMK4 fragment = new EntityBulletBaseMK4(getLevel(), null, pile_debris, 100F, 0.35F, avgX, yCoord + up + 1, avgZ, 0, mY, 0);
				worldObj.spawnEntityInWorld(fragment);
			}
		}
	}
	
	protected void recalculateSegments() {
		this.segments = new PileSegment[width];
		
		for(PileChannel chan : fuelChannels) {
			int index = getChannelVerticalIndex(chan);
			if(index < 0 || index >= this.segments.length) continue;
			
			if(this.segments[index] == null) {
				this.segments[index] = new PileSegment(PileChannelType.FUEL).addChan(chan);
			} else {
				if(this.segments[index].segType == PileChannelType.FUEL) this.segments[index].addChan(chan);
			}
		}
		
		for(PileChannel chan : controlChannels) {
			int index = getChannelVerticalIndex(chan);
			if(index < 0 || index >= this.segments.length) continue;
			
			if(this.segments[index] == null) {
				this.segments[index] = new PileSegment(PileChannelType.CONTROL).addChan(chan);
			} else {
				if(this.segments[index].segType == PileChannelType.CONTROL) this.segments[index].addChan(chan);
			}
		}
	}
	
	/**
	 * Splits the entire pile into vertical slices, left to right, and returns the index of which slice a channel belongs to.
	 * Since this left-to-right approach assumes the front of the pile, this only is used for fuel channels and control rods.
	 */
	protected int getChannelVerticalIndex(PileChannel chan) {
		PileDirectedPos pos = chan.entry;
		PileDirection right = pos.getDir().getRotation(PileDirection.UP);
		int deltaX = (pos.getX() - xCoord) * right.offsetX;
		int deltaZ = (pos.getZ() - zCoord) * right.offsetZ;
		int abs = deltaX == 0 ? deltaZ : deltaX;
		int index = abs + this.left;
		return index;
	}
	
	public void destroy() {
		worldObj.setBlock(xCoord, yCoord, zCoord, PileCoreContent.BRICK.get());
	}
	
	/**
	 * The orientation of the pile, based on the direction the core is pointing in.
	 * Channels that follow the orientation are fuel, perpendicular ones are for ventilation.
	 */
	public static enum PileOrientation {
		NORTH_SOUTH,
		EAST_WEST,
		NEITHER;
		
		public static PileOrientation getOrientation(PileDirection dir) {
			if(dir == PileDirection.NORTH || dir == PileDirection.SOUTH) return NORTH_SOUTH;
			if(dir == PileDirection.EAST || dir == PileDirection.WEST) return EAST_WEST;
			return NEITHER;
		}
	}
	
	/**
	 * A channel, represents any type, therefore has the data of all three types.
	 * Could have been an abstract class + three child classes but then we'd just be overcomplicating it.
	 */
	public class PileChannel { // originally called "PileHole", however i didn't want it sounding too sexual
		
		public final PileDirectedPos entry; // first channel block plus direction it faces inward
		public final int length; // length of the channel, always equal to the h/w/d size of that axis
		public final PileChannelType type;
		
		public final ItemStack[] rods;
		public double heat = 0D;
		public double outgoingNeutrons = 0D;
		public double incomingNeutrons = 0D;
		public static final int MAX_AIR = 1_000;
		public int air;
		public double control = 1D; // pulled out by default, unlike me who never pulls out
		
		public PileChannel(int x, int y, int z, PileDirection dir) {
			this.entry = new PileDirectedPos(x, y, z, dir);
			this.type = PileChannelType.getChannelType(dir, orientation);
			this.length =
					type == PileChannelType.CONTROL ? height :
					type == PileChannelType.FUEL ? depth : width;

			this.rods = new ItemStack[length];
		}
		
		public PileChannel(int x, int y, int z, PileDirection dir, int length, PileChannelType type) {
			this.entry = new PileDirectedPos(x, y, z, dir);
			this.type = type;
			this.length = length;
			this.rods = new ItemStack[length];
		}
		
		public void writeChannelToNBT(CompoundTag nbt, String name) {
			nbt.putInt(name + "_x", entry.getX());
			nbt.putInt(name + "_y", entry.getY());
			nbt.putInt(name + "_z", entry.getZ());
			nbt.putByte(name + "_d", (byte) entry.getDir().ordinal());
			
			if(type == type.FUEL) {
				ListTag list = new ListTag();
				for(int i = 0; i < rods.length; i++) {
					if(rods[i] != null) {
						CompoundTag nbt1 = new CompoundTag();
						nbt1.putByte("slot", (byte) i);
						nbt1.merge(saveStack(rods[i]));
						list.add(nbt1);
					}
				}
				nbt.put(name + "items", list);

				nbt.putDouble(name + "heat", heat);
				nbt.putDouble(name + "neutrons", incomingNeutrons);
			}
			
			if(type == type.VENTILATION) {
				nbt.putInt(name + "air", air);
			}
			
			if(type == type.CONTROL) {
				nbt.putDouble(name + "control", control);
			}
		}
		
		public void loadItem(ItemStack stack) {
			if(stack == null) return;
			if(rods.length <= 0) { dropItem(stack, -1); return; }
			
			for(int i = 0; i < rods.length; i++) {
				if(rods[i] == null) {
					rods[i] = stack;
					return;
					
				} else {
					ItemStack prev = rods[i];
					rods[i] = stack;
					stack = prev;
				}
			}
			
			if(stack != null) dropItem(stack, length);
		}
		
		public void ejectAll() {
			for(int i = 0; i < this.rods.length; i++) {
				this.dropItem(rods[i], length);
				this.rods[i] = null;
			}
		}
		
		public void dropItem(ItemStack stack, int depth) {
			if(stack == null) return;
			int x = entry.getX() + entry.getDir().offsetX * depth;
			int y = entry.getY();
			int z = entry.getZ() + entry.getDir().offsetZ * depth;
			
			if(stack.has(DataComponents.CUSTOM_DATA)) {var data=stack.get(DataComponents.CUSTOM_DATA).copyTag();data.remove(PileFuelItem.KEY_NBT_DEPLETION);if(data.isEmpty())stack.remove(DataComponents.CUSTOM_DATA);else stack.set(DataComponents.CUSTOM_DATA,CustomData.of(data));}

			ItemEntity item = new ItemEntity(getLevel(), x + 0.5, y + 0.5, z + 0.5, stack);
			worldObj.spawnEntityInWorld(item);
		}
	}
	
	public PileChannel readChannelFromNBT(CompoundTag nbt, String name) {
		int x = nbt.getInt(name + "_x");
		int y = nbt.getInt(name + "_y");
		int z = nbt.getInt(name + "_z");
		PileDirection dir = PileDirection.getOrientation(nbt.getByte(name + "_d"));
		
		PileChannel chan = new PileChannel(x, y, z, dir);
		
		if(chan.type == chan.type.FUEL) {
			ListTag list = nbt.getList(name + "items", 10);
			for(int i = 0; i < list.size(); i++) {
				CompoundTag nbt1 = list.getCompound(i);
				byte b0 = nbt1.getByte("slot");
				if(b0 >= 0 && b0 < chan.rods.length) {
					chan.rods[b0] = readStack(nbt1);
				}
			}

			chan.heat = nbt.getDouble(name + "heat");
			chan.incomingNeutrons = nbt.getDouble(name + "neutrons");
		}
		
		if(chan.type == chan.type.VENTILATION) {
			chan.air = nbt.getInt(name + "air");
		}
		
		if(chan.type == chan.type.CONTROL) {
			chan.control = nbt.getDouble(name + "control");
		}
		
		return chan;
	}
	
	public static enum PileChannelType {
		FUEL, VENTILATION, CONTROL;
		
		public static PileChannelType getChannelType(PileDirection channelDir, PileOrientation pileOrientation) {
			
			if(channelDir == PileDirection.UP || channelDir == PileDirection.DOWN) {
				return PileChannelType.CONTROL;
			} else if(PileOrientation.getOrientation(channelDir) == pileOrientation) {
				return PileChannelType.FUEL;
			} else {
				return PileChannelType.VENTILATION;
			}
		}
	}
	
	/** A vertical "slice" of the front of the reactor, can include multiple channels per slice,
	 * due to reactor geometry constraints, all channels are of the same type. */
	public static class PileSegment {
		
		public List<PileChannel> channels = new ArrayList();
		public final PileChannelType segType;
		
		public PileSegment(PileChannelType segType) {
			this.segType = segType;
		}
		
		public PileSegment addChan(PileChannel chan) {
			this.channels.add(chan);
			return this;
		}
		
		public double getNeutronMult(TileEntityPileCore core) {
			if(this.segType != this.segType.CONTROL) return 1D;
			int size = core.depth - 1; // minus one to make the calculation easier, a xixix pattern becomes xixi which means the rods cover 50%
			if(size < 3) return 0D; // reactors this small can't exist, nor can they have control rods
			double total = 0D;
			
			for(PileChannel chan : channels) total += chan.control;
			return Mth.clamp(total / size, 0D, 0.5D);
		}
	}
	
	public static final BulletConfig pile_debris = new BulletConfig("hbm:pile_debris").setLife(200).setVel(1F).setGrav(.1D).setOnUpdate(bullet->{if(bullet.level().isClientSide)PileCoreContent.CLIENT_FIRE.accept(bullet);}).setOnImpact((bullet,hit)->{bullet.level().explode(bullet,bullet.getX(),bullet.getY(),bullet.getZ(),5F,true,Level.ExplosionInteraction.NONE);bullet.discard();});
}
