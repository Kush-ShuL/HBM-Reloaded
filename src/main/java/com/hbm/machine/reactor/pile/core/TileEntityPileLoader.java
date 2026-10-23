package com.hbm.machine.reactor.pile.core;
import com.hbm.machine.reactor.pile.*;import com.hbm.api.computer.*;import api.hbm.redstoneoverradio.IRORValueProvider;import io.netty.buffer.ByteBuf;import net.minecraft.core.*;import net.minecraft.core.registries.BuiltInRegistries;import net.minecraft.nbt.*;import net.minecraft.util.Mth;import net.minecraft.world.*;import net.minecraft.world.entity.player.Player;import net.minecraft.world.item.*;import net.minecraft.world.level.block.entity.BlockEntity;import net.minecraft.world.level.block.state.BlockState;import com.hbm.registry.HbmSounds;import com.hbm.machine.reactor.pile.core.TileEntityPileCore.PileChannel;
public class TileEntityPileLoader extends TileEntityPileDeviceBase implements WorldlyContainer, IRORValueProvider, ComputerControl {
	
	public TileEntityPileLoader(BlockPos pos,BlockState state){super(PileCoreContent.LOADER.get(),pos,state);}
	public double syncLevel;
	public double level;
	public double lastLevel;
	
	public int turnProgress;
	
	public static final double SPEED = 1D / 7D;
	
	public boolean loading = false;
	public int delay = 0;
	public ItemStack syncStack;
	public ItemStack stack;
	public boolean wasRedstone;
	
	public ItemStack channelStack;
	public double channelDepletion;
	public double channelTemp;

	@Override
	public void updateEntity() {
		
		if(!worldObj.isRemote) {
			
			PileDirection dir = getOrientation();
			PileChannel fuelChan = null;
			this.channelStack = null;
			this.channelDepletion = 0D;
			this.channelTemp = 0D;
			
			int x = xCoord - dir.offsetX;
			int y = yCoord;
			int z = zCoord - dir.offsetZ;
			
			if(worldObj.getBlock(x, y, z) == PileCoreContent.BODY.get() && worldObj.getBlockMetadata(x, y, z) == PileBodyBlock.META_FUEL_IN) {
				BlockEntity tile = worldObj.getTileEntity( x, y, z);
				
				if(tile instanceof TileEntityPileBaseMK2) {
					TileEntityPileBaseMK2 pile = (TileEntityPileBaseMK2) tile;
					TileEntityPileCore core = pile.getCore();
					
					if(core != null) {
						fuelChan = core.getFuelChannel(x, y, z);
						
						if(fuelChan != null) {
							this.chanNum = core.getFuelChannelNum(fuelChan);
							this.channelStack = fuelChan.rods[fuelChan.rods.length - 1];
							this.channelDepletion = PileFuelItem.getDepletionPercent(channelStack);
							this.channelTemp = fuelChan.heat;
						}
					}
				}
			}
			
			boolean redstone = worldObj.getIndirectPowerOutput(xCoord + dir.offsetX, yCoord, zCoord + dir.offsetZ, dir.getOpposite().ordinal());
			if(redstone && !wasRedstone && this.delay <= 0 && this.level <= 0) this.loading = true;
			this.wasRedstone = redstone;
			
			if(this.delay > 0) {
				this.delay--;
			} else {
				
				if(loading) {
					
					if(this.level == 0) worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, HbmSounds.get("weapon.reload.boltOpen"), this.getVolume(1F), 1F);
					
					this.level += SPEED;
					if(this.level >= 1D) {
						this.level = 1D;
						this.loading = false;
						this.delay = 5;
					}
				} else {
					
					if(this.level == 1) {
						worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, HbmSounds.get("weapon.reload.boltOpen"), this.getVolume(1F), 0.75F);
						if(fuelChan != null) {
							fuelChan.loadItem(stack);
							this.setInventorySlotContents(0, null);
						}
					}
					
					if(this.level > 0D) {
						this.level -= SPEED;
						if(this.level < 0D) this.level = 0D;
					}
				}
			}
			
