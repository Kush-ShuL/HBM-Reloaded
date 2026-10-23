package com.hbm.machine.reactor.zirnox;
import com.hbm.api.computer.*;
import com.hbm.block.fluid.LegacyMaterials;
import com.hbm.energy.NodeConnection;
import com.hbm.fluid.*;
import com.hbm.fluid.network.*;
import com.hbm.machine.reactor.*;
import com.hbm.multiblock.DummyableBlock;
import com.hbm.registry.HbmContent;
import java.util.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
/** Original24-slot Zirnox, including slot-order depletion/breeding, CO2 pressure and non-stoichiometric source steam clipping. */
public final class ZirnoxBlockEntity extends ReactorInventory implements StandardFluidTransceiver {
    public static final int MAX_HEAT=100000,MAX_PRESSURE=100000;
    public int heat,pressure,output;public boolean redstonePowered,isOn,tilted;
    public final FluidTank steam=new FluidTank(HbmFluids.SUPERHOTSTEAM,8000),carbonDioxide=new FluidTank(HbmFluids.CARBONDIOXIDE,16000),water=new FluidTank(HbmFluids.WATER,32000);
    private int rorSlot,tiltBlocksChecked,tiltBlocksValid;private FluidPorts fluidPorts;
    private static final int[] ACCESS=java.util.stream.IntStream.range(0,24).toArray();
    private static final int[][] NEIGHBORS={{1,7},{0,2,8},{1,9},{4,10},{3,5,11},{4,6,12},{5,13},{0,8,14},{1,7,9,15},{2,8,16},{3,11,17},{4,10,12,18},{5,11,13,19},{6,12,20},{7,15,21},{8,14,16,22},{9,15,23},{10,18},{11,17,19},{12,18,20},{13,19},{14,22},{15,21,23},{16,22}};
    public ZirnoxBlockEntity(BlockPos pos,BlockState state){super(ZirnoxContent.REACTOR_ENTITY.get(),pos,state,28);}
    @Override protected String nameKey(){return "container.zirnox";}
    @Override public AbstractContainerMenu createMenu(int id,Inventory inventory,Player player){return new ZirnoxMenu(id,inventory,this,worldPosition);}
    public static void tick(Level level,BlockPos pos,BlockState state,ZirnoxBlockEntity reactor){if(level instanceof ServerLevel server)reactor.serverTick(server);}
    public void setRedstonePowered(boolean powered){if(!powered&&redstonePowered)isOn=false;redstonePowered=powered;setChanged();}
    private void serverTick(ServerLevel server){
        checkFoundation(server);updatePorts(server);if(redstonePowered)isOn=true;output=0;
        carbonDioxide.loadTank(24,26,slots);water.loadTank(25,27,slots);
        if(isOn)for(int slot=0;slot<24;slot++){var stack=slots[slot];if(stack.isEmpty())continue;if(stack.getItem() instanceof ZirnoxRod)decay(slot);else {var source=net.minecraft.resources.ResourceLocation.parse("hbm:meteorite_sword_bred");var target=net.minecraft.resources.ResourceLocation.parse("hbm:meteorite_sword_irradiated");if(net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(source)&&net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(target)&&stack.is(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(source)))slots[slot]=new ItemStack(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(target));}}
        pressure=carbonDioxide.getFill()*2+(int)((float)heat*((float)carbonDioxide.getFill()/carbonDioxide.getMaxFill()));
        if(heat>0&&heat<MAX_HEAT){if(water.getFill()>0&&carbonDioxide.getFill()>0&&steam.getFill()<steam.getMaxFill()){generateSteam();heat-=(int)((float)heat*pressure/1000000F);}else heat-=10;if(server.getGameTime()%100==0)com.hbm.satellite.SatelliteRayEvents.reportEvent(server,worldPosition.getX(),worldPosition.getY(),worldPosition.getZ(),com.hbm.satellite.SatelliteRayEvents.RayEvent.INFO_NUCLEAR,200);}
        if(pressure>MAX_PRESSURE||heat>MAX_HEAT){meltdown(server);return;}
        setChanged();var packet=getUpdatePacket();for(var player:server.players())if(player.distanceToSqr(worldPosition.getX(),worldPosition.getY(),worldPosition.getZ())<22500)player.connection.send(packet);
    }
    private void generateSteam(){if(heat<=10256)return;int cycle=(int)((((float)heat-10256F)/MAX_HEAT)*Math.min((float)carbonDioxide.getFill()/14000F,1F)*25F*7.5F);output=cycle;water.setFill(Math.max(water.getFill()-cycle,0));steam.setFill(Math.min(steam.getFill()+cycle,steam.getMaxFill()));}
    private boolean fuel(int slot){return slots[slot].getItem() instanceof ZirnoxRod rod&&!rod.type(slots[slot]).breeding;}
    private void decay(int slot){int decay=0;for(int neighbor:NEIGHBORS[slot])if(fuel(neighbor))decay++;var rod=(ZirnoxRod)slots[slot].getItem();var type=rod.type(slots[slot]);if(!type.breeding)decay++;for(int i=0;i<decay;i++){heat+=type.heat;ZirnoxRod.increment(slots[slot]);if(ZirnoxRod.life(slots[slot])>type.maxLife){slots[slot]=ZirnoxContent.spent(type);break;}}}
    private void updatePorts(ServerLevel server){if(fluidPorts==null){var facing=DummyableBlock.facing(getBlockState());var positions=ZirnoxBlock.fluidPorts(worldPosition,facing);var side=ZirnoxBlock.rotation(facing);var connections=new NodeConnection[4];for(int i=0;i<4;i++){var normal=i<2?side:side.getOpposite();connections[i]=new NodeConnection(positions[i].relative(normal),normal);}fluidPorts=new FluidPorts(positions,connections);}fluidPorts.update(server,this);}
    private void disconnect(){if(level instanceof ServerLevel server&&fluidPorts!=null)fluidPorts.remove(server);fluidPorts=null;}
    @Override public void onChunkUnloaded(){disconnect();super.onChunkUnloaded();}@Override public void setRemoved(){disconnect();super.setRemoved();}
    private void checkFoundation(ServerLevel server){if(!ZirnoxConfig.gravity()){tilted=false;return;}int identity=(worldPosition.getY()+worldPosition.getZ()*27644437)*27644437+worldPosition.getX();if((server.getGameTime()+identity)%20!=0)return;if(tiltBlocksChecked>=9){boolean before=tilted;tilted=tiltBlocksValid<tiltBlocksChecked*.95;if(tilted&&!before)server.playSound(null,worldPosition,com.hbm.registry.HbmSounds.get("block.metalImpact"),SoundSource.BLOCKS,3,1);tiltBlocksChecked=tiltBlocksValid=0;setChanged();}var pos=worldPosition.offset(-2+(tiltBlocksChecked/3)*2,-1,-2+(tiltBlocksChecked%3)*2);var state=server.getBlockState(pos);tiltBlocksChecked++;var material=LegacyMaterials.of(state).name();if(state.isFaceSturdy(server,pos,Direction.UP)&&state.isSolidRender(server,pos)&&!Set.of("sand","cloth","ground").contains(material)&&state.getBlock().getExplosionResistance()>=Blocks.STONE.getExplosionResistance())tiltBlocksValid++;}
    private void meltdown(ServerLevel server){Arrays.fill(slots,ItemStack.EMPTY);ZirnoxFailure.meltdown(this,server);}
    public void control(boolean toggle,boolean vent){if(toggle&&!redstonePowered)isOn=!isOn;if(vent)carbonDioxide.setFill(Math.max(carbonDioxide.getFill()-1000,0));setChanged();}
    @Override public FluidTank[] getAllTanks(){return new FluidTank[]{water,steam,carbonDioxide};}@Override public FluidTank[] getReceivingTanks(){return new FluidTank[]{water,carbonDioxide};}@Override public FluidTank[] getSendingTanks(){return new FluidTank[]{steam};}
    @Override public long getProviderSpeed(HbmFluidType type,int pressure){return tilted?0:1000000;}@Override public long getReceiverSpeed(HbmFluidType type,int pressure){return tilted?0:1000000;}
    @Override public long transferFluid(HbmFluidType type,int pressure,long amount){long rejected=StandardFluidTransceiver.super.transferFluid(type,pressure,amount);if(rejected!=amount)setChanged();return rejected;}
    @Override public void useUpFluid(HbmFluidType type,int pressure,long amount){StandardFluidTransceiver.super.useUpFluid(type,pressure,amount);if(amount>0)setChanged();}
    @Override public List<TankView> tanks(){return Arrays.stream(getAllTanks()).map(tank->new TankView(tank.getTankType(),tank.getFill(),tank.getMaxFill())).toList();}
    @Override public List<ProgressView> progressBars(){return List.of(new ProgressView("gui.hbm.reactor.heat",heat,MAX_HEAT,0xED7139),new ProgressView("gui.hbm.zirnox.pressure",pressure,MAX_PRESSURE,0xC65F5F));}
    @Override public int[] getSlotsForFace(Direction side){return ACCESS;}@Override public boolean canPlaceItem(int slot,ItemStack stack){return slot<24&&stack.getItem() instanceof ZirnoxRod;}@Override public boolean canTakeItemThroughFace(int slot,ItemStack stack,Direction side){return slot<24&&!(stack.getItem() instanceof ZirnoxRod);}
    @Override protected void writeState(CompoundTag tag){tag.putInt("heat",heat);tag.putInt("pressure",pressure);tag.putBoolean("isOn",isOn);tag.putBoolean("redstonePowered",redstonePowered);tag.putBoolean("tilted",tilted);steam.writeToNBT(tag,"steam");carbonDioxide.writeToNBT(tag,"carbondioxide");water.writeToNBT(tag,"water");}
    @Override protected void readState(CompoundTag tag){heat=tag.getInt("heat");pressure=tag.getInt("pressure");isOn=tag.getBoolean("isOn");redstonePowered=tag.getBoolean("redstonePowered");tilted=tag.getBoolean("tilted");steam.readFromNBT(tag,"steam");carbonDioxide.readFromNBT(tag,"carbondioxide");water.readFromNBT(tag,"water");}
    public long temperature(){return Math.round(heat*1E-5D*780+20);}public long bars(){return Math.round(pressure*1E-5D*30);}
    @Override public String computerType(){return "zirnox_reactor";}@Override public String[] computerMethods(){return new String[]{"getTemp","getPressure","getWater","getSteam","getCarbonDioxide","ventCarbonDioxide","getFuel","isActive","setActive","getInfo"};}
    @Override public Object[] callComputer(String method,ComputerArguments args){return switch(method){case "getTemp"->new Object[]{temperature()};case "getPressure"->new Object[]{bars()};case "getWater"->new Object[]{water.getFill()};case "getSteam"->new Object[]{steam.getFill()};case "getCarbonDioxide"->new Object[]{carbonDioxide.getFill()};case "ventCarbonDioxide"->{int amount=Mth.clamp(args.count()>0?args.checkInteger(0):1000,0,carbonDioxide.getMaxFill());carbonDioxide.setFill(Math.max(carbonDioxide.getFill()-amount,0));setChanged();yield new Object[]{};}case "getFuel"->{int slot=args.checkInteger(0);if(slot>=0&&slot<24&&fuel(slot)){var type=((ZirnoxRod)slots[slot].getItem()).type(slots[slot]);yield new Object[]{type.name(),ZirnoxRod.life(slots[slot]),type.maxLife};}yield new Object[]{"",0,0};}case "isActive"->new Object[]{isOn};case "setActive"->{isOn=args.checkBoolean(0);setChanged();yield new Object[]{};}case "getInfo"->new Object[]{temperature(),bars(),water.getFill(),steam.getFill(),carbonDioxide.getFill(),isOn};default->throw new ComputerException("No such method");};}
    /** Exact source ROR value names, retained for the actual radio-controller module. */
    public String readIrOr(String name){return switch(name){case "heat"->Long.toString(temperature());case "pressure"->Long.toString(bars());case "water"->Integer.toString(water.getFill());case "steam"->Integer.toString(steam.getFill());case "co2"->Integer.toString(carbonDioxide.getFill());case "state"->isOn?"1":"0";case "slottype"->fuel(rorSlot)?((ZirnoxRod)slots[rorSlot].getItem()).type(slots[rorSlot]).name():"";case "slotmaxdep"->fuel(rorSlot)?Integer.toString(((ZirnoxRod)slots[rorSlot].getItem()).type(slots[rorSlot]).maxLife):null;case "slotdep"->fuel(rorSlot)?Integer.toString(ZirnoxRod.life(slots[rorSlot])):null;default->null;};}
    public Object invokeIrOr(String name,String value){if(name.equals("ventco2")){control(false,true);return null;}if(redstonePowered)return null;try{int val=Integer.parseInt(value);if(name.equals("setslot")&&val>=0&&val<24)rorSlot=val;if(name.equals("setstate")){isOn=val==1;setChanged();}}catch(NumberFormatException ignored){}return null;}
}
