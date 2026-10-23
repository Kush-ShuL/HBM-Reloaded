package com.hbm.machine.reactor.pile.core;
import com.hbm.machine.reactor.pile.*;import com.hbm.api.computer.*;import api.hbm.redstoneoverradio.IRORValueProvider;import io.netty.buffer.ByteBuf;import net.minecraft.core.*;import net.minecraft.core.registries.BuiltInRegistries;import net.minecraft.nbt.*;import net.minecraft.util.Mth;import net.minecraft.world.*;import net.minecraft.world.entity.player.Player;import net.minecraft.world.item.*;import net.minecraft.world.level.block.entity.BlockEntity;import net.minecraft.world.level.block.state.BlockState;import com.hbm.registry.HbmSounds;import com.hbm.machine.reactor.pile.core.TileEntityPileCore.PileChannel;
public class TileEntityPileControl extends TileEntityPileDeviceBase implements api.hbm.redstoneoverradio.IRORInfo, ComputerControl {
	
	public TileEntityPileControl(BlockPos pos,BlockState state){super(PileCoreContent.CONTROL.get(),pos,state);}
	public double syncLevel;
	public double level;
	public double lastLevel;
	
	public int turnProgress;
	
	public double targetLevel;
	public static final double SPEED = 1D / 60D; // full traverse takes 3s
	public boolean wasRedstone;
	
	@Override
	public void updateEntity() {
		
		if(!worldObj.isRemote) {
			
			boolean canMove = false;
			
			if(worldObj.getBlock(xCoord, yCoord - 1, zCoord) == PileCoreContent.BODY.get() && worldObj.getBlockMetadata(xCoord, yCoord - 1, zCoord) == PileBodyBlock.META_CONTROL) {

				BlockEntity tile = worldObj.getTileEntity( xCoord, yCoord - 1, zCoord);
				
				if(tile instanceof TileEntityPileBaseMK2) {
					TileEntityPileBaseMK2 pile = (TileEntityPileBaseMK2) tile;
					TileEntityPileCore core = pile.getCore();
					
					if(core != null) {
						PileChannel controlChan = core.getControlChannel(xCoord, yCoord - 1, zCoord);
						
						if(controlChan != null) {
							canMove = true;
							this.chanNum = core.getControlChannelNum(controlChan);
							controlChan.control = this.level;
						}
					}
				}
			}
			
			if(canMove && this.level != this.targetLevel) {
				if(Math.abs(level - targetLevel) <= SPEED) {
					this.level = this.targetLevel;
				} else if(level < targetLevel) {
					this.level += SPEED;
				} else if(level > targetLevel) {
					this.level -= SPEED;
				}
			}
			
			PileDirection dir = this.getOrientation();
			boolean redstone = worldObj.getIndirectPowerOutput(xCoord + dir.offsetX, yCoord, zCoord + dir.offsetZ, dir.getOpposite().ordinal());

			if(redstone && !wasRedstone) this.setTarget(1D);
			if(!redstone && wasRedstone) this.setTarget(0D);
			
			this.wasRedstone = redstone;
			
			this.networkPackNT(100);
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
	}

	@Override
	public void deserialize(ByteBuf buf) {
		super.deserialize(buf);
		double lastSync = this.syncLevel;
		this.syncLevel = buf.readDouble();

		if(this.syncLevel != lastSync) this.turnProgress = 2;
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		this.level = nbt.getDouble("level");
		this.targetLevel = nbt.getDouble("targetLevel");
		this.wasRedstone = nbt.getBoolean("redstone");
	}

	@Override
	public void writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);
		nbt.putDouble("level", level);
		nbt.putDouble("targetLevel", targetLevel);
		nbt.putBoolean("wasRedstone", wasRedstone);
	}

	@Override
	public String[] getFunctionInfo() {
		return new String[] {
				PREFIX_FUNCTION + "setrods" + "!" + "percent",
				PREFIX_FUNCTION + "extendrods" + "!" + "percent"
		};
	}

	public String runRORFunction(String name, String[] params) {

		if((PREFIX_FUNCTION + "setrods").equals(name) && params.length > 0) {
			int percent = parsePercent(params[0], 0, 100);
			this.setTarget(percent / 100D);
			this.markChanged();
			return null;
		}

		if((PREFIX_FUNCTION + "extendrods").equals(name) && params.length > 0) {
			int percent = parsePercent(params[0], -100, 100);
			this.setTarget(Mth.clamp(this.targetLevel + percent / 100D, 0D, 1D));
			this.markChanged();
			return null;
		}

		return null;
	}

	public void setTarget(double target) {
		this.targetLevel = target;
	}

 private static int parsePercent(String value,int min,int max){int result;try{result=Integer.parseInt(value);}catch(Exception bad){try{result=(int)Math.round(Double.parseDouble(value));}catch(Exception invalid){throw new IllegalArgumentException("Exception: Parameter in Invalid Format");}}if(result<min||result>max)throw new IllegalArgumentException("Exception: Parameter in Invalid Format");return result;}
 @Override public String computerType(){return "ntm_pile_control";}
 @Override public String[] computerMethods(){return new String[]{"getLevel","setLevel"};}
 @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getLevel"->new Object[]{level};case "setLevel"->{setTarget(Mth.clamp(args.checkDouble(0)/100D,0D,1D));setChanged();yield new Object[]{};}default->throw new ComputerException("Unknown method "+method);};}
}