			this.networkPackNT(35);
            this.setChanged();
			
		} else {

			this.lastLevel = this.level;

			if(this.turnProgress > 0) {
				this.level = this.level + ((this.syncLevel - this.level) / (double) this.turnProgress);
				--this.turnProgress;
			} else {
				this.level = this.syncLevel;
			}
		}
	}

	@Override
	public void serialize(ByteBuf buf) {
		super.serialize(buf);
		buf.writeDouble(this.level);
		
		if(this.stack != null) {
			buf.writeInt(BuiltInRegistries.ITEM.getId(this.stack.getItem()));
			buf.writeShort(PileFuelItem.metadata(this.stack));
		} else {
			buf.writeInt(-1);
		}
		
		if(this.channelStack != null) {
			buf.writeInt(BuiltInRegistries.ITEM.getId(this.channelStack.getItem()));
			buf.writeShort(PileFuelItem.metadata(this.channelStack));
		} else {
			buf.writeInt(-1);
		}
		
		buf.writeDouble(this.channelDepletion);
		buf.writeDouble(this.channelTemp);
	}

	@Override
	public void deserialize(ByteBuf buf) {
		super.deserialize(buf);
		double lastSync = this.syncLevel;
		this.syncLevel = buf.readDouble();
		
		int itemId = buf.readInt();
		if(itemId != -1) this.syncStack = syncedStack(itemId,buf.readShort());
		else this.syncStack = null;
		
		int chanId = buf.readInt();
		if(chanId != -1) this.channelStack = syncedStack(chanId,buf.readShort());
		else this.channelStack = null;
		
		this.channelDepletion = buf.readDouble();
		this.channelTemp = buf.readDouble();

		if(this.syncLevel != lastSync) this.turnProgress = 2;
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		this.loading = nbt.getBoolean("loading");
		this.level = nbt.getDouble("level");
		this.delay = nbt.getInt("delay");
		this.wasRedstone = nbt.getBoolean("redstone");
		
		if(nbt.contains("stack")) {
			this.stack = readStack(nbt.getCompound("stack"));
		}
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);

		nbt.putBoolean("loading", loading);
		nbt.putDouble("level", level);
		nbt.putInt("delay", delay);
		nbt.putBoolean("wasRedstone", wasRedstone);
		
		if(this.stack != null) {
			CompoundTag stackTag = new CompoundTag();
			stackTag.merge(saveStack(this.stack));
			nbt.put("stack", stackTag);
		}
	}
	
	public static boolean isItemLoadable(ItemStack stack){return stack!=null&&!stack.isEmpty()&&stack.getItem()==PileFuelContent.ITEM.get();}
 public void setInventorySlotContents(int slot,ItemStack stack){this.stack=stack==null||stack.isEmpty()?null:stack;setChanged();}
 @Override public int getContainerSize(){return 1;}@Override public boolean isEmpty(){return stack==null;}@Override public ItemStack getItem(int slot){return stack==null?ItemStack.EMPTY:stack;}
 @Override public ItemStack removeItem(int slot,int amount){if(amount==1&&stack!=null){var ret=stack.copy();setInventorySlotContents(0,null);return ret;}return ItemStack.EMPTY;}
 @Override public ItemStack removeItemNoUpdate(int slot){return ItemStack.EMPTY;}@Override public void setItem(int slot,ItemStack stack){setInventorySlotContents(slot,stack);}@Override public int getMaxStackSize(){return 1;}@Override public boolean stillValid(Player player){return false;}@Override public void clearContent(){setInventorySlotContents(0,null);}
 @Override public int[] getSlotsForFace(Direction side){return new int[]{0};}@Override public boolean canPlaceItem(int slot,ItemStack stack){return isItemLoadable(stack);}@Override public boolean canPlaceItemThroughFace(int slot,ItemStack stack,Direction side){return isItemLoadable(stack);}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return false;}
 private static ItemStack syncedStack(int id,int meta){var stack=new ItemStack(BuiltInRegistries.ITEM.byId(id),1);stack.set(com.hbm.hazard.HbmHazards.LEGACY_METADATA.get(),meta);return stack;}

	@Override
	public String[] getFunctionInfo() {
		return new String[] {
				PREFIX_VALUE + "meta",
				PREFIX_VALUE + "depletion",
				PREFIX_VALUE + "deppercent",
				PREFIX_VALUE + "lifetime",
				PREFIX_VALUE + "temp",
		};
	}

	@Override
	public String provideRORValue(String name) {
		
		if(name.equals(PREFIX_VALUE + "meta")) {
			return this.channelStack == null ? "-1" : PileFuelItem.metadata(this.channelStack) + "";
		}
		if(name.equals(PREFIX_VALUE + "deppercent")) {
			return "" + (int) Math.round(this.channelDepletion);
		}
		if(name.equals(PREFIX_VALUE + "depletion")) {
			if(this.channelStack == null) return "0";
			return "" + (int) Math.round(PileFuelItem.getDepletionPercent(this.channelStack));
		}
		if(name.equals(PREFIX_VALUE + "lifetime")) {
			if(this.channelStack == null) return "0";
			PileFuelType rod = PileFuelItem.type(this.channelStack);
			return "" + (int) Math.round(rod.life);
		}
		if(name.equals(PREFIX_VALUE + "temp")) {
			return "" + (int) Math.round(this.channelTemp);
		}
		
		return null;
	}

 @Override public String computerType(){return "ntm_pile_loader";}
 @Override public String[] computerMethods(){return new String[]{"getTemp","getDepletion","getLifetime","getType","getLoadingType","isLoading","load"};}
 @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getTemp"->new Object[]{channelTemp};case "getDepletion"->new Object[]{channelStack==null?0:channelDepletion};case "getLifetime"->new Object[]{channelStack==null?0:PileFuelItem.type(channelStack).life};case "getType"->new Object[]{channelStack==null?-1:PileFuelItem.metadata(channelStack)};case "getLoadingType"->new Object[]{stack==null?-1:PileFuelItem.metadata(stack)};case "isLoading"->new Object[]{loading};case "load"->{if(delay<=0&&level<=0)loading=true;setChanged();yield new Object[]{};}default->throw new ComputerException("Unknown method "+method);};}
}
